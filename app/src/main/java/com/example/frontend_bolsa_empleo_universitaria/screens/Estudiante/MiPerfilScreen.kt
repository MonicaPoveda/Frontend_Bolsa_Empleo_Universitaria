package com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PerfilViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PerfilViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiPerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = remember { Token(context) }
    
    // Inicializar ViewModel usando la Factory
    val viewModel: PerfilViewModel = viewModel(
        factory = PerfilViewModelFactory(RetrofitClient.usuarioApi, RetrofitClient.perfilApi)
    )

    // Observar estados del ViewModel
    val usuario by viewModel.usuario.collectAsState()
    val perfil by viewModel.perfil.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        val userId = tokenManager.getUserId()
        val email = tokenManager.getUserEmail()
        if (userId != null && email != null) {
            viewModel.cargarTodo(email, userId)
        }
    }

    // Usar datos del ViewModel si están disponibles, sino del token (como respaldo inicial)
    val nombreMostrar = usuario?.let { "${it.nombre} ${it.apellido}" } ?: tokenManager.getUserNombre()
    val emailMostrar = usuario?.email ?: tokenManager.getUserEmail() ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0056D2), 
                    titleContentColor = Color.White, 
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F5F5))
        ) {
            // Header con degradado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF0056D2), Color(0xFF007BFF))))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = nombreMostrar, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = emailMostrar, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF0056D2))
                }
            } else if (error != null) {
                Text(error!!, color = Color.Red, modifier = Modifier.padding(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección: Datos Personales
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Datos Personales", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0056D2))
                    Spacer(modifier = Modifier.height(12.dp))
                    PersonalInfoRow(icon = Icons.Default.Person, label = "Nombre Completo", value = nombreMostrar)
                    PersonalInfoRow(icon = Icons.Default.Email, label = "Email", value = emailMostrar)
                    PersonalInfoRow(icon = Icons.Default.Phone, label = "Teléfono", value = usuario?.telefono ?: "No registrado")
                }
            }

            // Sección: Perfil Profesional
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Perfil Profesional", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0056D2))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (perfil == null && !isLoading) {
                        Text("No has creado tu perfil profesional aún.", color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { navController.navigate("configuracion") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Completar Perfil")
                        }
                    } else if (perfil != null) {
                        val p = perfil!!
                        PersonalInfoRow(icon = Icons.Default.School, label = "Carrera", value = p.carrera)
                        PersonalInfoRow(icon = Icons.Default.Business, label = "Universidad", value = p.universidad)
                        PersonalInfoRow(icon = Icons.Default.Numbers, label = "Semestre", value = p.semestre ?: "N/A")
                        PersonalInfoRow(icon = Icons.Default.Code, label = "Habilidades", value = p.habilidades, multiline = true)
                        PersonalInfoRow(icon = Icons.Default.Work, label = "Experiencia", value = p.experiencia ?: "Sin experiencia previa", multiline = true)
                        PersonalInfoRow(icon = Icons.Default.AccessTime, label = "Disponibilidad", value = p.disponibilidad)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PersonalInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, multiline: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF0056D2))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(
                text = value,
                fontSize = 15.sp,
                color = Color.Black,
                lineHeight = 20.sp
            )
        }
    }
}
