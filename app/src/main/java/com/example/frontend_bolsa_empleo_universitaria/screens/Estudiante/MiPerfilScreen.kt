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
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.Perfil
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoColors
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoGradient
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoScaffold
import com.example.frontend_bolsa_empleo_universitaria.utils.Token


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiPerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = remember { Token(context) }

    var perfil by remember { mutableStateOf<Perfil?>(null) }  // ✅ Usar Perfil, no PerfilRequest
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val nombre = tokenManager.getUserNombre() ?: "Estudiante"
    val apellido = tokenManager.getUserApellido()
    val email = tokenManager.getUserEmail() ?: ""
    val telefono = tokenManager.getUserTelefono()
    val telefonoMostrar = if (telefono.isNotBlank()) telefono else "No disponible"


    // Cargar solo el perfil profesional
    LaunchedEffect(Unit) {
        isLoading = true
        val token = tokenManager.getToken()
        val userId = tokenManager.getUserId()
        if (token != null && userId != null) {
            try {
                val response = RetrofitClient.perfilApi.listarPerfiles()
                if (response.isSuccessful) {
                    val perfiles = response.body() ?: emptyList()
                    perfil = perfiles.find { it.idUsuario == userId }
                } else {
                    errorMessage = "Error al cargar perfil: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
        isLoading = false
    }

    UniEmpleoScaffold(
        title = "Mi perfil",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(UniEmpleoColors.Background)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(UniEmpleoGradient)
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
                    Text(text = nombre, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = email, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Datos personales
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Datos personales", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UniEmpleoColors.Blue)
                    Spacer(modifier = Modifier.height(12.dp))
                    PersonalInfoRow(icon = Icons.Default.Person, label = "Nombre", value = nombre)
                    PersonalInfoRow(icon = Icons.Default.Person, label = "Apellido", value = apellido)
                    PersonalInfoRow(icon = Icons.Default.Email, label = "Email", value = email)
                    PersonalInfoRow(icon = Icons.Default.Phone, label = "Teléfono", value = telefonoMostrar) // ✅ añadir esta línea

                }
            }

            // Perfil profesional
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Perfil profesional", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UniEmpleoColors.Blue)
                    Spacer(modifier = Modifier.height(12.dp))
                    when {
                        isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = UniEmpleoColors.Blue)
                        errorMessage != null -> Text("Error: $errorMessage", color = Color.Red)
                        perfil == null -> {
                            Text("No has creado tu perfil profesional aún.", color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { navController.navigate("configuracion_cuenta") }) {
                                Text("Completar Perfil")
                            }
                        }
                        else -> {
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
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PersonalInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, multiline: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = UniEmpleoColors.Blue)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(
                text = value,
                fontSize = 15.sp,
                color = Color.Black,
                lineHeight = if (multiline) 22.sp else 20.sp
            )
        }
    }
}
