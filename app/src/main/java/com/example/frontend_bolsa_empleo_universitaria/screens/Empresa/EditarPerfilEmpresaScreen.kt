package com.example.frontend_bolsa_empleo_universitaria.screens.Empresa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.EmpresaDto
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import kotlinx.coroutines.launch

private val BlueGradientStart = Color(0xFF0056D2)
private val BackgroundGray = Color(0xFFF8FAFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilEmpresaScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var empresa by remember { mutableStateOf<EmpresaDto?>(null) }

    // Campos editables
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }

    // Cargar datos actuales
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val emailToken = token.getUserEmail()
            if (!emailToken.isNullOrEmpty()) {
                val response = RetrofitClient.empresaApi.listar()
                if (response.isSuccessful) {
                    val empresas = response.body() ?: emptyList()
                    empresa = empresas.find { it.email == emailToken }

                    empresa?.let {
                        nombre = it.nombre ?: ""
                        email = it.email ?: ""
                        sector = it.sector ?: ""
                        descripcion = it.descripcion ?: ""
                        telefono = it.telefono ?: ""
                        ciudad = it.ciudad ?: ""
                    }
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error al cargar datos: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar Perfil",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueGradientStart)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundGray)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = BlueGradientStart)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Cargando datos...", color = Color.Gray)
                        }
                    }
                }

                else -> {
                    // Tarjeta de información
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Edita la información de tu empresa",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            // Email (solo lectura)
                            OutlinedTextField(
                                value = email,
                                onValueChange = {},
                                label = { Text("Correo Electrónico") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueGradientStart,
                                    unfocusedBorderColor = Color.Gray,
                                    disabledTextColor = Color.Black,
                                    disabledBorderColor = Color.Gray.copy(alpha = 0.5f)
                                )
                            )

                            // Nombre
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre de la empresa") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueGradientStart,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )

                            // Sector
                            OutlinedTextField(
                                value = sector,
                                onValueChange = { sector = it },
                                label = { Text("Sector") },
                                placeholder = { Text("Ej: Tecnología, Comercio, etc.") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueGradientStart,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )

                            // Teléfono
                            OutlinedTextField(
                                value = telefono,
                                onValueChange = { telefono = it },
                                label = { Text("Teléfono") },
                                placeholder = { Text("Ej: 3001234567") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueGradientStart,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )

                            // Ciudad
                            OutlinedTextField(
                                value = ciudad,
                                onValueChange = { ciudad = it },
                                label = { Text("Ciudad") },
                                placeholder = { Text("Ej: Bogotá, Medellín, etc.") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueGradientStart,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )

                            // Descripción
                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                label = { Text("Descripción") },
                                placeholder = { Text("Describe tu empresa...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                maxLines = 6,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueGradientStart,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mensajes de error y éxito
                    if (errorMessage != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (successMessage != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = BlueGradientStart.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Éxito",
                                        tint = BlueGradientStart
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = successMessage!!,
                                        color = BlueGradientStart,
                                        modifier = Modifier.weight(1f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Botón guardar
                    Button(
                        onClick = {
                            if (nombre.isBlank()) {
                                errorMessage = "El nombre de la empresa es obligatorio"
                                return@Button
                            }

                            isSaving = true
                            errorMessage = null
                            successMessage = null

                            scope.launch {
                                try {
                                    val empresaActual = empresa ?: return@launch

                                    val empresaActualizada = EmpresaDto(
                                        idEmpresa = empresaActual.idEmpresa,
                                        nombre = nombre,
                                        sector = sector,
                                        descripcion = descripcion,
                                        email = email,
                                        telefono = telefono,
                                        ciudad = ciudad
                                    )

                                    val response = RetrofitClient.empresaApi.actualizar(
                                        empresaActual.idEmpresa,
                                        empresaActualizada
                                    )

                                    if (response.isSuccessful) {
                                        successMessage = "Perfil actualizado exitosamente"
                                        // Actualizar los datos en la empresa
                                        empresa = empresaActualizada
                                    } else {
                                        errorMessage = "Error al actualizar: ${response.code()}"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Error de conexión: ${e.message}"
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueGradientStart),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = "Guardar",
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar Cambios", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}