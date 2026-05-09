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
import kotlinx.coroutines.launch

private val BlueGradientStart = Color(0xFF0056D2)
private val BackgroundGray = Color(0xFFF8FAFF)

// ✅ Función formateadora de fechas al nivel superior (sin private)
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
    // Inicializar ViewModel
    val postulacionRepository = remember { PostulacionRepository(RetrofitClient.postulacionApi) }
    val seguimientoRepository = remember { SeguimientoPostulacionRepository(RetrofitClient.seguimientoPostulacionApi) }
    val viewModel: PostulacionViewModel = viewModel(
        factory = PostulacionViewModelFactory(postulacionRepository, seguimientoRepository)
    )

    val postulaciones by viewModel.postulaciones
    val isLoading by viewModel.loading
    val errorMessage by viewModel.error
    val isUpdating by viewModel.updating
    val historial by viewModel.historial

    var selectedPostulacion by remember { mutableStateOf<PostulacionDto?>(null) }
    var showHistorialDialog by remember { mutableStateOf(false) }

    // Cargar postulaciones
    LaunchedEffect(ofertaId) {
        viewModel.cargarPostulacionesPorOferta(ofertaId)
    }

    // Función para cargar historial
    fun cargarHistorial(postulacion: PostulacionDto) {
        selectedPostulacion = postulacion
        viewModel.cargarHistorial(postulacion.idPostulacion)
        showHistorialDialog = true
    }

    // Función para actualizar estado
    fun actualizarEstado(postulacion: PostulacionDto, nuevoEstado: String) {
        viewModel.actualizarEstado(postulacion, nuevoEstado)
    }

    // Función para obtener color según estado
    fun getEstadoColor(estado: String): Color {
        return when (estado.uppercase()) {
            "PENDIENTE" -> Color(0xFFFF9800)
            "APROBADA", "ACEPTADA" -> Color(0xFF4CAF50)
            "RECHAZADA" -> Color(0xFFF44336)
            "EN_REVISION" -> Color(0xFF2196F3)
            "FINALIZADA" -> Color(0xFF9C27B0)
            else -> Color.Gray
        }
    }

    // Función para obtener badge según estado
    fun getEstadoBadge(estado: String): String {
        return when (estado.uppercase()) {
            "PENDIENTE" -> "Pendiente"
            "APROBADA", "ACEPTADA" -> "Aprobada"
            "RECHAZADA" -> "Rechazada"
            "EN_REVISION" -> "En revisión"
            "FINALIZADA" -> "Finalizada"
            else -> estado
        }
    }

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
                            Text(errorMessage!!, color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    navController.popBackStack()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BlueGradientStart)
                            ) {
                                Text("Volver")
                            }
                        }
                    }
                }

                postulaciones.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.People,
                                contentDescription = "Sin postulaciones",
                                modifier = Modifier.size(80.dp),
                                tint = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No hay postulaciones aún",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Los estudiantes aún no se han postulado a esta oferta",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    // Resumen de postulaciones
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${postulaciones.size}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BlueGradientStart
                                )
                                Text("Total", fontSize = 12.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${postulaciones.count { it.estado.uppercase() == "PENDIENTE" }}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800)
                                )
                                Text("Pendientes", fontSize = 12.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${postulaciones.count { it.estado.uppercase() == "APROBADA" || it.estado.uppercase() == "ACEPTADA" }}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                                Text("Aprobadas", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Lista de postulantes
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(postulaciones) { postulacion ->
                            PostulanteCard(
                                postulacion = postulacion,
                                onVerHistorial = { cargarHistorial(postulacion) },
                                onEstadoChange = { nuevoEstado ->
                                    actualizarEstado(postulacion, nuevoEstado)
                                },
                                getEstadoColor = { getEstadoColor(it) },
                                getEstadoBadge = { getEstadoBadge(it) },
                                isUpdating = isUpdating
                            )
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }

    // Diálogo de historial
    if (showHistorialDialog && selectedPostulacion != null && historial.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = {
                showHistorialDialog = false
                viewModel.limpiarHistorial()
            },
            title = {
                Text(
                    "Historial de cambios",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 350.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    historial.forEach { seguimiento ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.History,
                                        contentDescription = "Historial",
                                        modifier = Modifier.size(14.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        formatFecha(seguimiento.fechaCambio),
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${seguimiento.estadoAnterior} → ${seguimiento.estadoNuevo}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                if (seguimiento.observacion.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "📝 ${seguimiento.observacion}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showHistorialDialog = false
                    viewModel.limpiarHistorial()
                }) {
                    Text("Cerrar", color = BlueGradientStart)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun PostulanteCard(
    postulacion: PostulacionDto,
    onVerHistorial: () -> Unit,
    onEstadoChange: (String) -> Unit,
    getEstadoColor: (String) -> Color,
    getEstadoBadge: (String) -> String,
    isUpdating: Boolean
) {
    var showEstadoMenu by remember { mutableStateOf(false) }
    val estados = listOf("PENDIENTE", "EN_REVISION", "APROBADA", "RECHAZADA", "FINALIZADA")
    val estadoBadge = getEstadoBadge(postulacion.estado)
    val estadoColor = getEstadoColor(postulacion.estado)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con nombre y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Estudiante",
                            tint = BlueGradientStart,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = postulacion.nombreEstudiante.ifBlank { "Estudiante #${postulacion.idUsuario}" },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = postulacion.emailEstudiante.ifBlank { "Email no disponible" },
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Badge de estado clickeable
                Box {
                    Surface(
                        color = estadoColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { showEstadoMenu = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(estadoColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = estadoBadge,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = estadoColor
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Cambiar estado",
                                modifier = Modifier.size(16.dp),
                                tint = estadoColor
                            )

                        }

                        DropdownMenu(
                            expanded = showEstadoMenu,
                            onDismissRequest = { showEstadoMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            estados.forEach { estado ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(getEstadoColor(estado))
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(getEstadoBadge(estado), fontSize = 13.sp)
                                        }
                                    },
                                    onClick = {
                                        if (!isUpdating) {
                                            onEstadoChange(estado)
                                        }
                                        showEstadoMenu = false
                                    },
                                    enabled = !isUpdating
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fecha de postulación
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
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón ver historial
                    OutlinedButton(
                        onClick = onVerHistorial,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BlueGradientStart
                        )
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "Historial",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Historial", fontSize = 12.sp)
                    }

                    // Botón ver CV (placeholder)
                    Button(
                        onClick = { /* Aquí iría la navegación al CV */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueGradientStart)
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = "Ver CV",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver CV", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}