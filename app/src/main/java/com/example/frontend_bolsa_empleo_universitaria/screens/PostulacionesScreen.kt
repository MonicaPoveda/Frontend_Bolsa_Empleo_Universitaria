package com.example.frontend_bolsa_empleo_universitaria.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontend_bolsa_empleo_universitaria.model.Postulacion
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PostulacionesViewModel
import java.util.Locale

private val AzulFondoHeader = Color(0xFF0D253F)
private val AzulTarjetaStats = Color(0xFF1E3A5F)
private val FondoPantalla = Color(0xFFF8F9FB)
private val ColorTextoPrimario = Color(0xFF1A1C1E)
private val ColorTextoSecundario = Color(0xFF74777F)

@Composable
fun PostulacionesScreen(
    idUsuario: Long,
    onBack: () -> Unit,
    onEmpresaClick: (Long) -> Unit,
    onNavigateToProfile: () -> Unit = {},
    viewModel: PostulacionesViewModel = viewModel()
) {
    val postulaciones by viewModel.postulaciones
    val loading by viewModel.loading
    var filtroSeleccionado by remember { mutableStateOf("Todas") }

    LaunchedEffect(idUsuario) {
        viewModel.cargarPostulaciones(idUsuario)
    }

    val postulacionesFiltradas = when (filtroSeleccionado) {
        "Pendiente" -> postulaciones.filter { it.estado.equals("PENDIENTE", true) }
        "Aceptada" -> postulaciones.filter { it.estado.equals("ACEPTADO", true) || it.estado.equals("ACEPTADA", true) }
        "Rechazada" -> postulaciones.filter { it.estado.equals("RECHAZADO", true) || it.estado.equals("RECHAZADA", true) }
        else -> postulaciones
    }

    val total = postulaciones.size
    val pendientes = postulaciones.count { it.estado.equals("PENDIENTE", true) }
    val aceptadas = postulaciones.count { it.estado.equals("ACEPTADO", true) || it.estado.equals("ACEPTADA", true) }

    Scaffold(
        containerColor = FondoPantalla,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                listOf(
                    Triple("Buscar", Icons.Default.Search, "busqueda"),
                    Triple("Postulaciones", Icons.Default.AssignmentTurnedIn, "postulaciones"),
                    Triple("Perfil", Icons.Default.Person, "perfil")
                ).forEach { (label, icon, route) ->
                    NavigationBarItem(
                        selected = route == "postulaciones",
                        onClick = {
                            when (route) {
                                "busqueda" -> onBack()
                                "perfil" -> onNavigateToProfile()
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AzulFondoHeader,
                            selectedTextColor = AzulFondoHeader,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = AzulTarjetaStats.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            HeaderConStats(total = total, pendientes = pendientes, aceptadas = aceptadas, onBack = onBack)

            FiltrosPostulaciones(
                seleccionado = filtroSeleccionado,
                onSeleccionar = { filtroSeleccionado = it }
            )

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AzulFondoHeader)
                }
            } else if (postulacionesFiltradas.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay postulaciones", color = ColorTextoSecundario)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(postulacionesFiltradas) { postulacion ->
                        PostulacionDesignCard(
                            postulacion = postulacion,
                            onEmpresaClick = onEmpresaClick,
                            onDesistirClick = {
                                postulacion.idPostulacion?.let { viewModel.desistirDePostulacion(it, idUsuario) }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderConStats(total: Int, pendientes: Int, aceptadas: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AzulFondoHeader, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f))) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text("Mis Postulaciones", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(label = "Total", value = total.toString(), modifier = Modifier.weight(1f))
            StatCard(label = "Pendientes", value = pendientes.toString(), colorValue = Color(0xFFFFB74D), modifier = Modifier.weight(1f))
            StatCard(label = "Aceptadas", value = aceptadas.toString(), colorValue = Color(0xFF81C784), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, colorValue: Color = Color.White, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = AzulTarjetaStats)) {
        Column(Modifier.padding(12.dp)) {
            Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            Text(value, color = colorValue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FiltrosPostulaciones(seleccionado: String, onSeleccionar: (String) -> Unit) {
    val opciones = listOf("Todas", "Pendiente", "Aceptada", "Rechazada")
    LazyRow(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(opciones) { opcion ->
            FilterChip(
                selected = seleccionado == opcion,
                onClick = { onSeleccionar(opcion) },
                label = { Text(opcion) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AzulFondoHeader, selectedLabelColor = Color.White, containerColor = Color.White, labelColor = ColorTextoSecundario),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
fun PostulacionDesignCard(
    postulacion: Postulacion,
    onEmpresaClick: (Long) -> Unit,
    onDesistirClick: () -> Unit
) {
    val oferta = postulacion.oferta
    val empresa = postulacion.empresa

    val colorEstado = when (postulacion.estado.uppercase()) {
        "ACEPTADO", "ACEPTADA" -> Color(0xFF4CAF50)
        "RECHAZADO", "RECHAZADA" -> Color(0xFFF44336)
        else -> Color(0xFF2196F3)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(6.dp).fillMaxHeight().background(colorEstado))

            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = oferta?.titulo ?: "Cargando oferta...",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextoPrimario
                        )
                        Text(
                            text = empresa?.nombre ?: postulacion.nombreEmpresa,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2D6BE4),
                            modifier = Modifier.clickable { oferta?.idEmpresa?.let { onEmpresaClick(it) } }
                        )
                        if (empresa != null) {
                            Text(text = "${empresa.sector} • ${empresa.ciudad}", fontSize = 12.sp, color = ColorTextoSecundario)
                        }
                    }
                    StatusBadgeDesign(postulacion.estado)
                }

                if (oferta != null && oferta.salario > 0) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachMoney, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        Text(text = "${oferta.salario.toInt()} USD", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF1F3F4))
                Spacer(Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, null, tint = ColorTextoSecundario, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(postulacion.fechaPostulacion, fontSize = 12.sp, color = ColorTextoSecundario)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (postulacion.estado.uppercase() == "PENDIENTE") {
                            Text(
                                text = "Desistir",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .clickable { onDesistirClick() }
                            )
                        }

                        Surface(color = Color(0xFFF0F5FF), shape = RoundedCornerShape(6.dp)) {
                            val mod = oferta?.modalidad?.lowercase()?.replaceFirstChar { it.titlecase() } ?: "Remoto"
                            Text(text = mod, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D6BE4))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadgeDesign(estado: String) {
    val (bgColor, textColor) = when (estado.uppercase()) {
        "ACEPTADO", "ACEPTADA" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "RECHAZADO", "RECHAZADA" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        "ENTREVISTA" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        else -> Color(0xFFFFF3E0) to Color(0xFFEF6C00)
    }
    Surface(color = bgColor, shape = RoundedCornerShape(8.dp)) {
        Text(text = estado, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}