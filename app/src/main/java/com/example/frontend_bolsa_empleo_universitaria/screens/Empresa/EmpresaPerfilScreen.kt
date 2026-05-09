package com.example.frontend_bolsa_empleo_universitaria.screens.Empresa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.EmpresaDto
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import kotlinx.coroutines.launch

private val BlueGradientStart = Color(0xFF0056D2)
private val BackgroundGray = Color(0xFFF8FAFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpresaPerfilScreen(padding: PaddingValues) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    val scope = rememberCoroutineScope()

    var empresa by remember { mutableStateOf<EmpresaDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar datos de la empresa
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null

            val email = token.getUserEmail()
            println("Email del token: $email")

            if (email.isNullOrEmpty()) {
                errorMessage = "No se encontró sesión activa"
                isLoading = false
                return@LaunchedEffect
            }

            val response = RetrofitClient.empresaApi.listar()

            if (response.isSuccessful) {
                val empresas = response.body() ?: emptyList()
                empresa = empresas.find { it.email == email }

                println("=== DATOS DE LA EMPRESA ===")
                println("Nombre: ${empresa?.nombre}")
                println("Sector: ${empresa?.sector}")
                println("Descripción: ${empresa?.descripcion}")
                println("Email: ${empresa?.email}")
                println("Teléfono: ${empresa?.telefono}")
                println("Ciudad: ${empresa?.ciudad}")
                println("==========================")

                if (empresa == null) {
                    errorMessage = "No se encontró la información de la empresa"
                }
            } else {
                errorMessage = "Error al cargar los datos: ${response.code()}"
            }
        } catch (e: Exception) {
            println("Error en EmpresaPerfilScreen: ${e.message}")
            errorMessage = "Error al cargar perfil: ${e.message}"
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(BackgroundGray)
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
                        Text("Cargando perfil...", color = Color.Gray)
                    }
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                isLoading = true
                                errorMessage = null
                                scope.launch {
                                    try {
                                        val emailToken = token.getUserEmail()
                                        if (!emailToken.isNullOrEmpty()) {
                                            val response = RetrofitClient.empresaApi.listar()
                                            if (response.isSuccessful) {
                                                val empresas = response.body() ?: emptyList()
                                                empresa = empresas.find { it.email == emailToken }
                                                if (empresa == null) {
                                                    errorMessage = "No se encontró la empresa"
                                                }
                                            } else {
                                                errorMessage = "Error ${response.code()}"
                                            }
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Error: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BlueGradientStart)
                        ) {
                            Text("Reintentar", color = Color.White)
                        }
                    }
                }
            }

            empresa != null -> {
                val e = empresa!!

                // Header con gradiente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(BlueGradientStart, BlueGradientStart.copy(alpha = 0.8f))
                            ),
                            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                        )
                        .padding(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Business,
                                contentDescription = "Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(56.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nombre de la empresa
                        Text(
                            text = e.nombre?.takeIf { it.isNotBlank() } ?: "Sin nombre",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sector
                        val sectorText = e.sector?.takeIf { it.isNotBlank() }
                        if (sectorText != null) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = sectorText,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        } else {
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "Sector no especificado",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                // Información de la empresa
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Sección de información de contacto
                    item {
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
                                    "Información de Contacto",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                // Email
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Email,
                                        contentDescription = "Email",
                                        modifier = Modifier.size(20.dp),
                                        tint = BlueGradientStart
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Email", fontSize = 12.sp, color = Color.Gray)
                                        Text(
                                            e.email?.takeIf { it.isNotBlank() } ?: "No especificado",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black
                                        )
                                    }
                                }

                                // Teléfono
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Phone,
                                        contentDescription = "Teléfono",
                                        modifier = Modifier.size(20.dp),
                                        tint = BlueGradientStart
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Teléfono", fontSize = 12.sp, color = Color.Gray)
                                        Text(
                                            e.telefono?.takeIf { it.isNotBlank() } ?: "No especificado",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black
                                        )
                                    }
                                }

                                // Ciudad
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = "Ciudad",
                                        modifier = Modifier.size(20.dp),
                                        tint = BlueGradientStart
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Ciudad", fontSize = 12.sp, color = Color.Gray)
                                        Text(
                                            e.ciudad?.takeIf { it.isNotBlank() } ?: "No especificada",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Sección de descripción
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = "Descripción",
                                        modifier = Modifier.size(20.dp),
                                        tint = BlueGradientStart
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Descripción",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }

                                val descripcionText = e.descripcion?.takeIf { it.isNotBlank() }
                                if (descripcionText != null) {
                                    Text(
                                        text = descripcionText,
                                        fontSize = 14.sp,
                                        color = Color.DarkGray,
                                        lineHeight = 20.sp
                                    )
                                } else {
                                    Text(
                                        text = "Sin descripción",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }

                    // ✅ Botón de editar perfil ELIMINADO

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}