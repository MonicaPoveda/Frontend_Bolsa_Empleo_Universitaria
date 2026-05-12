package com.example.frontend_bolsa_empleo_universitaria.screens.Login

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.repository.AuthRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.PerfilRepository
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginUiState
import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    val authRepo = remember { AuthRepository(RetrofitClient.usuarioApi) }
    val empresaRepo = remember { EmpresaRepository(RetrofitClient.empresaApi) }
    val perfilRepo = remember { PerfilRepository(RetrofitClient.perfilApi) }

    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(authRepo, token, empresaRepo)
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showRecoverDialog by remember { mutableStateOf(false) }
    var showEmpresaPendienteDialog by remember { mutableStateOf(false) }
    var showEmpresaRechazadaDialog by remember { mutableStateOf(false) }
    var empresaStatusInfo by remember { mutableStateOf<EmpresaStatusInfo?>(null) }

    val uiState by viewModel.uiState.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Estado para verificar perfil después del login
    var verificandoPerfil by remember { mutableStateOf(false) }
    var startAnimation by remember { mutableStateOf(false) }
    val introAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "login_intro_alpha"
    )
    val introOffset by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 28.dp,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "login_intro_offset"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> {
                val successState = uiState as LoginUiState.Success

                // Si es estudiante, verificar si tiene perfil creado
                if (successState.rol == "ESTUDIANTE") {
                    verificandoPerfil = true

                    try {
                        val userId = token.getUserId()
                        val jwtToken = token.getToken()

                        if (userId != null && jwtToken != null) {
                            // Intentar obtener el perfil del usuario
                            val perfil = perfilRepo.obtenerPerfilPorUsuario(userId)

                            if (perfil != null) {
                                // SI TIENE PERFIL: Guardar estado e ir al Home
                                token.setProfileCreated(true)
                                delay(500)
                                navController.navigate("estudiante_home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                // NO TIENE PERFIL (o error de búsqueda): 
                                // Obligado a ver la alerta y crear perfil
                                token.setProfileCreated(false)
                                delay(500)
                                navController.navigate("mensaje_alerta_crear_perfil") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        } else {
                            // Fallback: ir al home de todas formas
                            delay(500)
                            navController.navigate("estudiante_home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    } catch (e: Exception) {
                        // Error al verificar, igual dejar entrar (fallback seguro)
                        delay(500)
                        navController.navigate("estudiante_home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } finally {
                        verificandoPerfil = false
                    }
                } else {
                    // Para EMPRESA o ADMIN, redirigir normalmente
                    delay(500)
                    when (successState.rol) {
                        "EMPRESA" -> navController.navigate("empresa_home") {
                            popUpTo("login") { inclusive = true }
                        }
                        "ADMIN" -> navController.navigate("admin_home") {
                            popUpTo("login") { inclusive = true }
                        }
                        else -> {}
                    }
                }
            }
            is LoginUiState.Error -> {
                val rawError = (uiState as LoginUiState.Error).message
                // Limpiar códigos HTTP y comillas exteriores para que el error sea legible
                val cleanError = rawError
                    .replace(Regex("\\d{3}\\s+[A-Z]+\\s*"), "")
                    .removeSurrounding("\"")
                    .trim()

                errorMessage = cleanError

                // Verificar estados específicos de la empresa
                if (cleanError.contains("PENDIENTE", ignoreCase = true)) {
                    empresaStatusInfo = EmpresaStatusInfo(email = email, mensaje = cleanError)
                    showEmpresaPendienteDialog = true
                    viewModel.startStatusPolling(email, password)
                } else if (cleanError.contains("RECHAZADA", ignoreCase = true)) {
                    empresaStatusInfo = EmpresaStatusInfo(email = email, mensaje = cleanError)
                    showEmpresaRechazadaDialog = true
                }
            }
            else -> {}
        }
    }

    if (showRecoverDialog) {
        RecoverPasswordDialog(
            onDismiss = { showRecoverDialog = false },
            snackbarHostState = snackbarHostState
        )
    }

    if (showEmpresaPendienteDialog && empresaStatusInfo != null) {
        EmpresaPendienteDialog(
            info = EmpresaPendienteInfo(empresaStatusInfo!!.email, empresaStatusInfo!!.mensaje),
            onDismiss = {
                showEmpresaPendienteDialog = false
                empresaStatusInfo = null
                viewModel.stopStatusPolling()
            },
            onNavigateToRegistro = {
                showEmpresaPendienteDialog = false
                viewModel.stopStatusPolling()
                navController.navigate("registro_empresa")
            }
        )
    }

    if (showEmpresaRechazadaDialog && empresaStatusInfo != null) {
        EmpresaRechazadaDialog(
            info = empresaStatusInfo!!,
            onDismiss = {
                showEmpresaRechazadaDialog = false
                empresaStatusInfo = null
            },
            onNavigateToRegistro = {
                showEmpresaRechazadaDialog = false
                navController.navigate("registro_empresa")
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F2D42),
                            Color(0xFF1E5A7A),
                            Color(0xFFEAF3F5)
                        )
                    )
                )
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // Si está verificando perfil, mostrar loading
            if (verificandoPerfil) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .alpha(introAlpha)
                        .offset(y = introOffset),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Verificando tu información...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .alpha(introAlpha)
                        .offset(y = introOffset),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(72.dp),
                            shape = RoundedCornerShape(22.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "UNIEMPLEO",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Bolsa universitaria de oportunidades",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (uiState is LoginUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(30.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        errorMessage?.let {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFEF2F2)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ErrorOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = it,
                                        color = Color(0xFF991B1B),
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; errorMessage = null },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = null },
                            label = { Text("Contraseña") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        TextButton(
                            onClick = { showRecoverDialog = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("¿Olvidaste tu contraseña?")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.login(email, password) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            enabled = uiState !is LoginUiState.Loading && email.isNotBlank() && password.isNotBlank(),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Ingresar", style = MaterialTheme.typography.labelLarge)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            text = "¿No tienes una cuenta?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { navController.navigate("registro_estudiante") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Estudiante")
                            }
                            OutlinedButton(
                                onClick = { navController.navigate("registro_empresa") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Business,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Empresa")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class EmpresaStatusInfo(val email: String, val mensaje: String)

// Diálogo para empresas rechazadas
@Composable
fun EmpresaRechazadaDialog(
    info: EmpresaStatusInfo,
    onDismiss: () -> Unit,
    onNavigateToRegistro: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Cancel, null, tint = Color(0xFFDC2626), modifier = Modifier.size(48.dp)) },
        title = { Text("Solicitud Rechazada", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Tu registro no ha sido aprobado por el administrador.")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Motivo:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Text(
                        text = info.mensaje
                            .replace("RECHAZADA:", "", ignoreCase = true)
                            .replace(Regex("\\d{3}\\s+[A-Z]+\\s*"), "")
                            .removeSurrounding("\"")
                            .trim(),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onNavigateToRegistro) { Text("Intentar nuevo registro") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

// Data class para información de empresa pendiente
data class EmpresaPendienteInfo(
    val email: String,
    val mensaje: String
)

// Diálogo para empresas pendientes de aprobación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpresaPendienteDialog(
    info: EmpresaPendienteInfo,
    onDismiss: () -> Unit,
    onNavigateToRegistro: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Info,
                contentDescription = "Información",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "⏳ Solicitud Pendiente",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Tu solicitud de registro está siendo revisada por el administrador.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📋 Detalles de tu solicitud:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("📧 Correo:", style = MaterialTheme.typography.bodySmall)
                            Text(
                                info.email,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("📊 Estado:", style = MaterialTheme.typography.bodySmall)
                            Surface(
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    "PENDIENTE",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        // Mostrar el comentario detallado del administrador si existe
                        val adminComment = info.mensaje
                            .replace("PENDIENTE:", "", ignoreCase = true)
                            .replace(Regex("\\d{3}\\s+[A-Z]+\\s*"), "")
                            .removeSurrounding("\"")
                            .trim()

                        if (adminComment.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(modifier = Modifier.alpha(0.3f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "💬 Comentario del administrador:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = adminComment,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Info",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "📝 ¿Qué puedes hacer mientras tanto?",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• ✅ Espera a que un administrador revise tu solicitud\n" +
                                    "• 📧 Recibirás un correo cuando sea aprobada\n" +
                                    "• 🔐 Una vez aprobada, podrás iniciar sesión\n" +
                                    "• 📞 Si tienes dudas, contacta al administrador",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "⚠️ Nota: Mientras tu solicitud esté PENDIENTE no podrás iniciar sesión. El administrador te notificará cuando sea APROBADA o RECHAZADA.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Entendido", fontSize = 16.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onNavigateToRegistro,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Intentar otro registro", fontSize = 14.sp)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverPasswordDialog(
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var temporaryPassword by remember { mutableStateOf("") }

    val authRepository = remember { AuthRepository(RetrofitClient.usuarioApi) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onDismiss()
            },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Éxito",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "¡Contraseña Recuperada!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Se ha generado una nueva contraseña para tu cuenta.",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "🔑 CONTRASEÑA TEMPORAL",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = temporaryPassword,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 2.sp
                                    )

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                            val clip = android.content.ClipData.newPlainText("contraseña", temporaryPassword)
                                            clipboard.setPrimaryClip(clip)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("🔐 Contraseña copiada al portapapeles")
                                            }
                                        },
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.ContentCopy,
                                            contentDescription = "Copiar",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Info",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "📝 Instrucciones importantes:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "• Usa esta contraseña para iniciar sesión\n" +
                                        "• Después de iniciar sesión, cámbiala en tu perfil\n" +
                                        "• No compartas esta contraseña con nadie\n" +
                                        "• Puedes copiarla haciendo clic en el ícono 📋",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "⚠️ Recomendación: Cambia esta contraseña temporal después de iniciar sesión por seguridad.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Ir al inicio de sesión", fontSize = 16.sp)
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Recuperar Contraseña") },
            text = {
                Column {
                    Text("Ingresa tu correo para recibir una contraseña temporal.")
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        label = { Text("Correo Electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            val result = authRepository.recuperarPassword(email)

                            result.onSuccess { response ->
                                temporaryPassword = response.passwordTemporal
                                showSuccessDialog = true
                                isLoading = false
                            }.onFailure { error ->
                                errorMessage = when {
                                    error.message?.contains("PENDIENTE") == true ->
                                        "❌ Tu solicitud de registro está pendiente. Espera la aprobación del administrador."
                                    error.message?.contains("no encontrada") == true ->
                                        "❌ No se encontró una cuenta con este correo electrónico"
                                    else -> error.message ?: "Error al recuperar contraseña"
                                }
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading && email.isNotBlank()
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        )
    }
}
