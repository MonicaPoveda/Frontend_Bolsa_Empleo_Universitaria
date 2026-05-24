package com.example.frontend_bolsa_empleo_universitaria.screens.Empresa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.SolicitudRegistroEmpresa
import com.example.frontend_bolsa_empleo_universitaria.repository.ArchivoRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.components.BolsaOutlinedFormField
import com.example.frontend_bolsa_empleo_universitaria.ui.components.EmpresaDocumentSection
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
import com.example.frontend_bolsa_empleo_universitaria.utils.EmpresaSolicitudCache
import com.example.frontend_bolsa_empleo_universitaria.utils.EmpresaSolicitudDraft
import com.example.frontend_bolsa_empleo_universitaria.utils.HttpErrorParser
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroEmpresaScreen(navController: NavController, initialEmail: String = "") {
    val context = LocalContext.current
    val solicitudCache = remember { EmpresaSolicitudCache(context) }
    val archivoRepository = remember { ArchivoRepository(RetrofitClient.archivoApi, context) }
    val scope = rememberCoroutineScope()
    var pasoActual by remember { mutableIntStateOf(1) }

    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var cacheLoaded by remember { mutableStateOf(false) }
    var sinDatosLocales by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var sector by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var idEmpresaPendiente by remember { mutableStateOf<Long?>(null) }
    val esModoEdicion = initialEmail.isNotBlank()

    LaunchedEffect(initialEmail) {
        if (initialEmail.isNotBlank()) {
            isLoading = true
            val draft = solicitudCache.load(initialEmail)
            if (draft != null) {
                idEmpresaPendiente = draft.idEmpresaPendiente
                nombre = draft.nombre
                email = draft.email
                sector = draft.sector
                telefono = draft.telefono
                ciudad = draft.ciudad
                descripcion = draft.descripcion
                sinDatosLocales = false
            } else {
                email = initialEmail
                sinDatosLocales = true
            }
            cacheLoaded = true
            isLoading = false
        } else {
            cacheLoaded = true
        }
    }

    fun validarEmail(email: String): String? {
        val pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(?!gmail\\.com$|hotmail\\.com$|outlook\\.com$)[A-Za-z0-9.-]+\\.(com|co|net)$")
        return when {
            email.isBlank() -> "El correo es obligatorio"
            !pattern.matcher(email).matches() -> "Usa un correo empresarial válido (.com, .co, .net)"
            else -> null
        }
    }

    fun validarPassword(pass: String): String? {
        return when {
            pass.length < 6 -> "Mínimo 6 caracteres"
            !pass.matches(Regex(".*[A-Z].*")) -> "Falta una mayúscula"
            !pass.matches(Regex(".*[0-9].*")) -> "Falta un número"
            !pass.matches(Regex(".*[@#\\$%^&+=!].*")) -> "Falta un carácter especial"
            else -> null
        }
    }

  fun persistDraft() {
        if (email.isBlank()) return
        solicitudCache.save(
            EmpresaSolicitudDraft(
                nombre = nombre,
                email = email,
                sector = sector,
                telefono = telefono,
                ciudad = ciudad,
                descripcion = descripcion,
                idEmpresaPendiente = idEmpresaPendiente
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (pasoActual == 1) {
                            if (esModoEdicion) "Corregir solicitud" else "Paso 1: Cuenta"
                        } else {
                            "Paso 2: Perfil"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (pasoActual == 2) pasoActual = 1 else navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BolsaTokens.Palette.Primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (isLoading || !cacheLoaded) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = BolsaTokens.Palette.Primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Cargando datos...", color = BolsaTokens.Palette.TextSecondary)
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepIndicator(step = 1, active = pasoActual >= 1)
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(if (pasoActual == 2) BolsaTokens.Palette.Primary else Color.LightGray)
                )
                StepIndicator(step = 2, active = pasoActual == 2)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (esModoEdicion && sinDatosLocales) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFFB45309))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "No encontramos un borrador local. Completa los datos de perfil para reenviar tu solicitud.",
                            fontSize = 13.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            if (esModoEdicion) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Solicitud rechazada", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Puedes corregir los datos de texto y reenviar. Los archivos (foto/documentos) estarán disponibles cuando tu cuenta sea aprobada.",
                            fontSize = 13.sp,
                            color = Color(0xFF991B1B)
                        )
                    }
                }
            }

            if (pasoActual == 1) {
                Text(
                    text = if (esModoEdicion) "Corregir solicitud de registro" else "Crea tu cuenta empresarial",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (esModoEdicion) {
                    Text(
                        "El correo y la contraseña no se modifican. Puedes actualizar el nombre y los datos del perfil en el siguiente paso.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                BolsaOutlinedFormField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = "Nombre de la empresa",
                    isError = nombre.length < 3,
                    supportingText = if (nombre.length < 3) {
                        { Text("Mínimo 3 caracteres", color = BolsaTokens.Palette.Error) }
                    } else null,
                    leadingIcon = { Icon(Icons.Default.Business, null, tint = BolsaTokens.Palette.Primary) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                val emailErr = if (esModoEdicion) null else validarEmail(email)
                BolsaOutlinedFormField(
                    value = email,
                    onValueChange = { if (!esModoEdicion) email = it.lowercase() },
                    label = "Correo corporativo",
                    enabled = !esModoEdicion,
                    isError = emailErr != null,
                    supportingText = if (emailErr != null) {
                        { Text(emailErr, color = BolsaTokens.Palette.Error) }
                    } else null,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            null,
                            tint = if (esModoEdicion) Color.Gray else BolsaTokens.Palette.Primary
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!esModoEdicion) {
                    val passErr = validarPassword(password)
                    BolsaOutlinedFormField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Contraseña",
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passErr != null,
                        supportingText = if (passErr != null) {
                            { Text(passErr, color = BolsaTokens.Palette.Error) }
                        } else null,
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = BolsaTokens.Palette.Primary) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BolsaOutlinedFormField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirmar contraseña",
                        isError = confirmPassword != password,
                        supportingText = if (confirmPassword != password) {
                            { Text("No coinciden", color = BolsaTokens.Palette.Error) }
                        } else null,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.LockClock, null, tint = BolsaTokens.Palette.Primary) }
                    )
                } else {
                    BolsaOutlinedFormField(
                        value = "********",
                        onValueChange = {},
                        label = "Contraseña",
                        enabled = false,
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.Gray) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        persistDraft()
                        pasoActual = 2
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(BolsaTokens.Dimens.buttonRadius),
                    enabled = if (esModoEdicion) {
                        nombre.length >= 3
                    } else {
                        nombre.length >= 3 &&
                            validarEmail(email) == null &&
                            validarPassword(password) == null &&
                            password == confirmPassword
                    }
                ) {
                    Text("Siguiente: Datos de perfil", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ChevronRight, null)
                }
            } else {
                Text("Completa el perfil de la empresa", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                BolsaOutlinedFormField(
                    value = sector,
                    onValueChange = { sector = it },
                    label = "Sector económico",
                    placeholder = "Ej: Tecnología, Salud...",
                    isError = sector.length < 3,
                    supportingText = if (sector.length < 3) {
                        { Text("Mínimo 3 caracteres", color = BolsaTokens.Palette.Error) }
                    } else null,
                    leadingIcon = { Icon(Icons.Default.Category, null, tint = BolsaTokens.Palette.Primary) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                BolsaOutlinedFormField(
                    value = telefono,
                    onValueChange = { if (it.length <= 10) telefono = it.filter { c -> c.isDigit() } },
                    label = "Teléfono de contacto",
                    placeholder = "10 dígitos",
                    isError = telefono.length != 10,
                    supportingText = if (telefono.length != 10) {
                        { Text("Deben ser 10 dígitos", color = BolsaTokens.Palette.Error) }
                    } else null,
                    leadingIcon = { Icon(Icons.Default.Phone, null, tint = BolsaTokens.Palette.Primary) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                BolsaOutlinedFormField(
                    value = ciudad,
                    onValueChange = { ciudad = it },
                    label = "Ciudad / Ubicación",
                    isError = ciudad.length < 3,
                    supportingText = if (ciudad.length < 3) {
                        { Text("Mínimo 3 caracteres", color = BolsaTokens.Palette.Error) }
                    } else null,
                    leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = BolsaTokens.Palette.Primary) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                BolsaOutlinedFormField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = "Descripción de la empresa",
                    placeholder = "Mínimo 20 caracteres sobre tu empresa...",
                    singleLine = false,
                    modifier = Modifier.height(120.dp),
                    isError = descripcion.length < 20,
                    supportingText = if (descripcion.length < 20) {
                        { Text("${descripcion.length}/20 caracteres mínimos", color = BolsaTokens.Palette.Error) }
                    } else null
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sección de Documentos - Solo disponible si ya hay un ID (Registro inicial exitoso o modo edición)
                if (idEmpresaPendiente != null) {
                    EmpresaDocumentSection(
                        editable = true,
                        hasDocument = false, // Podríamos consultar el estado si fuera necesario
                        onUpload = { uri, isReplace ->
                            archivoRepository.subirDocumentoEmpresa(idEmpresaPendiente!!, uri, isReplace)
                        },
                        onViewDocument = {
                            archivoRepository.descargarYAbrirDocumentoEmpresa(idEmpresaPendiente!!)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.PrimaryLight.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = BolsaTokens.Palette.Primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Podrás subir el documento PDF después de enviar la solicitud por primera vez.",
                                fontSize = 12.sp,
                                color = BolsaTokens.Palette.Primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (showError) {
                    Text(
                        errorMessage,
                        color = BolsaTokens.Palette.Error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        isLoading = true
                        showError = false
                        scope.launch {
                            try {
                                persistDraft()
                                val request = SolicitudRegistroEmpresa(
                                    idEmpresaPendiente = idEmpresaPendiente,
                                    nombre = nombre.trim(),
                                    email = email.trim().lowercase(),
                                    password = if (esModoEdicion) null else password,
                                    sector = sector.trim(),
                                    telefono = telefono.trim(),
                                    ciudad = ciudad.trim(),
                                    descripcion = descripcion.trim()
                                )
                                val response = RetrofitClient.empresaApi.enviarSolicitudEmpresa(request)
                                if (response.isSuccessful) {
                                    val body = response.body()
                                    idEmpresaPendiente = body?.idEmpresaPendiente ?: idEmpresaPendiente
                                    persistDraft()
                                    showSuccess = true
                                } else {
                                    errorMessage = HttpErrorParser.fromResponse(response)
                                    showError = true
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error de conexión: ${e.message ?: "Intenta más tarde"}"
                                showError = true
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !isLoading &&
                        sector.length >= 3 &&
                        telefono.length == 10 &&
                        ciudad.length >= 3 &&
                        descripcion.length >= 20,
                    shape = RoundedCornerShape(BolsaTokens.Dimens.buttonRadius)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            if (esModoEdicion) "Reenviar solicitud corregida" else "Enviar solicitud de registro",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (showSuccess) {
            AlertDialog(
                onDismissRequest = { navController.navigate("login") { popUpTo("login") { inclusive = true } } },
                confirmButton = {
                    Button(onClick = { navController.navigate("login") { popUpTo("login") { inclusive = true } } }) {
                        Text("Entendido")
                    }
                },
                title = { Text(if (esModoEdicion) "¡Solicitud reenviada!" else "¡Solicitud enviada!") },
                text = {
                    Text(
                        if (esModoEdicion) {
                            "Tu solicitud corregida fue reenviada. El administrador la revisará nuevamente."
                        } else {
                            "Tu solicitud fue enviada. El administrador revisará tu perfil empresarial."
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun StepIndicator(step: Int, active: Boolean) {
    Surface(
        color = if (active) BolsaTokens.Palette.Primary else Color.LightGray,
        shape = CircleShape,
        modifier = Modifier.size(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(step.toString(), color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
