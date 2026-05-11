package com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.PerfilRequest
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPerfilEstudianteScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val tokenManager = remember { Token(context) }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var carrera by remember { mutableStateOf("") }
    var universidad by remember { mutableStateOf("") }
    var semestre by remember { mutableStateOf("") }
    var habilidades by remember { mutableStateOf("") }
    var disponibilidad by remember { mutableStateOf("INMEDIATA") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val disponibilidadOptions = listOf("INMEDIATA", "1 MES", "3 MESES", "6 MESES")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Crear Perfil - Estudiante",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("mensaje_alerta_crear_perfil") {
                            popUpTo("crear_perfil_estudiante") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E5A7A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.School,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally),
                tint = Color(0xFF1E5A7A)
            )

            Text(
                text = "Información Académica",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )

            Text(
                text = "Completa los siguientes datos para crear tu perfil",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = carrera,
                onValueChange = { carrera = it; errorMessage = null },
                label = { Text("Carrera *") },
                placeholder = { Text("Ej: Ingeniería de Sistemas") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                isError = errorMessage != null && carrera.isEmpty(),
                singleLine = true
            )

            OutlinedTextField(
                value = universidad,
                onValueChange = { universidad = it; errorMessage = null },
                label = { Text("Universidad *") },
                placeholder = { Text("Ej: Universidad Nacional") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                isError = errorMessage != null && universidad.isEmpty(),
                singleLine = true
            )

            OutlinedTextField(
                value = semestre,
                onValueChange = { semestre = it; errorMessage = null },
                label = { Text("Semestre *") },
                placeholder = { Text("Ej: 8") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errorMessage != null && semestre.isEmpty(),
                singleLine = true
            )

            OutlinedTextField(
                value = habilidades,
                onValueChange = { habilidades = it },
                label = { Text("Habilidades Técnicas") },
                placeholder = { Text("Ej: Kotlin, Java, SQL (separadas por comas)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Code, contentDescription = null) },
                minLines = 2
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = disponibilidad,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Disponibilidad para trabajar *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    disponibilidadOptions.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                disponibilidad = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (carrera.isBlank() || universidad.isBlank() || semestre.isBlank()) {
                        errorMessage = "Por favor completa todos los campos obligatorios (*)"
                        return@Button
                    }

                    scope.launch {
                        isLoading = true
                        errorMessage = null

                        try {
                            val token = tokenManager.getToken()
                            val userId = tokenManager.getUserId()

                            if (token == null || userId == null) {
                                errorMessage = "Error de sesión. Por favor inicia sesión nuevamente"
                                isLoading = false
                                return@launch
                            }

                            val perfilRequest = PerfilRequest(
                                carrera = carrera,
                                universidad = universidad,
                                semestre = semestre,
                                habilidades = habilidades,
                                experiencia = "",
                                cvUrl = "",
                                disponibilidad = disponibilidad,
                                idUsuario = userId
                            )

                            val response = RetrofitClient.perfilApi.crearPerfil(perfilRequest)

                            if (response.isSuccessful) {
                                // ✅ Éxito: el perfil se creó correctamente
                                val perfilCreado = response.body()

                                if (perfilCreado != null && perfilCreado.idPerfil > 0) {
                                    // Guardar en SharedPreferences que ya tiene perfil
                                    tokenManager.setProfileCreated(true)
                                    tokenManager.setUserType("ESTUDIANTE")

                                    // Navegar al home principal
                                    navController.navigate("estudiante_home") {
                                        popUpTo("login") { inclusive = true }
                                        popUpTo("mensaje_alerta_crear_perfil") { inclusive = true }
                                        popUpTo("crear_perfil_estudiante") { inclusive = true }
                                    }
                                } else {
                                    errorMessage = "Error: La respuesta del servidor no contiene datos válidos"
                                }
                            } else {
                                // Error HTTP (400, 401, 403, 500, etc.)
                                val errorCode = response.code()
                                val errorBody = response.errorBody()?.string()

                                errorMessage = when {
                                    errorCode == 401 -> "Error de autenticación. Por favor inicia sesión nuevamente."
                                    errorCode == 403 -> "No tienes permisos para realizar esta acción."
                                    !errorBody.isNullOrBlank() -> errorBody
                                    errorCode == 400 -> "Datos inválidos. Verifica la información ingresada."
                                    errorCode == 500 -> "Error interno del servidor. Intenta más tarde."
                                    else -> "Error al guardar perfil: Código $errorCode"
                                }

                                // Si es 401, redirigir al login
                                if (errorCode == 401) {
                                    tokenManager.clearSession()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        } catch (e: HttpException) {
                            // Error de red/HTTP
                            errorMessage = "Error de conexión: ${e.message}"
                        } catch (e: IOException) {
                            errorMessage = "Error de red. Verifica tu conexión a internet."
                        } catch (e: Exception) {
                            errorMessage = "Error inesperado: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5A7A)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Guardar Perfil y Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
