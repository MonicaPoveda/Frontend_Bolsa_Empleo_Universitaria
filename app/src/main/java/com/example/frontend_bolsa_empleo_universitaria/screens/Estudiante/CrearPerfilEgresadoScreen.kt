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
import com.example.frontend_bolsa_empleo_universitaria.repository.PerfilRepository
import com.example.frontend_bolsa_empleo_universitaria.utils.HttpErrorParser
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
    var habilidades by remember { mutableStateOf("") }
    var disponibilidad by remember { mutableStateOf("INMEDIATA") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
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

            // --- Habilidades como Chips ---
            var habilidadActual by remember { mutableStateOf("") }
            val listaHabilidades = remember(habilidades) {
                if (habilidades.isBlank()) mutableListOf<String>()
                else habilidades.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
            }

            OutlinedTextField(
                value = habilidadActual,
                onValueChange = { habilidadActual = it },
                label = { Text("Agregar Habilidades (Enter para añadir)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Code, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = {
                    if (habilidadActual.isNotBlank()) {
                        val nuevasHabilidades = if (habilidades.isBlank()) habilidadActual
                                               else "$habilidades, ${habilidadActual.trim()}"
                        habilidades = nuevasHabilidades
                        habilidadActual = ""
                    }
                })
            )

            FlowRow(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listaHabilidades.forEach { hab ->
                    AssistChip(
                        onClick = { },
                        label = { Text(hab) },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    val nuevaLista = listaHabilidades.filter { it != hab }
                                    habilidades = nuevaLista.joinToString(", ")
                                },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Eliminar",
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    )
                }
            }

            // --- Selector de Experiencia ---
            var tieneExperiencia by remember { mutableStateOf(false) }
            var cvUrl by remember { mutableStateOf("") }
            var experienciaDetalle by remember { mutableStateOf("") }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = tieneExperiencia, onCheckedChange = { tieneExperiencia = it })
                Text("¿Tienes experiencia laboral?")
            }

            if (tieneExperiencia) {
                OutlinedTextField(
                    value = experienciaDetalle,
                    onValueChange = { experienciaDetalle = it },
                    label = { Text("Cuéntanos tu experiencia") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                    minLines = 3,
                    maxLines = 5
                )
                OutlinedTextField(
                    value = cvUrl,
                    onValueChange = { cvUrl = it },
                    label = { Text("URL de tu CV (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                    singleLine = true
                )
            }

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
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, "Error", tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (successMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2E7D32).copy(alpha = 0.1f)
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
                            tint = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = successMessage!!,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
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
                            RetrofitClient.init(context)
                            val token = tokenManager.getToken()
                            val userId = tokenManager.getUserId()

                            if (token.isNullOrBlank() || userId == null || userId <= 0L) {
                                errorMessage = "Error de sesión. Por favor inicia sesión nuevamente"
                                isLoading = false
                                return@launch
                            }

                            // Crear el request del perfil
                            val perfilRequest = PerfilRequest(
                                carrera = titulo,
                                universidad = universidad,
                                semestre = "EGRESADO",
                                habilidades = habilidades,
                                experiencia = if (tieneExperiencia) experienciaDetalle else "",
                                cvUrl = if (tieneExperiencia) cvUrl else "",
                                disponibilidad = disponibilidad,
                                idUsuario = userId
                            )

                            // Llamar al API para guardar el perfil
                            val response = RetrofitClient.perfilApi.crearPerfil(perfilRequest)

                            if (response.isSuccessful) {
                                val perfilCreado = response.body()

                                if (perfilCreado != null && perfilCreado.idPerfil > 0) {
                                    successMessage = "Perfil creado exitosamente"
                                    errorMessage = null

                                    PerfilRepository(RetrofitClient.perfilApi)
                                        .sincronizarEstadoLocal(tokenManager, perfilCreado)
                                    tokenManager.setUserType("EGRESADO")

                                    // Esperar un momento para mostrar el mensaje de éxito
                                    kotlinx.coroutines.delay(1500)

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
                                val errorCode = response.code()
                                val parsedError = HttpErrorParser.fromResponse(response)

                                errorMessage = when (errorCode) {
                                    401 -> "Tu sesión expiró. Inicia sesión nuevamente."
                                    403 -> "No se pudo guardar el perfil: sesión inválida o expirada. Cierra sesión, vuelve a entrar e intenta de nuevo."
                                    400 -> parsedError.ifBlank { "Datos inválidos. Verifica la información ingresada." }
                                    in 500..599 -> "Error interno del servidor. Intenta más tarde."
                                    else -> parsedError.ifBlank { "Error al guardar perfil: código $errorCode" }
                                }

                                if (errorCode == 401 || errorCode == 403) {
                                    tokenManager.clearSession()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
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