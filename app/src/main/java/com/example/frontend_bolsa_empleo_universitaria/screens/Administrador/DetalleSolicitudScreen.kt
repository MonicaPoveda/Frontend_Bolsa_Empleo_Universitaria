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
import com.example.frontend_bolsa_empleo_universitaria.model.EmpresaPendiente
import com.example.frontend_bolsa_empleo_universitaria.ui.components.AdminMessageBanner
import com.example.frontend_bolsa_empleo_universitaria.ui.components.AdminMessageType
import com.example.frontend_bolsa_empleo_universitaria.ui.components.ProfilePhotoDisplay
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
    val todasLasEmpresas by viewModel.empresasPendientes.collectAsState()
    val adminMessage by viewModel.adminMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Mantenemos una referencia local para que no desaparezca la info al aprobar/rechazar
    var empresaLocal by remember { mutableStateOf<EmpresaPendiente?>(null) }
    var comentarioAdmin by remember { mutableStateOf("") }

    // Actualizar la referencia local solo cuando se carga inicialmente
    LaunchedEffect(todasLasEmpresas) {
        val encontrada = todasLasEmpresas.find { it.idEmpresaPendiente == id }
        if (encontrada != null) {
            empresaLocal = encontrada
        }
    }

    Scaffold(
        containerColor = CleanBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle de la Empresa", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextMain) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = CleanWhite)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (empresaLocal == null && isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentIndigo)
                }
            } else if (empresaLocal != null) {
                val empresa = empresaLocal!!
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
                        Column(modifier = Modifier.padding(24.dp)) {
                            // Cabecera con Foto y Nombre
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ProfilePhotoDisplay(
                                    photoUrl = "https://backend-sistema-empleo-universitario.onrender.com/api/archivos/foto/empresa/${empresa.idEmpresaPendiente}",
                                    size = 72,
                                    modifier = Modifier.clip(RoundedCornerShape(14.dp))
                                )
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column {
                                    Text(text = empresa.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextMain)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(color = if (empresa.estado == "RECHAZADA") ErrorRedBg else StatusGoldBg, shape = RoundedCornerShape(100.dp)) {
                                            Text(
                                                text = if (empresa.estado == "RECHAZADA") "Rechazada" else "Pendiente",
                                                color = if (empresa.estado == "RECHAZADA") ErrorRed else StatusGold,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                        if (empresa.actualizada) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(color = SuccessGreenBg, shape = RoundedCornerShape(100.dp)) {
                                                Text("ACTUALIZADA", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(color = BorderLight)
                            Spacer(modifier = Modifier.height(24.dp))

                            // Información de Contacto
                            InfoRow(Icons.Outlined.Mail, "Correo", empresa.email)
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(Icons.Outlined.Phone, "Teléfono", empresa.telefono ?: "No registrado")
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(Icons.Outlined.LocationOn, "Ciudad", empresa.ciudad ?: "No especificada")
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow(Icons.Outlined.Category, "Sector", empresa.sector ?: "No especificado")

                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Descripción
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF9FAFB))
                                    .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Text("Descripción de la empresa", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = empresa.descripcion ?: "Sin descripción proporcionada.",
                                    color = TextMain, fontSize = 14.sp, lineHeight = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(color = BorderLight)
                            Spacer(modifier = Modifier.height(24.dp))

                            // Documentos (Si hubiera una URL o indicador)
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Outlined.Description, null, tint = AccentIndigo, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Documentación Adjunta", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextMain)
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = { /* Lógica para ver PDF/Docs */ }) {
                                    Text("VER ARCHIVOS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentIndigo)
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Sección de Resolución
                            Text("MOTIVO DE LA DECISIÓN", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextSecondary)
                            Text("(OBLIGATORIO SOLO PARA RECHAZO)", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = TextSecondary.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = comentarioAdmin,
                                onValueChange = { comentarioAdmin = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Escribe aquí el comentario para la empresa...", fontSize = 14.sp) },
                                shape = RoundedCornerShape(12.dp),
                                maxLines = 4,
                                minLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentIndigo,
                                    unfocusedBorderColor = BorderLight
                                )
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Botones de Acción
                            Button(
                                onClick = {
                                    // APROBACIÓN: No requiere comentario obligatorio
                                    viewModel.aprobarEmpresa(empresa.idEmpresaPendiente, comentarioAdmin)
                                    comentarioAdmin = ""
                                },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                            ) {
                                Icon(Icons.Default.Check, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Aprobar Registro", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    // RECHAZO: Sí requiere comentario obligatorio
                                    if (comentarioAdmin.isBlank()) {
                                        viewModel.showAdminMessage("Debes ingresar el motivo del rechazo.", AdminMessageType.WARNING)
                                    } else {
                                        viewModel.rechazarEmpresa(empresa, comentarioAdmin)
                                        comentarioAdmin = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                            ) {
                                Icon(Icons.Default.Close, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (empresa.rechazos >= 2) "Rechazar y Eliminar Definitivamente" else "Rechazar Solicitud",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Banner de Mensaje - CORREGIDO: Ahora considera el padding para ser visible arriba
            AdminMessageBanner(
                state = adminMessage,
                onDismiss = { viewModel.dismissAdminMessage() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = padding.calculateTopPadding() + 12.dp)
            )

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = padding.calculateTopPadding()),
                    color = AccentIndigo,
                    trackColor = Color.Transparent
                )
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.width(80.dp))
        Text(
            text = value,
            color = TextMain,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
