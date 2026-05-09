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
fun CrearPerfilEgresadoScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val tokenManager = remember { Token(context) }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf("") }
    var universidad by remember { mutableStateOf("") }
    var anioGraduacion by remember { mutableStateOf("") }
    var experiencia by remember { mutableStateOf("") }
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
                        "Crear Perfil - Egresado",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("mensaje_alerta_crear_perfil") {
                            popUpTo("crear_perfil_egresado") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
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
            // Icono
            Icon(
                Icons.Default.Work,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally),
                tint = Color(0xFF2E7D32)
            )

            Text(
                text = "Información Profesional",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )

            Text(
                text = "Completa los siguientes datos para crear tu perfil de egresado",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Título
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it; errorMessage = null },
                label = { Text("Título Obtenido *") },
                placeholder = { Text("Ej: Ingeniero de Sistemas") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                isError = errorMessage != null && titulo.isEmpty(),
                singleLine = true
            )

            // Campo Universidad
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

            // Campo Año de Graduación
            OutlinedTextField(
                value = anioGraduacion,
                onValueChange = { anioGraduacion = it; errorMessage = null },
                label = { Text("Año de Graduación *") },
                placeholder = { Text("Ej: 2023") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errorMessage != null && anioGraduacion.isEmpty(),
                singleLine = true
            )

            // Campo Experiencia Laboral
            OutlinedTextField(
                value = experiencia,
                onValueChange = { experiencia = it },
                label = { Text("Experiencia Laboral") },
                placeholder = { Text("Describe tu experiencia previa...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                minLines = 3,
                maxLines = 5
            )

            // Campo Habilidades
            OutlinedTextField(
                value = habilidades,
                onValueChange = { habilidades = it },
                label = { Text("Habilidades Técnicas") },
                placeholder = { Text("Ej: Kotlin, Java, SQL, Liderazgo (separadas por comas)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Code, contentDescription = null) },
                minLines = 2
            )

            // Campo Disponibilidad
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

            // Mensaje de error
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

            // Botón Guardar
            Button(
                onClick = {
                    // Validar campos obligatorios
                    if (titulo.isBlank() || universidad.isBlank() || anioGraduacion.isBlank()) {
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

                            // Crear el request del perfil
                            val perfilRequest = PerfilRequest(
                                carrera = titulo,
                                universidad = universidad,
                                semestre = "Graduado",  // Los egresados no tienen semestre
                                habilidades = habilidades,
                                experiencia = experiencia,
                                cvUrl = "",
                                disponibilidad = disponibilidad,
                                idUsuario = userId
                            )

                            // Llamar al API para guardar el perfil
                            val response = RetrofitClient.perfilApi.crearPerfil(perfilRequest)

                            if (response.isSuccessful) {
                                val perfilCreado = response.body()

                                if (perfilCreado != null && perfilCreado.idPerfil > 0) {
                                    // Guardar en SharedPreferences que ya tiene perfil
                                    tokenManager.setProfileCreated(true)
                                    tokenManager.setUserType("EGRESADO")

                                    // Navegar al home principal limpiando el backstack
                                    navController.navigate("estudiante_home") {
                                        popUpTo("login") { inclusive = true }
                                        popUpTo("mensaje_alerta_crear_perfil") { inclusive = true }
                                        popUpTo("crear_perfil_egresado") { inclusive = true }
                                    }
                                } else {
                                    errorMessage = "Error: La respuesta del servidor no contiene datos válidos"
                                }
                            } else {
                                // Error HTTP
                                val errorCode = response.code()
                                val errorBody = response.errorBody()?.string()

                                errorMessage = when {
                                    errorCode == 401 -> {
                                        tokenManager.clearSession()
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                        "Error de autenticación. Por favor inicia sesión nuevamente."
                                    }
                                    errorCode == 403 -> "No tienes permisos para realizar esta acción."
                                    !errorBody.isNullOrBlank() -> errorBody
                                    errorCode == 400 -> "Datos inválidos. Verifica la información ingresada."
                                    errorCode == 500 -> "Error interno del servidor. Intenta más tarde."
                                    else -> "Error al guardar perfil: Código $errorCode"
                                }
                            }
                        } catch (e: HttpException) {
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Texto informativo
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Como egresado podrás postularte a ofertas que requieran título profesional",
                        fontSize = 12.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}