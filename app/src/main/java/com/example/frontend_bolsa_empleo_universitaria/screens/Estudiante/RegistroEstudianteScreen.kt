package com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.RegUsuRequest
import com.example.frontend_bolsa_empleo_universitaria.repository.AuthRepository
import kotlinx.coroutines.launch

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
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Estudiante") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
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
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = password != confirmPassword && confirmPassword.isNotEmpty()
            )

            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (showSuccess) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = successMessage,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        nombre.isBlank() -> {
                            showError = true
                            errorMessage = "Ingresa tu nombre"
                            showSuccess = false
                        }
                        apellido.isBlank() -> {
                            showError = true
                            errorMessage = "Ingresa tu apellido"
                            showSuccess = false
                        }
                        email.isBlank() -> {
                            showError = true
                            errorMessage = "Ingresa tu correo electrónico"
                            showSuccess = false
                        }
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            showError = true
                            errorMessage = "Ingresa un correo electrónico válido"
                            showSuccess = false
                        }
                        telefono.isBlank() -> {
                            showError = true
                            errorMessage = "Ingresa tu teléfono"
                            showSuccess = false
                        }
                        password.isBlank() -> {
                            showError = true
                            errorMessage = "Ingresa una contraseña"
                            showSuccess = false
                        }
                        password.length < 6 -> {
                            showError = true
                            errorMessage = "La contraseña debe tener al menos 6 caracteres"
                            showSuccess = false
                        }
                        password != confirmPassword -> {
                            showError = true
                            errorMessage = "Las contraseñas no coinciden"
                            showSuccess = false
                        }
                        else -> {
                            showError = false
                            isLoading = true

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
                                        successMessage = "¡Registro exitoso! Redirigiendo al login..."

                                        // Esperar 2 segundos y redirigir al login
                                        kotlinx.coroutines.delay(2000)
                                        navController.navigate("login") {
                                            popUpTo("registro_estudiante") { inclusive = true }
                                        }
                                    },
                                    onFailure = { error ->
                                        showError = true
                                        errorMessage = error.message ?: "Error al registrar"
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