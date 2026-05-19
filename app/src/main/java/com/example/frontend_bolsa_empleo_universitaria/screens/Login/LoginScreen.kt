package com.example.frontend_bolsa_empleo_universitaria.screens.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.repository.AdminRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.AuthRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.PerfilRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoLogo
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginUiState
import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginViewModelFactory

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    val authRepo = remember { AuthRepository(RetrofitClient.usuarioApi) }
    val empresaRepo = remember { EmpresaRepository(RetrofitClient.empresaApi) }
    val perfilRepo = remember { PerfilRepository(RetrofitClient.perfilApi) }
    val adminRepo = remember { AdminRepository(context) }

    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(authRepo, token, empresaRepo, adminRepo)
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

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> {
                val successState = uiState as LoginUiState.Success
                if (successState.rol == "ESTUDIANTE") {
                    try {
                        val userId = token.getUserId()
                        if (userId != null) {
                            val perfil = perfilRepo.obtenerPerfilPorUsuario(userId)
                            if (perfil != null) {
                                token.setProfileCreated(true)
                                navController.navigate("estudiante_home") { popUpTo("login") { inclusive = true } }
                            } else {
                                token.setProfileCreated(false)
                                navController.navigate("mensaje_alerta_crear_perfil") { popUpTo("login") { inclusive = true } }
                            }
                        }
                    } catch (e: Exception) {
                        navController.navigate("estudiante_home") { popUpTo("login") { inclusive = true } }
                    }
                } else {
                    when (successState.rol) {
                        "EMPRESA" -> navController.navigate("empresa_home") { popUpTo("login") { inclusive = true } }
                        "ADMIN" -> navController.navigate("admin_home") { popUpTo("login") { inclusive = true } }
                    }
                }
            }
            is LoginUiState.Error -> {
                val cleanError = (uiState as LoginUiState.Error).message
                    .replace(Regex("\\d{3}\\s+[A-Z]+\\s*"), "")
                    .removeSurrounding("\"")
                    .trim()
                errorMessage = cleanError

                if (cleanError.contains("PENDIENTE", ignoreCase = true) || cleanError.contains("revisión", ignoreCase = true)) {
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

    if (showRecoverDialog) RecoverPasswordDialog(onDismiss = { showRecoverDialog = false }, snackbarHostState = snackbarHostState)

    if (showEmpresaPendienteDialog && empresaStatusInfo != null) {
        EmpresaPendienteDialog(
            info = EmpresaPendienteInfo(empresaStatusInfo!!.email, empresaStatusInfo!!.mensaje),
            onDismiss = {
                showEmpresaPendienteDialog = false
                viewModel.stopStatusPolling()
            }
        )
    }

    if (showEmpresaRechazadaDialog && empresaStatusInfo != null) {
        EmpresaRechazadaDialog(
            info = empresaStatusInfo!!,
            onDismiss = { showEmpresaRechazadaDialog = false },
            onNavigateToRegistro = {
                showEmpresaRechazadaDialog = false
                navController.navigate("registro_empresa?email=${empresaStatusInfo!!.email}")
            }
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, containerColor = Color.Transparent) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(BolsaTokens.Palette.HeaderStart, BolsaTokens.Palette.HeaderEnd, BolsaTokens.Palette.Background)))
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UniEmpleoLogo(
                        modifier = Modifier.size(78.dp),
                        containerColor = BolsaTokens.Palette.Primary.copy(alpha = 0.10f),
                        cornerRadius = 22.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        "UNIEMPLEO",
                        style = MaterialTheme.typography.headlineLarge,
                        color = BolsaTokens.Palette.Primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        "Bolsa universitaria de oportunidades",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    errorMessage?.let {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Error.copy(0.05f)),
                            modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.ErrorOutline, null, tint = BolsaTokens.Palette.Error)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(it, color = BolsaTokens.Palette.Error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, null) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                            }
                        }
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
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        enabled = uiState !is LoginUiState.Loading && email.isNotBlank() && password.isNotBlank()
                    ) {
                        if (uiState is LoginUiState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Ingresar")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        "¿No tienes una cuenta?",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate("registro_estudiante") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Estudiante")
                        }
                        OutlinedButton(
                            onClick = { navController.navigate("registro_empresa") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Business, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Empresa")
                        }
                    }
                }
            }
        }
    }
}

data class EmpresaStatusInfo(val email: String, val mensaje: String)

@Composable
fun EmpresaRechazadaDialog(info: EmpresaStatusInfo, onDismiss: () -> Unit, onNavigateToRegistro: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Warning, null, tint = Color(0xFFDC2626), modifier = Modifier.size(48.dp)) },
        title = { Text("Solicitud rechazada", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Tu registro no ha sido aprobado por el administrador.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Motivo:", fontWeight = FontWeight.Bold, color = Color.Gray)
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)), modifier = Modifier.padding(top = 8.dp).fillMaxWidth()) {
                    Text(text = info.mensaje.replace("RECHAZADA:", "").trim(), modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Haz clic abajo para completar los documentos solicitados.")
            }
        },
        confirmButton = { Button(onClick = onNavigateToRegistro) { Text("Corregir y reenviar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

data class EmpresaPendienteInfo(val email: String, val mensaje: String)

@Composable
fun EmpresaPendienteDialog(info: EmpresaPendienteInfo, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Schedule, null, tint = Color(0xFFB45309), modifier = Modifier.size(48.dp)) },
        title = { Text("Solicitud en revisión", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Tu solicitud de registro está siendo analizada por el administrador.")
                Spacer(modifier = Modifier.height(16.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Correo: ${info.email}", style = MaterialTheme.typography.bodySmall)
                        Text("Estado: Pendiente", color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Recibirás un correo cuando el proceso finalice.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        },
        confirmButton = { Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Entendido") } }
    )
}

@Composable
fun RecoverPasswordDialog(onDismiss: () -> Unit, snackbarHostState: SnackbarHostState) {
    var email by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recuperar contraseña") },
        text = {
            Column {
                Text("Ingresa tu correo para recibir instrucciones de recuperación.")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, enabled = email.isNotBlank()) { Text("Enviar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
