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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel

private val BlueStart = Color(0xFF0056D2)
private val BlueEnd = Color(0xFF007BFF)
private val GreenActive = Color(0xFF2E7D32)
private val GreenActiveBg = Color(0xFFE8F5E9)
private val RedInactive = Color(0xFFE53935)
private val RedInactiveBg = Color(0xFFFFEBEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOfertaEmpresaScreen(
    ofertaId: Long,
    navController: NavController,
    viewModel: OfertasViewModel  // ← recibe el viewModel que ya tiene las ofertas
) {
    // Buscar la oferta directamente de la lista ya cargada en el ViewModel
    val oferta by remember {
        derivedStateOf { viewModel.ofertasEmpresa.value.find { it.idOferta == ofertaId } }
    }

    val isLoading = viewModel.loading.value
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Diálogo de confirmación
    if (showConfirmDialog && oferta != null) {
        val esActiva = oferta!!.estado
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = {
                Icon(
                    if (esActiva) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = if (esActiva) RedInactive else GreenActive,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    if (esActiva) "¿Cerrar oferta?" else "¿Activar oferta?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    if (esActiva)
                        "La oferta dejará de ser visible para los estudiantes. Puedes reactivarla después."
                    else
                        "La oferta volverá a ser visible para los estudiantes."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.actualizarEstadoOferta(ofertaId, !esActiva)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (esActiva) RedInactive else GreenActive
                    )
                ) {
                    Text(if (esActiva) "Sí, cerrar" else "Sí, activar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle de Oferta",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueStart)
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BlueStart)
                }
            }

            oferta == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No se encontró la oferta", color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Volver")
                        }
                    }
                }
            }

            else -> {
                val o = oferta!!
                val esActiva = o.estado

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Contenido scrolleable
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header gradiente
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(listOf(BlueStart, BlueEnd)),
                                    RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                                )
                                .padding(24.dp)
                        ) {
                            Column {
                                // Badge estado
                                Surface(
                                    color = if (esActiva) GreenActiveBg else RedInactiveBg,
                                    shape = RoundedCornerShape(50.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 4.dp
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(RoundedCornerShape(50.dp))
                                                .background(if (esActiva) GreenActive else RedInactive)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (esActiva) "Activa" else "Cerrada",
                                            color = if (esActiva) GreenActive else RedInactive,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = o.titulo,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = o.area,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Info rápida fila 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DetalleInfoChip(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.AttachMoney,
                                label = "Salario",
                                value = "$${o.salario.toInt()}/mes",
                                color = GreenActive
                            )
                            DetalleInfoChip(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.WorkOutline,
                                label = "Modalidad",
                                value = o.modalidad.ifBlank { "Presencial" },
                                color = BlueStart
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Info rápida fila 2
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DetalleInfoChip(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.CalendarToday,
                                label = "Publicada",
                                value = formatFecha(o.fechaPublicacion),
                                color = Color(0xFF6A1B9A)
                            )
                            DetalleInfoChip(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.EventBusy,
                                label = "Cierre",
                                value = formatFecha(o.fechaCierre),
                                color = Color(0xFFE65100)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Descripción
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = null,
                                        tint = BlueStart,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Descripción del puesto",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = o.descripcion,
                                    color = Color(0xFF444444),
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Botón fijo abajo
                    Surface(
                        shadowElevation = 8.dp,
                        color = Color.White
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                    color = BlueStart
                                )
                            } else {
                                Button(
                                    onClick = { showConfirmDialog = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (esActiva) RedInactive else GreenActive
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (esActiva) Icons.Default.Lock
                                        else Icons.Default.LockOpen,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (esActiva) "Cerrar Oferta" else "Activar Oferta",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetalleInfoChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = label, fontSize = 11.sp, color = Color.Gray)
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

private fun formatFecha(fecha: String): String {
    return try {
        val partes = fecha.split("-")
        if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fecha
    } catch (e: Exception) {
        fecha
    }
}