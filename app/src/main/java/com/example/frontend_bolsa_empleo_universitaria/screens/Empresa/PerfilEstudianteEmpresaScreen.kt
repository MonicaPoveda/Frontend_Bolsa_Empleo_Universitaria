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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.Perfil
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoColors
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoGradient

private val BlueGradientStart = UniEmpleoColors.Blue
private val BackgroundGray = UniEmpleoColors.Background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilEstudianteEmpresaScreen(
    idUsuario: Long,
    navController: NavController
) {
    var perfil by remember { mutableStateOf<Perfil?>(null) }
    var usuarioNombre by remember { mutableStateOf("") }
    var usuarioEmail by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar perfil del estudiante
    LaunchedEffect(idUsuario) {
        isLoading = true
        try {
            // Obtener perfil profesional
            val responsePerfil = RetrofitClient.perfilApi.listarPerfiles()
            if (responsePerfil.isSuccessful) {
                val perfiles = responsePerfil.body() ?: emptyList()
                perfil = perfiles.find { it.idUsuario == idUsuario }
            }

            // Obtener datos básicos del usuario (nombre, email)
            val responseUsuarios = RetrofitClient.usuarioApi.listar()
            if (responseUsuarios.isSuccessful) {
                val usuarios = responseUsuarios.body() ?: emptyList()
                val usuario = usuarios.find { it.idUsuario == idUsuario }
                usuarioNombre = "${usuario?.nombre ?: ""} ${usuario?.apellido ?: ""}".trim()
                usuarioEmail = usuario?.email ?: ""
            }
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Perfil del Estudiante",
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
                .verticalScroll(rememberScrollState())
                .background(BackgroundGray)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BlueGradientStart)
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ErrorOutline, null, modifier = Modifier.size(64.dp), tint = Color.Red)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Error: $errorMessage", color = Color.Red)
                        }
                    }
                }
                else -> {
                    // Header gradiente
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(UniEmpleoGradient)
                            .padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = usuarioNombre.ifBlank { "Estudiante" },
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = usuarioEmail.ifBlank { "Email no disponible" },
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Información del perfil profesional
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Perfil profesional",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlueGradientStart
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (perfil == null) {
                                Text(
                                    "Este estudiante no ha completado su perfil profesional.",
                                    color = Color.Gray
                                )
                            } else {
                                val p = perfil!!
                                PerfilInfoRow(
                                    icon = Icons.Default.School,
                                    label = "Carrera",
                                    value = p.carrera.ifBlank { "No especificada" }
                                )
                                PerfilInfoRow(
                                    icon = Icons.Default.Business,
                                    label = "Universidad",
                                    value = p.universidad.ifBlank { "No especificada" }
                                )
                                PerfilInfoRow(
                                    icon = Icons.Default.Numbers,
                                    label = "Semestre",
                                    value = p.semestre?.ifBlank { "No especificado" } ?: "No especificado"
                                )
                                PerfilInfoRow(
                                    icon = Icons.Default.Code,
                                    label = "Habilidades",
                                    value = p.habilidades.ifBlank { "No especificadas" },
                                    multiline = true
                                )
                                PerfilInfoRow(
                                    icon = Icons.Default.Work,
                                    label = "Experiencia",
                                    value = p.experiencia?.ifBlank { "Sin experiencia previa" } ?: "Sin experiencia previa",
                                    multiline = true
                                )
                                PerfilInfoRow(
                                    icon = Icons.Default.AccessTime,
                                    label = "Disponibilidad",
                                    value = p.disponibilidad.ifBlank { "No especificada" }
                                )
                            }
                        }
                    }

                    // Botón de contacto
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Contacto",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlueGradientStart
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            PerfilInfoRow(
                                icon = Icons.Default.Email,
                                label = "Correo electrónico",
                                value = usuarioEmail.ifBlank { "No disponible" }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun PerfilInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    multiline: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = BlueGradientStart)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = if (multiline) 22.sp else 20.sp
            )
        }
    }
}