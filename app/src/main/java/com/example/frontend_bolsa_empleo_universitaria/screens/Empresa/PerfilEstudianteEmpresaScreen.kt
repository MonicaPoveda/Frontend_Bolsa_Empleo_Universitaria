package com.example.frontend_bolsa_empleo_universitaria.screens.Empresa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.Perfil
import com.example.frontend_bolsa_empleo_universitaria.ui.components.*
import com.example.frontend_bolsa_empleo_universitaria.utils.ArchivoUrls

private val BlueGradientStart = UniEmpleoColors.Blue
private val BackgroundGray = UniEmpleoColors.Background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilEstudianteEmpresaScreen(
    idUsuario: Long,
    navController: NavController
) {
    var perfil by remember { mutableStateOf<Perfil?>(null) }
    var usuarioNombre by remember { mutableStateOf("Cargando...") }
    var usuarioEmail by remember { mutableStateOf("") }
    var usuarioTelefono by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    
    var messageState by remember { mutableStateOf(AdminMessageState()) }
    val cacheBuster = remember { System.currentTimeMillis() }

    LaunchedEffect(idUsuario) {
        isLoading = true
        try {
            // 1. CARGAR DATOS DE USUARIO (Usando listar y filtrar por ID localmente)
            val responseUsuario = RetrofitClient.usuarioApi.listar()
            if (responseUsuario.isSuccessful) {
                val usuario = responseUsuario.body()?.find { it.idUsuario == idUsuario }
                usuarioNombre = "${usuario?.nombre ?: ""} ${usuario?.apellido ?: ""}".trim()
                usuarioEmail = usuario?.email ?: ""
                usuarioTelefono = usuario?.telefono ?: ""
            }

            // 2. CARGAR PERFIL PROFESIONAL (PUEDE FALLAR 404 SI NO ESTÁ CREADO)
            val responsePerfil = RetrofitClient.perfilApi.listarPerfiles()
            if (responsePerfil.isSuccessful) {
                perfil = responsePerfil.body()?.find { it.idUsuario == idUsuario }
            } else if (responsePerfil.code() == 403) {
                messageState = AdminMessageState("No tienes permisos para ver el perfil profesional.", AdminMessageType.WARNING, true)
            }
        } catch (e: Exception) {
            messageState = AdminMessageState("Error de conexión al cargar datos.", AdminMessageType.ERROR, true)
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = { Text("Perfil del Candidato", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueGradientStart)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tarjeta Principal
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProfilePhotoDisplay(
                            photoUrl = ArchivoUrls.fotoUsuario(idUsuario),
                            cacheBuster = cacheBuster,
                            size = 100,
                            modifier = Modifier.clip(CircleShape).background(BackgroundGray)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(usuarioNombre, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = UniEmpleoColors.Text)
                        Text(perfil?.carrera ?: "Candidato", fontSize = 14.sp, color = UniEmpleoColors.Blue, fontWeight = FontWeight.Medium)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = Color(0xFFF3F4F6))
                        Spacer(modifier = Modifier.height(24.dp))

                        PerfilInfoRow(Icons.Default.Email, "Correo", usuarioEmail)
                        if (usuarioTelefono.isNotBlank()) {
                            PerfilInfoRow(Icons.Default.Phone, "Teléfono", usuarioTelefono)
                        }
                        
                        perfil?.let { p ->
                            PerfilInfoRow(Icons.Default.School, "Universidad", p.universidad)
                            PerfilInfoRow(Icons.Default.WorkOutline, "Disponibilidad", p.disponibilidad)
                        }
                    }
                }

                if (perfil != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Habilidades y Experiencia", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = UniEmpleoColors.Text)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(perfil?.experiencia ?: "Sin experiencia registrada.", fontSize = 14.sp, color = UniEmpleoColors.Muted, lineHeight = 20.sp)
                            
                            if (perfil?.habilidades?.isNotBlank() == true) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Habilidades: ${perfil?.habilidades}", fontSize = 13.sp, color = UniEmpleoColors.Blue)
                            }
                        }
                    }
                } else if (!isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("El perfil profesional no ha sido completado.", fontSize = 12.sp, color = Color.Gray)
                }
            }

            AdminMessageBanner(
                state = messageState,
                onDismiss = { messageState = messageState.copy(visible = false) },
                modifier = Modifier.align(Alignment.TopCenter).padding(8.dp)
            )

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter), color = BlueGradientStart)
            }
        }
    }
}

@Composable
private fun PerfilInfoRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = Color(0xFFF0F7FF)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, null, modifier = Modifier.size(18.dp), tint = UniEmpleoColors.Blue) }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 14.sp, color = UniEmpleoColors.Text, fontWeight = FontWeight.SemiBold)
        }
    }
}
