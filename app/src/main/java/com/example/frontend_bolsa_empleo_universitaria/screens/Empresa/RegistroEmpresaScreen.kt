package com.example.frontend_bolsa_empleo_universitaria.screens.Empresa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.SolicitudRegistroEmpresa
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroEmpresaScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Campos del formulario SOLO los que requiere el backend para la solicitud
    var nombre by remember { mutableStateOf("") }
    var nombreError by remember { mutableStateOf<String?>(null) }

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Validaciones específicas para solicitud de empresa
    fun validarNombreEmpresa(nombre: String): String? {
        return when {
            nombre.isBlank() -> "El nombre de la empresa es obligatorio"
            nombre.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            nombre.length > 50 -> "El nombre no puede tener más de 50 caracteres"
            else -> null
        }
    }

    fun validarEmailEmpresa(email: String): String? {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(?!gmail\\.com$|hotmail\\.com$|outlook\\.com$)[A-Za-z0-9.-]+\\.(com|co|net)$"
        )

        return when {
            email.isBlank() -> "El correo electrónico es obligatorio"
            !email.contains("@") -> "El correo debe contener '@'"
            email.contains("gmail.com") ||
                    email.contains("hotmail.com") ||
                    email.contains("outlook.com") ->
                "❌ Solo se permiten correos empresariales (.com, .co, .net). No se permiten correos personales"
            !emailPattern.matcher(email).matches() ->
                "Formato inválido. Solo se permiten dominios .com, .co, .net"
            else -> null
        }
    }

    fun validarPassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es obligatoria"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            password.length > 20 -> "La contraseña no puede tener más de 20 caracteres"
            !password.matches(Regex(".*[A-Z].*")) -> "Debe contener al menos una mayúscula"
            !password.matches(Regex(".*[a-z].*")) -> "Debe contener al menos una minúscula"
            !password.matches(Regex(".*[0-9].*")) -> "Debe contener al menos un número"
            !password.matches(Regex(".*[@#\$%^&+=!].*")) -> "Debe contener al menos un carácter especial (@#\$%^&+=!)"
            else -> null
        }
    }

    // Validaciones en tiempo real
    LaunchedEffect(nombre) { nombreError = validarNombreEmpresa(nombre) }
    LaunchedEffect(email) { emailError = validarEmailEmpresa(email) }
    LaunchedEffect(password) { passwordError = validarPassword(password) }
    LaunchedEffect(confirmPassword) {
        confirmPasswordError = when {
            confirmPassword.isBlank() -> "Debes confirmar tu contraseña"
            confirmPassword != password -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Empresa") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Solicita tu Registro",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Completa tus datos para solicitar registro.\nUn administrador revisará tu solicitud.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Nombre de la Empresa
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    showError = false
                },
                label = { Text("Nombre de la Empresa") },
                placeholder = { Text("Ej: Mi Empresa SAS") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreError != null && nombre.isNotEmpty(),
                supportingText = {
                    if (nombreError != null && nombre.isNotEmpty()) {
                        Text(
                            text = nombreError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp
                        )
                    }
                },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = "Empresa") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it.lowercase()
                    showError = false
                },
                label = { Text("Correo Electrónico") },
                placeholder = { Text("empresa@midominio.com.co") },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null && email.isNotEmpty(),
                supportingText = {
                    when {
                        emailError != null && email.isNotEmpty() -> {
                            Text(
                                text = emailError!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp
                            )
                        }
                        email.isNotEmpty() && emailError == null -> {
                            Text(
                                text = "✓ Correo empresarial válido",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") }
            )

            // Tarjeta informativa sobre correos permitidos
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "📧 Solo correos empresariales:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• ✅ Dominios permitidos: .com, .co, .net\n" +
                                "• ❌ No se permiten: @gmail.com, @hotmail.com, @outlook.com\n" +
                                "• 📝 Ejemplo: empresa@midominio.com.co",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    showError = false
                },
                label = { Text("Contraseña") },
                placeholder = { Text("Mínimo 6 caracteres") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null && password.isNotEmpty(),
                supportingText = {
                    if (passwordError != null && password.isNotEmpty()) {
                        Text(
                            text = passwordError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp
                        )
                    } else if (password.isNotEmpty() && passwordError == null) {
                        Text(
                            text = "✓ Contraseña segura",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp
                        )
                    }
                },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña") },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isPasswordVisible) "Ocultar" else "Mostrar"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Confirmar Contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    showError = false
                },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = confirmPasswordError != null && confirmPassword.isNotEmpty(),
                supportingText = {
                    if (confirmPasswordError != null && confirmPassword.isNotEmpty()) {
                        Text(
                            text = confirmPasswordError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp
                        )
                    } else if (confirmPassword.isNotEmpty() && confirmPasswordError == null) {
                        Text(
                            text = "✓ Las contraseñas coinciden",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp
                        )
                    }
                },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirmar") }
            )

            // Mensaje de error general
            if (showError && errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Mensaje de éxito
            if (showSuccess) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Éxito",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "¡Solicitud Enviada!",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = successMessage,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Enviar Solicitud
            Button(
                onClick = {
                    val nombreValid = validarNombreEmpresa(nombre)
                    val emailValid = validarEmailEmpresa(email)
                    val passwordValid = validarPassword(password)
                    val confirmValid = if (confirmPassword != password) "Las contraseñas no coinciden" else null

                    when {
                        nombreValid != null -> {
                            showError = true
                            errorMessage = "❌ $nombreValid"
                            showSuccess = false
                        }
                        emailValid != null -> {
                            showError = true
                            errorMessage = "❌ $emailValid"
                            showSuccess = false
                        }
                        passwordValid != null -> {
                            showError = true
                            errorMessage = "❌ $passwordValid"
                            showSuccess = false
                        }
                        confirmValid != null -> {
                            showError = true
                            errorMessage = "❌ $confirmValid"
                            showSuccess = false
                        }
                        else -> {
                            showError = false
                            isLoading = true

                            scope.launch {
                                try {
                                    val request = SolicitudRegistroEmpresa(
                                        nombre = nombre,
                                        email = email,
                                        password = password
                                    )

                                    println("📤 Enviando solicitud: $request")

                                    val response = RetrofitClient.empresaApi.enviarSolicitudEmpresa(request)

                                    isLoading = false

                                    if (response.isSuccessful) {
                                        val solicitud = response.body()
                                        showSuccess = true
                                        successMessage = "✅ ¡Solicitud enviada!\n" +
                                                "📧 Correo: ${solicitud?.email}\n" +
                                                "📊 Estado: ${solicitud?.estado}\n" +
                                                "📝 ${solicitud?.mensaje}\n\n" +
                                                "⏳ Espera la aprobación del administrador para iniciar sesión."

                                        // Limpiar campos
                                        nombre = ""
                                        email = ""
                                        password = ""
                                        confirmPassword = ""
                                    } else {
                                        val errorBody = response.errorBody()?.string()
                                        showError = true
                                        errorMessage = when {
                                            errorBody?.contains("Solo correos empresariales") == true ->
                                                "❌ Solo se permiten correos empresariales (.com, .co, .net)"
                                            else -> "❌ Error al enviar solicitud: ${errorBody ?: "Intenta nuevamente"}"
                                        }
                                    }
                                } catch (e: Exception) {
                                    isLoading = false
                                    showError = true
                                    errorMessage = "❌ Error de conexión: ${e.message}"
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Enviar Solicitud de Registro")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text("¿Ya tienes cuenta aprobada? Inicia sesión aquí")
            }
        }
    }
}