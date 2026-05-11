package com.example.frontend_bolsa_empleo_universitaria.screens.Empresa

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralRequest
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralResponse
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoColors
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

private val BlueStart = UniEmpleoColors.Blue
private val BackgroundGray = UniEmpleoColors.Background

// Función para formatear fechas
private fun formaFecha(fecha: String): String {
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

// Función para obtener la fecha actual en formato yyyy-MM-dd
private fun getFechaActual(): String {
    return DateFormat.format("yyyy-MM-dd", Date()).toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOfertaEmpresaScreen(
    ofertaId: Long,
    navController: NavController,
    viewModel: OfertasViewModel = viewModel()
) {
    var oferta by remember { mutableStateOf<OfertaLaboralResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showCloseConfirmDialog by remember { mutableStateOf(false) }
    var showActivateConfirmDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Estados editables
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var salario by remember { mutableStateOf("") }
    var modalidad by remember { mutableStateOf("") }
    var fechaPublicacion by remember { mutableStateOf("") }
    var fechaCierre by remember { mutableStateOf("") }

    // Estados para selectores de fecha de cierre
    var selectedYear by remember { mutableStateOf(2026) }
    var selectedMonth by remember { mutableStateOf(5) }
    var selectedDay by remember { mutableStateOf(30) }

    // Cargar la oferta
    LaunchedEffect(ofertaId) {
        isLoading = true
        delay(500)

        val encontrada = viewModel.ofertas.value.find { it.idOferta == ofertaId }
        if (encontrada != null) {
            oferta = encontrada
            titulo = encontrada.titulo
            descripcion = encontrada.descripcion
            area = encontrada.area
            salario = encontrada.salario.toString()
            modalidad = encontrada.modalidad
            fechaPublicacion = encontrada.fechaPublicacion
            fechaCierre = encontrada.fechaCierre

            try {
                val partes = encontrada.fechaCierre.split("-")
                if (partes.size == 3) {
                    selectedYear = partes[0].toInt()
                    selectedMonth = partes[1].toInt()
                    selectedDay = partes[2].toInt()
                }
            } catch (e: Exception) {}
        } else {
            try {
                val repo = com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository(
                    com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient.ofertaLaboralApi
                )
                val todas = repo.listarTodas()
                val encontradaEnRepo = todas.find { it.idOferta == ofertaId }
                if (encontradaEnRepo != null) {
                    oferta = encontradaEnRepo
                    titulo = encontradaEnRepo.titulo
                    descripcion = encontradaEnRepo.descripcion
                    area = encontradaEnRepo.area
                    salario = encontradaEnRepo.salario.toString()
                    modalidad = encontradaEnRepo.modalidad
                    fechaPublicacion = encontradaEnRepo.fechaPublicacion
                    fechaCierre = encontradaEnRepo.fechaCierre
                }
            } catch (e: Exception) {}
        }
        isLoading = false
    }

    // Listas para selectores
    val years = (2020..2030).toList()
    val monthNames = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")

    val maxDays = remember(selectedYear, selectedMonth) {
        when (selectedMonth) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if ((selectedYear % 4 == 0 && selectedYear % 100 != 0) || selectedYear % 400 == 0) 29 else 28
            else -> 31
        }
    }
    val days = (1..maxDays).toList()
    val fechaCierreSeleccionada = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, selectedDay)

    LaunchedEffect(selectedYear, selectedMonth, selectedDay) {
        if (isEditing) {
            fechaCierre = fechaCierreSeleccionada
        }
    }

    // Diálogo de confirmación para cerrar oferta
    if (showCloseConfirmDialog && oferta != null && !isEditing) {
        AlertDialog(
            onDismissRequest = { showCloseConfirmDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    "Cerrar oferta",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53935),
                    fontSize = 20.sp
                )
            },
            text = {
                Column {
                    Text(
                        "¿Estás seguro de que deseas cerrar esta oferta?",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "La oferta dejará de ser visible para los estudiantes.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCloseConfirmDialog = false
                        isSaving = true
                        val ofertaActual = oferta!!
                        val ofertaActualizada = OfertaLaboralRequest(
                            titulo = ofertaActual.titulo,
                            descripcion = ofertaActual.descripcion,
                            area = ofertaActual.area,
                            salario = ofertaActual.salario,
                            modalidad = ofertaActual.modalidad,
                            fechaPublicacion = getFechaActual(),  // ← Actualizar con fecha actual
                            fechaCierre = fechaCierreSeleccionada,
                            estado = false,
                            idEmpresa = ofertaActual.idEmpresa
                        )
                        scope.launch {
                            val repo = com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository(
                                com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient.ofertaLaboralApi
                            )
                            val resultado = repo.actualizarOferta(ofertaId, ofertaActualizada)
                            isSaving = false
                            if (resultado != null) {
                                viewModel.cargarActivas()
                                viewModel.cargarOfertasPorEmpresa(ofertaActual.idEmpresa)
                                oferta = resultado
                                println("✅ Oferta cerrada exitosamente")
                            } else {
                                println("❌ Error al cerrar la oferta")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloseConfirmDialog = false }) {
                    Text("Cancelar", fontWeight = FontWeight.Medium, color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Diálogo de confirmación para activar oferta
    if (showActivateConfirmDialog && oferta != null && !isEditing) {
        AlertDialog(
            onDismissRequest = { showActivateConfirmDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    "Activar oferta",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    fontSize = 20.sp
                )
            },
            text = {
                Column {
                    Text(
                        "¿Estás seguro de que deseas activar esta oferta?",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "La oferta volverá a ser visible para los estudiantes.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showActivateConfirmDialog = false
                        isSaving = true
                        val ofertaActual = oferta!!
                        val ofertaActualizada = OfertaLaboralRequest(
                            titulo = ofertaActual.titulo,
                            descripcion = ofertaActual.descripcion,
                            area = ofertaActual.area,
                            salario = ofertaActual.salario,
                            modalidad = ofertaActual.modalidad,
                            fechaPublicacion = getFechaActual(),  // ← Actualizar con fecha actual
                            fechaCierre = fechaCierreSeleccionada,
                            estado = true,
                            idEmpresa = ofertaActual.idEmpresa
                        )
                        scope.launch {
                            val repo = com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository(
                                com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient.ofertaLaboralApi
                            )
                            val resultado = repo.actualizarOferta(ofertaId, ofertaActualizada)
                            isSaving = false
                            if (resultado != null) {
                                viewModel.cargarActivas()
                                viewModel.cargarOfertasPorEmpresa(ofertaActual.idEmpresa)
                                oferta = resultado
                                println("✅ Oferta activada exitosamente")
                            } else {
                                println("❌ Error al activar la oferta")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Activar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showActivateConfirmDialog = false }) {
                    Text("Cancelar", fontWeight = FontWeight.Medium, color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Editar Oferta" else "Detalle de Oferta",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing) {
                            isEditing = false
                            oferta?.let { o ->
                                titulo = o.titulo
                                descripcion = o.descripcion
                                area = o.area
                                salario = o.salario.toString()
                                modalidad = o.modalidad
                                fechaPublicacion = o.fechaPublicacion
                                fechaCierre = o.fechaCierre
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    if (!isEditing && !isLoading && oferta != null) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                        }
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
                        Icon(Icons.Default.ErrorOutline, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No se encontró la oferta", color = Color.Gray)
                        Button(onClick = { navController.popBackStack() }) { Text("Volver") }
                    }
                }
            }
            else -> {
                val ofertaActual = oferta!!
                val esActiva = ofertaActual.estado

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Estado badge (solo en vista)
                        if (!isEditing) {
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    color = if (esActiva) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(50.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(RoundedCornerShape(50.dp))
                                                .background(if (esActiva) Color(0xFF2E7D32) else Color(0xFFE53935))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (esActiva) "Activa" else "Cerrada",
                                            color = if (esActiva) Color(0xFF2E7D32) else Color(0xFFE53935),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Título
                        if (isEditing) {
                            OutlinedTextField(
                                value = titulo,
                                onValueChange = { titulo = it },
                                label = { Text("Título de la oferta") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueStart,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )
                        } else {
                            Text(titulo.ifBlank { "Sin título" }, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Área
                        if (isEditing) {
                            OutlinedTextField(
                                value = area,
                                onValueChange = { area = it },
                                label = { Text("Área / Cargo") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueStart,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )
                        } else {
                            Text(area.ifBlank { "Área no especificada" }, fontSize = 14.sp, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Salario y Modalidad
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (isEditing) {
                                OutlinedTextField(
                                    value = salario,
                                    onValueChange = { salario = it },
                                    label = { Text("Salario (USD)") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BlueStart,
                                        unfocusedBorderColor = Color.Gray,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black
                                    )
                                )
                                OutlinedTextField(
                                    value = modalidad,
                                    onValueChange = { modalidad = it },
                                    label = { Text("Modalidad") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BlueStart,
                                        unfocusedBorderColor = Color.Gray,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black
                                    )
                                )
                            } else {
                                InfoChip(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.AttachMoney,
                                    label = "Salario",
                                    value = if (salario.toDoubleOrNull() != null && salario.toDouble() > 0) "$${salario.toDouble().toInt()}/mes" else "No especificado"
                                )
                                InfoChip(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.WorkOutline,
                                    label = "Modalidad",
                                    value = modalidad.ifBlank { "No especificada" }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Fecha de publicación (solo en modo vista)
                        if (!isEditing) {
                            InfoChipFecha(
                                icon = Icons.Default.CalendarToday,
                                label = "Fecha de publicación",
                                value = formaFecha(fechaPublicacion)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Fecha de cierre
                        if (isEditing) {
                            Text("Fecha de cierre", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DateSelectorDropdown(
                                    value = selectedYear.toString(),
                                    items = years.map { it.toString() },
                                    onItemSelected = { selectedYear = it.toInt() },
                                    modifier = Modifier.weight(1f),
                                    label = "Año"
                                )
                                DateSelectorDropdown(
                                    value = monthNames[selectedMonth - 1],
                                    items = monthNames,
                                    onItemSelected = { selectedMonth = monthNames.indexOf(it) + 1 },
                                    modifier = Modifier.weight(1f),
                                    label = "Mes"
                                )
                                DateSelectorDropdown(
                                    value = selectedDay.toString(),
                                    items = days.map { it.toString() },
                                    onItemSelected = { selectedDay = it.toInt() },
                                    modifier = Modifier.weight(1f),
                                    label = "Día"
                                )
                            }
                        } else {
                            InfoChipFecha(
                                icon = Icons.Default.EventBusy,
                                label = "Fecha de cierre",
                                value = formaFecha(fechaCierre)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Descripción
                        Text("Descripción del puesto", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (isEditing) {
                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                shape = RoundedCornerShape(12.dp),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlueStart,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                            ) {
                                Text(
                                    text = descripcion.ifBlank { "Sin descripción" },
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // Botón fijo abajo
                    Surface(shadowElevation = 8.dp, color = Color.White) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            if (isSaving) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = BlueStart)
                            } else {
                                Button(
                                    onClick = {
                                        if (isEditing) {
                                            // Guardar cambios
                                            isSaving = true
                                            val ofertaActualizada = OfertaLaboralRequest(
                                                titulo = titulo,
                                                descripcion = descripcion,
                                                area = area,
                                                salario = salario.toDoubleOrNull() ?: 0.0,
                                                modalidad = modalidad,
                                                fechaPublicacion = fechaPublicacion,
                                                fechaCierre = fechaCierreSeleccionada,
                                                estado = ofertaActual.estado,
                                                idEmpresa = ofertaActual.idEmpresa
                                            )
                                            scope.launch {
                                                val repo = com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository(
                                                    com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient.ofertaLaboralApi
                                                )
                                                val resultado = repo.actualizarOferta(ofertaId, ofertaActualizada)
                                                isSaving = false
                                                if (resultado != null) {
                                                    isEditing = false
                                                    viewModel.cargarActivas()
                                                    viewModel.cargarOfertasPorEmpresa(ofertaActual.idEmpresa)
                                                    oferta = resultado
                                                    // Actualizar variables locales
                                                    titulo = resultado.titulo
                                                    descripcion = resultado.descripcion
                                                    area = resultado.area
                                                    salario = resultado.salario.toString()
                                                    modalidad = resultado.modalidad
                                                    fechaPublicacion = resultado.fechaPublicacion
                                                    fechaCierre = resultado.fechaCierre
                                                }
                                            }
                                        } else {
                                            // Mostrar diálogo según el estado actual
                                            if (esActiva) {
                                                showCloseConfirmDialog = true
                                            } else {
                                                showActivateConfirmDialog = true
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isEditing) BlueStart else (if (esActiva) Color(0xFFE53935) else Color(0xFF2E7D32))
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isEditing) Icons.Default.Save else (if (esActiva) Icons.Default.Lock else Icons.Default.LockOpen),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isEditing) "Guardar Cambios" else (if (esActiva) "Cerrar Oferta" else "Activar Oferta"),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
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
fun InfoChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = BlueStart, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(label, fontSize = 11.sp, color = Color.Gray)
                Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }
        }
    }
}

@Composable
fun InfoChipFecha(
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = BlueStart, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(label, fontSize = 11.sp, color = Color.Gray)
                Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }
        }
    }
}

@Composable
fun DateSelectorDropdown(
    value: String,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = ""
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        placeholder = { if (label.isNotEmpty()) Text(label, fontSize = 12.sp) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        trailingIcon = {
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Seleccionar",
                modifier = Modifier.clickable { showDialog = true }
            )
        },
        textStyle = MaterialTheme.typography.bodySmall.copy(
            textAlign = TextAlign.Center,
            color = Color.Black
        ),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlueStart,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    "Seleccionar $label",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 250.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    items.forEach { item ->
                        TextButton(
                            onClick = {
                                onItemSelected(item)
                                showDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = item,
                                fontSize = 14.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        HorizontalDivider(color = Color.LightGray)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cerrar", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }
}
