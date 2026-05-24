package com.example.frontend_bolsa_empleo_universitaria.screens.Empresa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.model.SeguimientoPostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PostulacionViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PostulacionViewModelFactory
import com.example.frontend_bolsa_empleo_universitaria.repository.PostulacionRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.SeguimientoPostulacionRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.components.ProfilePhotoDisplay
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoColors
import com.example.frontend_bolsa_empleo_universitaria.utils.ArchivoUrls
import kotlinx.coroutines.launch

private val BlueGradientStart = UniEmpleoColors.Blue
private val BackgroundGray = UniEmpleoColors.Background

fun formatFecha(fecha: String): String {
    return try {
        if (fecha.isBlank()) "No especificada"
        else {
            val partes = fecha.split("-")
            if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fecha
        }
    } catch (e: Exception) {
        fecha
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostulantesOfertaScreen(
    ofertaId: Long,
    ofertaTitulo: String,
    navController: NavController
) {
    val postulacionRepository = remember {
        PostulacionRepository(
            RetrofitClient.postulacionApi,
            RetrofitClient.usuarioApi,
            RetrofitClient.ofertaLaboralApi
        )
    }
    val seguimientoRepository = remember { SeguimientoPostulacionRepository(RetrofitClient.seguimientoPostulacionApi) }
    val viewModel: PostulacionViewModel = viewModel(
        factory = PostulacionViewModelFactory(postulacionRepository, seguimientoRepository,RetrofitClient.postulacionApi )
    )

    val postulaciones by viewModel.postulaciones
    val isLoading by viewModel.loading
    val errorMessage by viewModel.error
    val isUpdating by viewModel.updating

    // Estado para el filtro de estado
    var filtroEstado by remember { mutableStateOf("TODAS") }

    LaunchedEffect(ofertaId) {
        viewModel.cargarPostulacionesPorOferta(ofertaId)
    }

    fun actualizarEstado(postulacion: PostulacionDto, nuevoEstado: String) {
        viewModel.actualizarEstado(postulacion, nuevoEstado)
    }

    fun getEstadoColor(estado: String): Color {
        return when (estado.uppercase()) {
            "PENDIENTE" -> Color(0xFFFF9800)
            "EN_REVISION" -> Color(0xFF2196F3)
            "ACEPTADA" -> Color(0xFF4CAF50)
            "RECHAZADA" -> Color(0xFFF44336)
            else -> Color.Gray
        }
    }

    fun getEstadoBadge(estado: String): String {
        return when (estado.uppercase()) {
            "PENDIENTE" -> "Pendiente"
            "EN_REVISION" -> "En revisión"
            "ACEPTADA" -> "Aceptada"
            "RECHAZADA" -> "Rechazada"
            else -> estado
        }
    }

    // Filtrar postulaciones según el estado seleccionado
    val postulacionesFiltradas = when (filtroEstado) {
        "PENDIENTE" -> postulaciones.filter { it.estado.uppercase() == "PENDIENTE" }
        "EN_REVISION" -> postulaciones.filter { it.estado.uppercase() == "EN_REVISION" }
        "ACEPTADA" -> postulaciones.filter { it.estado.uppercase() == "ACEPTADA" }
        "RECHAZADA" -> postulaciones.filter { it.estado.uppercase() == "RECHAZADA" }
        else -> postulaciones
    }

    // Calcular estadísticas
    val total = postulaciones.size
    val pendientes = postulaciones.count { it.estado.uppercase() == "PENDIENTE" }
    val enRevision = postulaciones.count { it.estado.uppercase() == "EN_REVISION" }
    val aceptadas = postulaciones.count { it.estado.uppercase() == "ACEPTADA" }
    val rechazadas = postulaciones.count { it.estado.uppercase() == "RECHAZADA" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Postulaciones",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            ofertaTitulo,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueGradientStart)
            )
        }
    ) { padding ->
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
                            Text("Cargando postulaciones...", color = Color.Gray)
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
                            Text(errorMessage!!, color = Color.Red)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(containerColor = BlueGradientStart)
                            ) {
                                Text("Volver")
                            }
                        }
                    }
                }

                else -> {
                    // Tarjetas de filtros (Todas, Pendientes, En revisión, Aceptadas, Rechazadas)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FiltroEstadoCard(
                            modifier = Modifier.weight(1f),
                            titulo = "Todas",
                            numero = total,
                            color = BlueGradientStart,
                            isSelected = filtroEstado == "TODAS",
                            onClick = { filtroEstado = "TODAS" }
                        )
                        FiltroEstadoCard(
                            modifier = Modifier.weight(1f),
                            titulo = "Pendientes",
                            numero = pendientes,
                            color = Color(0xFFFF9800),
                            isSelected = filtroEstado == "PENDIENTE",
                            onClick = { filtroEstado = "PENDIENTE" }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FiltroEstadoCard(
                            modifier = Modifier.weight(1f),
                            titulo = "En revisión",
                            numero = enRevision,
                            color = Color(0xFF2196F3),
                            isSelected = filtroEstado == "EN_REVISION",
                            onClick = { filtroEstado = "EN_REVISION" }
                        )
                        FiltroEstadoCard(
                            modifier = Modifier.weight(1f),
                            titulo = "Aceptadas",
                            numero = aceptadas,
                            color = Color(0xFF4CAF50),
                            isSelected = filtroEstado == "ACEPTADA",
                            onClick = { filtroEstado = "ACEPTADA" }
                        )
                        FiltroEstadoCard(
                            modifier = Modifier.weight(1f),
                            titulo = "Rechazadas",
                            numero = rechazadas,
                            color = Color(0xFFF44336),
                            isSelected = filtroEstado == "RECHAZADA",
                            onClick = { filtroEstado = "RECHAZADA" }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Título de la lista filtrada
                    Text(
                        text = when (filtroEstado) {
                            "PENDIENTE" -> "Postulaciones Pendientes"
                            "EN_REVISION" -> "Postulaciones en Revisión"
                            "ACEPTADA" -> "Postulaciones Aceptadas"
                            "RECHAZADA" -> "Postulaciones Rechazadas"
                            else -> "Todas las Postulaciones"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Lista de postulantes filtrada
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (postulacionesFiltradas.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.People,
                                            contentDescription = null,
                                            modifier = Modifier.size(64.dp),
                                            tint = Color.LightGray
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = when (filtroEstado) {
                                                "PENDIENTE" -> "No hay postulaciones pendientes"
                                                "EN_REVISION" -> "No hay postulaciones en revisión"
                                                "ACEPTADA" -> "No hay postulaciones aceptadas"
                                                "RECHAZADA" -> "No hay postulaciones rechazadas"
                                                else -> "No hay postulaciones"
                                            },
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        } else {
                            items(postulacionesFiltradas) { postulacion ->
                                PostulanteCardSimple(
                                    postulacion = postulacion,
                                    onEstadoChange = { nuevoEstado ->
                                        actualizarEstado(postulacion, nuevoEstado)
                                    },
                                    getEstadoColor = { getEstadoColor(it) },
                                    getEstadoBadge = { getEstadoBadge(it) },
                                    isUpdating = isUpdating,
                                    onVerPerfil = { idUsuario, nombre, email, idOferta ->
                                        // Navegar al perfil del estudiante
                                        navController.navigate("perfil_estudiante_empresa/$idUsuario")
                                    }
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun FiltroEstadoCard(
    modifier: Modifier = Modifier,
    titulo: String,
    numero: Int,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color else Color.White,
            contentColor = if (isSelected) Color.White else color
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 3.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else Color.DarkGray,
                maxLines = 1
            )
            Text(
                text = numero.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else color
            )
        }
    }
}

@Composable
fun PostulanteCardSimple(
    postulacion: PostulacionDto,
    onEstadoChange: (String) -> Unit,
    getEstadoColor: (String) -> Color,
    getEstadoBadge: (String) -> String,
    isUpdating: Boolean,
    onVerPerfil: (Long, String, String, String) -> Unit = { _, _, _, _ -> } // ← NUEVO: callback con datos del estudiante
) {
    var showEstadoMenu by remember { mutableStateOf(false) }
    val estados = listOf("PENDIENTE", "EN_REVISION", "ACEPTADA", "RECHAZADA")
    val estadoBadge = getEstadoBadge(postulacion.estado)
    val estadoColor = getEstadoColor(postulacion.estado)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con avatar, nombre, email y botón ver perfil
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfilePhotoDisplay(
                    photoUrl = ArchivoUrls.fotoUsuario(postulacion.idUsuario),
                    size = 48,
                    placeholderIcon = Icons.Default.Person,
                    modifier = Modifier.background(Color(0xFFE3F2FD), CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = postulacion.nombreEstudiante.ifBlank { "Estudiante #${postulacion.idUsuario}" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = postulacion.emailEstudiante.ifBlank { "Email no disponible" },
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                // ✅ BOTÓN VER PERFIL (ícono de ojo)
                IconButton(
                    onClick = {
                        onVerPerfil(
                            postulacion.idUsuario,
                            postulacion.nombreEstudiante,
                            postulacion.emailEstudiante,
                            postulacion.idOferta.toString()
                        )
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = "Ver perfil completo",
                        tint = BlueGradientStart,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "Fecha",
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Postulado: ${formatFecha(postulacion.fechaPostulacion)}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenedor para el selector de estado con menú interno
            Column {
                Surface(
                    color = estadoColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEstadoMenu = !showEstadoMenu }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(estadoColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Estado: $estadoBadge",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = estadoColor
                            )
                        }
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Cambiar estado",
                            modifier = Modifier.size(20.dp),
                            tint = estadoColor
                        )
                    }
                }

                // Menú desplegable dentro de la tarjeta
                if (showEstadoMenu) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            estados.forEach { estado ->
                                val isSelected = postulacion.estado.uppercase() == estado
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (!isUpdating && !isSelected) {
                                                onEstadoChange(estado)
                                            }
                                            showEstadoMenu = false
                                        },
                                    color = if (isSelected) getEstadoColor(estado).copy(alpha = 0.1f) else Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(getEstadoColor(estado))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = getEstadoBadge(estado),
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) getEstadoColor(estado) else Color.Black
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.weight(1f))
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Seleccionado",
                                                modifier = Modifier.size(16.dp),
                                                tint = getEstadoColor(estado)
                                            )
                                        }
                                    }
                                }
                                if (estado != estados.last()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = Color.LightGray.copy(alpha = 0.3f),
                                        thickness = 0.5.dp
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