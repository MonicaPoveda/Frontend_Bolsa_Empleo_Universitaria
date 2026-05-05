package com.example.frontend_bolsa_empleo_universitaria.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend_bolsa_empleo_universitaria.model.Empresa
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEmpresaScreen(
    idEmpresa: Long,
    viewModel: OfertasViewModel,
    onBack: () -> Unit
) {
    // Cargamos la empresa al iniciar la pantalla
    LaunchedEffect(idEmpresa) {
        viewModel.cargarEmpresa(idEmpresa)
    }

    val empresa by viewModel.empresaSeleccionada
    val isLoading by viewModel.loadingEmpresa
    val azulPrimario = Color(0xFF1565C0)
    val backgroundGray = Color(0xFFF8FAFF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Empresa", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = azulPrimario,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = backgroundGray
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = azulPrimario)
            }
        } else if (empresa == null) {
            val errorMsg by viewModel.errorEmpresa
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(errorMsg ?: "No se pudo cargar la información", fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Text("ID de empresa buscado: $idEmpresa", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = azulPrimario)
                    ) {
                        Text("Volver")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(azulPrimario)
                        .padding(bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Business, null, tint = Color.White, modifier = Modifier.size(50.dp))
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = empresa?.nombre ?: "Sin nombre",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = empresa?.sector ?: "Sector General",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .offset(y = (-30).dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Sobre la empresa", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = empresa?.descripcion ?: "No hay descripción disponible para esta empresa.",
                            color = Color.Gray,
                            lineHeight = 22.sp
                        )
                        
                        HorizontalDivider(Modifier.padding(vertical = 20.dp), color = Color(0xFFF1F3F4))

                        DetailRow(Icons.Default.LocationOn, "Ubicación", empresa?.ciudad ?: "No especificada")
                        DetailRow(Icons.Default.Email, "Correo Electrónico", empresa?.email ?: "No disponible")
                        DetailRow(Icons.Default.Phone, "Teléfono de contacto", empresa?.telefono ?: "No disponible")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF1565C0), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}
