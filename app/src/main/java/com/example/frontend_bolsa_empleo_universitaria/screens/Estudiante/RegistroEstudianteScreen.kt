package com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante

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
import com.example.frontend_bolsa_empleo_universitaria.model.RegUsuRequest
import com.example.frontend_bolsa_empleo_universitaria.repository.AuthRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.components.BolsaOutlinedFormField
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroEstudianteScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(RetrofitClient.usuarioApi) }

    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Campos del formulario
    var nombre by remember { mutableStateOf("") }
    var nombreError by remember { mutableStateOf<String?>(null) }

    var apellido by remember { mutableStateOf("") }
    var apellidoError by remember { mutableStateOf<String?>(null) }

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    var telefono by remember { mutableStateOf("") }
    var telefonoError by remember { mutableStateOf<String?>(null) }

    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Función para validar nombre
    fun validarNombre(nombre: String): String? {
        return when {
            nombre.isBlank() -> "El nombre es obligatorio"
            nombre.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            nombre.length > 15 -> "El nombre no puede tener más de 15 caracteres"
            !nombre.matches(Regex("^[a-zA-ZáéíóúñÑ\\s]+$")) -> "El nombre solo puede contener letras y espacios"
            else -> null
        }
    }

    // Función para validar apellido
    fun validarApellido(apellido: String): String? {
        return when {
            apellido.isBlank() -> "El apellido es obligatorio"
            apellido.length < 2 -> "El apellido debe tener al menos 2 caracteres"
            apellido.length > 50 -> "El apellido no puede tener más de 50 caracteres"
            !apellido.matches(Regex("^[a-zA-ZáéíóúñÑ\\s]+$")) -> "El apellido solo puede contener letras y espacios"
            else -> null
        }
    }

    // Función para validar email - SOLO VALIDACIÓN BÁSICA (permite cualquier dominio)
    fun validarEmail(email: String): String? {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)\$"
        )

        return when {
            email.isBlank() -> "El correo electrónico es obligatorio"
            !email.contains("@") -> "El correo debe contener '@'"
            email.count { it == '@' } > 1 -> "El correo no puede tener múltiples '@'"
            email.startsWith("@") || email.endsWith("@") -> "El '@' no puede estar al inicio o final"
            email.contains(" ") -> "El correo no puede contener espacios"
            !emailPattern.matcher(email).matches() -> "Formato de correo inválido. Ejemplo: usuario@dominio.com"
            else -> null
        }
    }

    // Función para detectar tipo de correo (informativo, no bloqueante)
    fun getEmailType(email: String): String? {
        if (!email.contains("@")) return null
        val dominio = email.substringAfter("@").lowercase()

        return when {
            dominio.contains("edu") -> "🎓 Correo institucional universitario"
            dominio.contains("gov") -> "🏛️ Correo gubernamental"
            dominio in listOf("gmail.com", "hotmail.com", "outlook.com", "yahoo.com") -> "📧 Correo personal"
            else -> "📨 Correo válido"
        }
    }

    // Función para validar teléfono
    fun validarTelefono(telefono: String): String? {
        return when {
            telefono.isBlank() -> "El teléfono es obligatorio"
            !telefono.matches(Regex("^[0-9]+$")) -> "El teléfono solo debe contener números"
            telefono.length < 10 -> "El numero de teléfono debe tener al menos 10 dígitos"
            telefono.length > 10 -> "El numero de teléfono no puede tener más de 10 dígitos"
            else -> null
        }
    }

    // Función para validar contraseña
    fun validarPassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es obligatoria"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            password.length > 20 -> "La contraseña no puede tener más de 20 caracteres"
            !password.matches(Regex(".*[A-Z].*")) -> "La contraseña debe contener al menos una mayúscula"
            !password.matches(Regex(".*[a-z].*")) -> "La contraseña debe contener al menos una minúscula"
            !password.matches(Regex(".*[0-9].*")) -> "La contraseña debe contener al menos un número"
            !password.matches(Regex(".*[@#\$%^&+=!].*")) -> "La contraseña debe contener al menos un carácter especial (@#\$%^&+=!)"
            else -> null
        }
    }

    // Función para validar confirmación de contraseña
    fun validarConfirmPassword(confirmPassword: String, password: String): String? {
        return when {
            confirmPassword.isBlank() -> "Debes confirmar tu contraseña"
            confirmPassword != password -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    // Validar todos los campos en tiempo real
    LaunchedEffect(nombre) { nombreError = validarNombre(nombre) }
    LaunchedEffect(apellido) { apellidoError = validarApellido(apellido) }
    LaunchedEffect(email) { emailError = validarEmail(email) }
    LaunchedEffect(telefono) { telefonoError = validarTelefono(telefono) }
    LaunchedEffect(password) { passwordError = validarPassword(password) }
    LaunchedEffect(confirmPassword) { confirmPasswordError = validarConfirmPassword(confirmPassword, password) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Estudiante") },
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
                text = "Crea tu cuenta como Estudiante",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Estudiantes activos y egresados pueden registrarse",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            BolsaOutlinedFormField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    showError = false
                },
                label = "Nombre",
                placeholder = "Ej: Juan",
                isError = nombreError != null && nombre.isNotEmpty(),
                supportingText = if (nombreError != null && nombre.isNotEmpty()) {
                    {
                        Text(
                            text = nombreError!!,
                            color = BolsaTokens.Palette.Error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else null,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nombre", tint = BolsaTokens.Palette.Primary) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            BolsaOutlinedFormField(
                value = apellido,
                onValueChange = {
                    apellido = it
                    showError = false
                },
                label = "Apellido",
                placeholder = "Ej: Pérez",
                isError = apellidoError != null && apellido.isNotEmpty(),
                supportingText = if (apellidoError != null && apellido.isNotEmpty()) {
                    {
                        Text(
                            text = apellidoError!!,
                            color = BolsaTokens.Palette.Error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else null,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Apellido", tint = BolsaTokens.Palette.Primary) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            BolsaOutlinedFormField(
                value = email,
                onValueChange = {
                    email = it.lowercase()
                    showError = false
                },
                label = "Correo electrónico",
                placeholder = "estudiante@universidad.edu.co o usuario@gmail.com",
                isError = emailError != null && email.isNotEmpty(),
                supportingText = when {
                    emailError != null && email.isNotEmpty() -> {
                        {
                            Text(
                                text = emailError!!,
                                color = BolsaTokens.Palette.Error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    email.isNotEmpty() && emailError == null -> {
                        {
                            val tipoCorreo = getEmailType(email)
                            Text(
                                text = "✓ $tipoCorreo",
                                color = BolsaTokens.Palette.Primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    else -> null
                },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = BolsaTokens.Palette.Primary) }
            )

            // Tarjeta informativa sobre tipos de correo permitidos
            if (email.isNotEmpty() && emailError == null) {
                Spacer(modifier = Modifier.height(8.dp))
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
                                text = "📧 Tipos de correo permitidos:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• 🎓 Correo institucional universitario (ej: @unal.edu.co, @javeriana.edu.co)\n" +
                                    "• 📧 Correo personal (Gmail, Hotmail, Outlook, Yahoo, etc.)\n" +
                                    "• 🌐 Cualquier dominio válido es aceptado",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BolsaOutlinedFormField(
                value = telefono,
                onValueChange = {
                    telefono = it.filter { it.isDigit() }
                    showError = false
                },
                label = "Teléfono",
                placeholder = "3001234567",
                isError = telefonoError != null && telefono.isNotEmpty(),
                supportingText = if (telefonoError != null && telefono.isNotEmpty()) {
                    {
                        Text(
                            text = telefonoError!!,
                            color = BolsaTokens.Palette.Error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else null,
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Teléfono", tint = BolsaTokens.Palette.Primary) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            BolsaOutlinedFormField(
                value = password,
                onValueChange = {
                    password = it
                    showError = false
                },
                label = "Contraseña",
                placeholder = "Mínimo 6 caracteres",
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError != null && password.isNotEmpty(),
                supportingText = when {
                    passwordError != null && password.isNotEmpty() -> {
                        {
                            Text(
                                text = passwordError!!,
                                color = BolsaTokens.Palette.Error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    password.isNotEmpty() && passwordError == null -> {
                        {
                            Text(
                                text = "✓ Contraseña segura",
                                color = BolsaTokens.Palette.Primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    else -> null
                },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña", tint = BolsaTokens.Palette.Primary) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isPasswordVisible) "Ocultar" else "Mostrar",
                            tint = BolsaTokens.Palette.TextSecondary
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            BolsaOutlinedFormField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    showError = false
                },
                label = "Confirmar contraseña",
                placeholder = "Repite tu contraseña",
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = confirmPasswordError != null && confirmPassword.isNotEmpty(),
                supportingText = when {
                    confirmPasswordError != null && confirmPassword.isNotEmpty() -> {
                        {
                            Text(
                                text = confirmPasswordError!!,
                                color = BolsaTokens.Palette.Error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    confirmPassword.isNotEmpty() && confirmPasswordError == null -> {
                        {
                            Text(
                                text = "✓ Las contraseñas coinciden",
                                color = BolsaTokens.Palette.Primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    else -> null
                },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirmar", tint = BolsaTokens.Palette.Primary) }
            )

            // Requisitos de contraseña
            if (password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "🔐 Requisitos de contraseña:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Mínimo 6 caracteres\n" +
                                    "• Al menos una mayúscula\n" +
                                    "• Al menos una minúscula\n" +
                                    "• Al menos un número\n" +
                                    "• Al menos un carácter especial (@#\$%^&+=!)",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp
                        )
                    }
                }
            }

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
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Éxito",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = successMessage,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Registrar
            Button(
                onClick = {
                    // Validar todos los campos antes de enviar
                    val nombreValid = validarNombre(nombre)
                    val apellidoValid = validarApellido(apellido)
                    val emailValid = validarEmail(email)
                    val telefonoValid = validarTelefono(telefono)
                    val passwordValid = validarPassword(password)
                    val confirmValid = validarConfirmPassword(confirmPassword, password)

                    when {
                        nombreValid != null -> {
                            showError = true
                            errorMessage = "❌ Error en el campo 'Nombre': $nombreValid"
                            showSuccess = false
                        }
                        apellidoValid != null -> {
                            showError = true
                            errorMessage = "❌ Error en el campo 'Apellido': $apellidoValid"
                            showSuccess = false
                        }
                        emailValid != null -> {
                            showError = true
                            errorMessage = "❌ Error en el campo 'Correo Electrónico': $emailValid"
                            showSuccess = false
                        }
                        telefonoValid != null -> {
                            showError = true
                            errorMessage = "❌ Error en el campo 'Teléfono': $telefonoValid"
                            showSuccess = false
                        }
                        passwordValid != null -> {
                            showError = true
                            errorMessage = "❌ Error en el campo 'Contraseña': $passwordValid"
                            showSuccess = false
                        }
                        confirmValid != null -> {
                            showError = true
                            errorMessage = "❌ Error en el campo 'Confirmar Contraseña': $confirmValid"
                            showSuccess = false
                        }
                        else -> {
                            showError = false
                            isLoading = true
                            errorMessage = ""

                            scope.launch {
                                val request = RegUsuRequest(
                                    nombre = nombre,
                                    apellido = apellido,
                                    email = email,
                                    telefono = telefono,
                                    password = password
                                )

                                val result = authRepository.registrarEstudiante(request)

                                isLoading = false

                                result.fold(
                                    onSuccess = { usuario ->
                                        showSuccess = true
                                        successMessage = "✅ ¡Registro exitoso! Redirigiendo al login..."

                                        delay(2000)
                                        navController.navigate("login") {
                                            popUpTo("registro_estudiante") { inclusive = true }
                                        }
                                    },
                                    onFailure = { error ->
                                        showError = true
                                        errorMessage = when {
                                            error.message?.contains("409") == true ||
                                                    error.message?.contains("duplicate") == true ->
                                                "❌ Ya existe un usuario registrado con este correo electrónico"
                                            error.message?.contains("timeout") == true ->
                                                "⏰ Tiempo de espera agotado. Verifica tu conexión a internet"
                                            error.message?.contains("500") == true ->
                                                "⚠️ Error en el servidor. Intenta más tarde"
                                            else ->
                                                "❌ Error al registrar: ${error.message ?: "Intenta nuevamente"}"
                                        }
                                        showSuccess = false
                                    }
                                )
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
                    Text("Registrarse")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigateUp() }
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión aquí")
            }
        }
    }
}