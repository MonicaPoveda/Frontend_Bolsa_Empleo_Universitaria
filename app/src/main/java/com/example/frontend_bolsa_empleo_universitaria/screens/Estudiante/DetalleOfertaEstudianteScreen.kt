package com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralResponse
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModelFactory
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PostulacionViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PostulacionViewModelFactory
import kotlinx.coroutines.launch

private val BlueStart = Color(0xFF0056D2)
private val BlueEnd = Color(0xFF007BFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOfertaEstudianteScreen(
    ofertaId: Long,
    navController: NavController
) {
    val context = LocalContext.current
    val tokenManager = remember { Token(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // ✅ 1. Crear ViewModel de ofertas con su factory
    val repository = remember { OfertasRepository(RetrofitClient.ofertaLaboralApi) }
    val ofertasViewModel: OfertasViewModel = viewModel(
        factory = OfertasViewModelFactory(repository)
    )

    // ✅ 2. Crear ViewModel de postulaciones con su factory
    val postulacionViewModel: PostulacionViewModel = viewModel(
        factory = PostulacionViewModelFactory(RetrofitClient.postulacionApi)
    )

    var oferta by remember { mutableStateOf<OfertaLaboralResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showPostularDialog by remember { mutableStateOf(false) }
    var postulando by remember { mutableStateOf(false) }

    // Cargar oferta al iniciar
    LaunchedEffect(ofertaId) {
        isLoading = true
        // Buscar en las ofertas ya cargadas en ViewModel (si las hay)
        var encontrada = ofertasViewModel.ofertas.value.find { it.idOferta == ofertaId }
        if (encontrada == null) {
            // Si no está, cargar todas y buscar
            try {
                val repo = OfertasRepository(RetrofitClient.ofertaLaboralApi)
                val todas = repo.listarTodas()
                encontrada = todas.find { it.idOferta == ofertaId }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        oferta = encontrada
        isLoading = false
    }

    // Diálogo de confirmación de postulación
    if (showPostularDialog && oferta != null) {
        AlertDialog(
            onDismissRequest = { showPostularDialog = false },
            icon = {
                Icon(Icons.Default.Work, contentDescription = null, tint = BlueStart, modifier = Modifier.size(40.dp))
            },
            title = { Text("Confirmar postulación", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Deseas postularte a la oferta \"${oferta!!.titulo}\"?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            showPostularDialog = false
                            postulando = true
                            val userId = tokenManager.getUserId()
                            if (userId != null) {
                                // ✅ Usar el postulacionViewModel correctamente
                                postulacionViewModel.postularse(
                                    idUsuario = userId,
                                    idOferta = ofertaId,
                                    onSuccess = {
                                        postulando = false
                                        scope.launch {
                                            snackbarHostState.showSnackbar("✅ ¡Postulación exitosa!")
                                        }
                                    },
                                    onError = { error ->
                                        postulando = false
                                        scope.launch {
                                            snackbarHostState.showSnackbar("❌ $error")
                                        }
                                    }
                                )
                            } else {
                                postulando = false
                                snackbarHostState.showSnackbar("❌ Sesión no válida. Inicia sesión nuevamente.")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueStart)
                ) {
                    if (postulando) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    else Text("Sí, postularme")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPostularDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Oferta", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
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
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No se encontró la oferta", color = Color.Gray)
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Volver")
                        }
                    }
                }
            }
            else -> {
                val o = oferta!!
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Brush.verticalGradient(listOf(BlueStart, BlueEnd)), RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                                .padding(24.dp)
                        ) {
                            Column {
                                Text(text = o.titulo.ifBlank { "Sin título" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = o.area.ifBlank { "Área no especificada" }, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            DetalleInfoChipEstudiante(modifier = Modifier.weight(1f), icon = Icons.Default.AttachMoney, label = "Salario", value = if (o.salario > 0) "$${o.salario.toInt()}/mes" else "No especificado", color = Color(0xFF2E7D32))
                            DetalleInfoChipEstudiante(modifier = Modifier.weight(1f), icon = Icons.Default.Work, label = "Modalidad", value = o.modalidad.ifBlank { "No especificada" }, color = BlueStart)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            DetalleInfoChipEstudiante(modifier = Modifier.weight(1f), icon = Icons.Default.CalendarToday, label = "Publicada", value = formatFecha(o.fechaPublicacion), color = Color(0xFF6A1B9A))
                            DetalleInfoChipEstudiante(modifier = Modifier.weight(1f), icon = Icons.Default.EventBusy, label = "Cierre", value = formatFecha(o.fechaCierre), color = Color(0xFFE65100))
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = BlueStart, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Descripción del puesto", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = o.descripcion.ifBlank { "Sin descripción" }, color = Color(0xFF444444), fontSize = 14.sp, lineHeight = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Surface(shadowElevation = 8.dp, color = Color.White) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Button(
                                onClick = { showPostularDialog = true },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BlueStart),
                                enabled = !postulando
                            ) {
                                if (postulando) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                                else {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Postularme ahora", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
private fun DetalleInfoChipEstudiante(modifier: Modifier = Modifier, icon: ImageVector, label: String, value: String, color: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

private fun formatFecha(fecha: String): String {
    return try {
        if (fecha.isBlank()) "No especificada"
        else {
            val partes = fecha.split("-")
            if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fecha
        }
    } catch (e: Exception) { fecha }
}