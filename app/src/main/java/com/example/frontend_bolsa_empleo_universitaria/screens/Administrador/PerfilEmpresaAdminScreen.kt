package com.example.frontend_bolsa_empleo_universitaria.screens.Administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.net.Uri
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoColors
import com.example.frontend_bolsa_empleo_universitaria.viewModel.AdminViewModel

// Paleta de colores Premium Clean (Estilo Notion/Stripe)
private val CleanWhite = UniEmpleoColors.Surface
private val CleanBackground = UniEmpleoColors.Background
private val AccentIndigo = UniEmpleoColors.Blue
private val TextMain = UniEmpleoColors.Text
private val TextSecondary = UniEmpleoColors.Muted
private val BorderLight = Color(0xFFE5E7EB)
private val SuccessGreen = Color(0xFF059669)
private val SuccessGreenBg = Color(0xFFECFDF5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilEmpresaAdminScreen(idEmpresa: Long, navController: NavController, viewModel: AdminViewModel) {
    val empresas by viewModel.empresasAceptadas.collectAsState()
    val empresa = empresas.find { it.idEmpresa == idEmpresa }

    Scaffold(
        containerColor = CleanBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Perfil de empresa", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextMain) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CleanWhite)
            )
        }
    ) { padding ->
        empresa?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card Principal con el diseño Premium solicitado
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Línea de acento lateral (Estilo Notion)
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(80.dp)
                                .align(Alignment.CenterStart)
                                .background(AccentIndigo)
                        )

                        Column(modifier = Modifier.padding(24.dp)) {
                            // Header: Icono + Nombre + Sector
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(64.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF3F4F6),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = when(it.sector?.lowercase()) {
                                                "tecnología", "ti", "sistemas" -> Icons.Default.Code
                                                "ventas", "comercial" -> Icons.Default.TrendingUp
                                                else -> Icons.Default.Business
                                            },
                                            contentDescription = null,
                                            tint = Color(0xFF1E3A8A),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column {
                                    Text(
                                        text = it.nombre ?: "Empresa",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMain
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        color = SuccessGreenBg,
                                        shape = RoundedCornerShape(100.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SuccessGreen))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(it.sector ?: "Activa", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                            HorizontalDivider(color = BorderLight, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(24.dp))

                            // Filas de Información
                            PerfilInfoRow(Icons.Outlined.Mail, "Correo", it.email ?: "N/A")
                            Spacer(modifier = Modifier.height(20.dp))
                            PerfilInfoRow(Icons.Outlined.Phone, "Teléfono", it.telefono ?: "N/A")
                            Spacer(modifier = Modifier.height(20.dp))
                            PerfilInfoRow(Icons.Outlined.LocationOn, "Ciudad", it.ciudad ?: "N/A")
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                "DESCRIPCIÓN", 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = TextSecondary.copy(alpha = 0.6f),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = it.descripcion?.ifBlank { "Sin descripción disponible." } ?: "Sin descripción.",
                                fontSize = 14.sp,
                                color = TextMain,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                "GESTIÓN", 
                                fontSize = 11.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = TextSecondary.copy(alpha = 0.6f),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Botón de Acción
                            PerfilActionButton(
                                text = "Ver ofertas laborales",
                                icon = Icons.Outlined.WorkOutline,
                                color = AccentIndigo,
                                bgColor = AccentIndigo.copy(alpha = 0.05f),
                                onClick = {
                                    val nombre = Uri.encode(it.nombre ?: "Empresa")
                                    navController.navigate("ofertas_por_empresa/${it.idEmpresa}/$nombre")
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Footer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, null, tint = TextSecondary.copy(alpha = 0.4f), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Datos verificados por la institución",
                                    fontSize = 11.sp,
                                    color = TextSecondary.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PerfilInfoRow(icon: ImageVector, label: String, value: String, valueColor: Color = TextMain) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.width(80.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun PerfilActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = color.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
        }
    }
}
