package com.example.frontend_bolsa_empleo_universitaria.screens.Administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Warning
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
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoColors
import com.example.frontend_bolsa_empleo_universitaria.viewModel.AdminViewModel

private val CleanWhite = UniEmpleoColors.Surface
private val CleanBackground = UniEmpleoColors.Background
private val AccentIndigo = UniEmpleoColors.Blue
private val TextMain = UniEmpleoColors.Text
private val TextSecondary = UniEmpleoColors.Muted
private val BorderLight = Color(0xFFE5E7EB)
private val StatusGold = Color(0xFFB45309)
private val StatusGoldBg = Color(0xFFFEF3C7)
private val SuccessGreen = Color(0xFF059669)
private val SuccessGreenBg = Color(0xFFECFDF5)
private val ErrorRed = Color(0xFFDC2626)
private val ErrorRedBg = Color(0xFFFEF2F2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSolicitudScreen(id: Long, navController: NavController, viewModel: AdminViewModel) {
    val empresasPendientes by viewModel.empresasPendientes.collectAsState()
    val empresa = empresasPendientes.find { it.idEmpresaPendiente == id }
    val mensajeGlobal by viewModel.mensaje.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var comentarioAdmin by remember { mutableStateOf("") }

    LaunchedEffect(mensajeGlobal) {
        if (mensajeGlobal != null) {
            if (mensajeGlobal!!.contains("éxito") || mensajeGlobal!!.contains("rechazada") || mensajeGlobal!!.contains("eliminada")) {
                navController.popBackStack()
                viewModel.clearMensaje()
            }
        }
    }

    Scaffold(
        containerColor = CleanBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle de solicitud", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextMain) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = CleanWhite)
            )
        }
    ) { padding ->
        if (empresa == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentIndigo)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = CleanWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(80.dp)
                                    .align(Alignment.CenterStart)
                                    .background(if (empresa.actualizada) SuccessGreen else AccentIndigo)
                            )

                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier.size(64.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF3F4F6),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Storefront, null, tint = Color(0xFF1E3A8A), modifier = Modifier.size(32.dp))
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column {
                                        Text(text = empresa.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextMain)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(color = if (empresa.estado == "RECHAZADA") ErrorRedBg else StatusGoldBg, shape = RoundedCornerShape(100.dp)) {
                                                Text(
                                                    text = if (empresa.estado == "RECHAZADA") "Rechazada" else "En revisión",
                                                    color = if (empresa.estado == "RECHAZADA") ErrorRed else StatusGold,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                )
                                            }
                                            if (empresa.actualizada) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Surface(color = SuccessGreenBg, shape = RoundedCornerShape(100.dp)) {
                                                    Text("ACTUALIZADA", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))
                                HorizontalDivider(color = BorderLight)
                                Spacer(modifier = Modifier.height(24.dp))

                                InfoRow(Icons.Outlined.Mail, "Correo", empresa.email)
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Warning, null, tint = if (empresa.rechazos > 0) ErrorRed else TextSecondary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Intentos", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.width(80.dp))
                                    Text(
                                        text = "${empresa.rechazos} de 3 rechazos",
                                        color = if (empresa.rechazos >= 2) ErrorRed else TextMain,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(20.dp))
                                InfoRow(Icons.Outlined.ChatBubbleOutline, "Mensaje", empresa.mensaje.ifBlank { "Revisión de credenciales" })

                                Spacer(modifier = Modifier.height(32.dp))

                                Text("COMENTARIO DE RESOLUCIÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary.copy(alpha = 0.6f))
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = comentarioAdmin,
                                    onValueChange = { comentarioAdmin = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Motivo de la decisión...", fontSize = 14.sp) },
                                    shape = RoundedCornerShape(12.dp),
                                    maxLines = 4,
                                    minLines = 2
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                ActionButton(
                                    text = "Aprobar solicitud",
                                    icon = Icons.Outlined.CheckCircle,
                                    color = SuccessGreen,
                                    bgColor = SuccessGreenBg,
                                    onClick = { viewModel.aprobarEmpresa(empresa.idEmpresaPendiente, comentarioAdmin) }
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))

                                // ✅ SE CORRIGE LA LLAMADA PASANDO EL OBJETO 'empresa' COMPLETO
                                ActionButton(
                                    text = if (empresa.rechazos >= 2) "Rechazar y Eliminar" else "Rechazar solicitud",
                                    icon = Icons.Outlined.Cancel,
                                    color = ErrorRed,
                                    bgColor = ErrorRedBg,
                                    onClick = { viewModel.rechazarEmpresa(empresa, comentarioAdmin) }
                                )

                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Text(
                                    text = if (empresa.rechazos >= 2) "⚠️ Esta acción eliminará a la empresa permanentemente" 
                                           else "La empresa podrá corregir y reenviar datos",
                                    fontSize = 11.sp,
                                    color = TextSecondary.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                if (isLoading) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentIndigo)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, valueColor: Color = TextMain) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.width(80.dp))
        Text(text = value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector, color: Color, bgColor: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowForward, null, tint = color.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
        }
    }
}
