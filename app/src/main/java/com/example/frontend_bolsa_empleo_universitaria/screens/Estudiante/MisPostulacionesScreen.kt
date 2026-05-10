package com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionResponse
import com.example.frontend_bolsa_empleo_universitaria.repository.PostulacionRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.SeguimientoPostulacionRepository
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PostulacionViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PostulacionViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPostulacionesScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = remember { Token(context) }

    // ✅ CORREGIDO: Crear repositorios y ViewModel correctamente
    val postulacionRepository = remember {
        PostulacionRepository(
            RetrofitClient.postulacionApi,
            RetrofitClient.usuarioApi
        )
    }
    val seguimientoRepository = remember {
        SeguimientoPostulacionRepository(RetrofitClient.seguimientoPostulacionApi)
    }

    val viewModel: PostulacionViewModel = viewModel(
        factory = PostulacionViewModelFactory(
            postulacionRepository,
            seguimientoRepository,
            RetrofitClient.postulacionApi
        )
    )

    // ✅ Usar los StateFlow correctamente
    val postulaciones by viewModel.postulacionesEstudiante.collectAsState()
    val loading by viewModel.loadingEstudiante.collectAsState()
    val error by viewModel.errorEstudiante.collectAsState()

    var postulacionesEnriquecidas by remember { mutableStateOf<List<PostulacionEnriquecida>>(emptyList()) }
    var cargandoOfertas by remember { mutableStateOf(false) }

    // 1. Cargar postulaciones del estudiante
    LaunchedEffect(Unit) {
        val userId = tokenManager.getUserId()
        if (userId != null) {
            viewModel.cargarPostulacionesEstudiante(userId)
        }
    }

    // 2. Cuando lleguen las postulaciones, cargar ofertas y enriquecer
    LaunchedEffect(postulaciones) {
        if (postulaciones.isNotEmpty()) {
            cargandoOfertas = true
            try {
                val responseOfertas = RetrofitClient.ofertaLaboralApi.listar()
                if (responseOfertas.isSuccessful) {
                    val ofertasMap = responseOfertas.body()?.associateBy { it.idOferta } ?: emptyMap()
                    val enriquecidas = postulaciones.map { p ->
                        val oferta = ofertasMap[p.idOferta]
                        PostulacionEnriquecida(
                            idPostulacion = p.idPostulacion,
                            fechaPostulacion = p.fechaPostulacion,
                            estado = p.estado,
                            idOferta = p.idOferta,
                            tituloOferta = oferta?.titulo ?: "Oferta #${p.idOferta}",
                            area = oferta?.area ?: "",
                            salario = oferta?.salario ?: 0.0,
                            modalidad = oferta?.modalidad ?: "",
                            nombreEmpresa = "Empresa"
                        )
                    }
                    postulacionesEnriquecidas = enriquecidas
                } else {
                    postulacionesEnriquecidas = postulaciones.map { p ->
                        PostulacionEnriquecida(
                            idPostulacion = p.idPostulacion,
                            fechaPostulacion = p.fechaPostulacion,
                            estado = p.estado,
                            idOferta = p.idOferta,
                            tituloOferta = "Oferta #${p.idOferta}",
                            area = "",
                            salario = 0.0,
                            modalidad = "",
                            nombreEmpresa = ""
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                postulacionesEnriquecidas = postulaciones.map { p ->
                    PostulacionEnriquecida(
                        idPostulacion = p.idPostulacion,
                        fechaPostulacion = p.fechaPostulacion,
                        estado = p.estado,
                        idOferta = p.idOferta,
                        tituloOferta = "Oferta #${p.idOferta}",
                        area = "",
                        salario = 0.0,
                        modalidad = "",
                        nombreEmpresa = ""
                    )
                }
            } finally {
                cargandoOfertas = false
            }
        } else {
            postulacionesEnriquecidas = emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Postulaciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0056D2),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                loading || cargandoOfertas -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF0056D2))
                }
                error != null && error!!.contains("403") -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFFFF9800))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No se pueden cargar tus postulaciones en este momento.\nPor favor, contacta al administrador.",
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Volver")
                        }
                    }
                }
                error != null && !error!!.contains("403") -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Error: $error", color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
                postulacionesEnriquecidas.isEmpty() -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.BusinessCenter, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No te has postulado a ninguna oferta aún.", fontSize = 16.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { navController.navigate("estudiante_home") }) {
                            Text("Explorar ofertas")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(postulacionesEnriquecidas) { postulacion ->
                            PostulacionCardEnriquecida(postulacion, navController)
                        }
                    }
                }
            }
        }
    }
}

data class PostulacionEnriquecida(
    val idPostulacion: Long,
    val fechaPostulacion: String,
    val estado: String,
    val idOferta: Long,
    val tituloOferta: String,
    val area: String,
    val salario: Double,
    val modalidad: String,
    val nombreEmpresa: String
)

@Composable
fun PostulacionCardEnriquecida(postulacion: PostulacionEnriquecida, navController: NavController) {
    val estadoColor = when (postulacion.estado) {
        "PENDIENTE" -> Color(0xFFFF9800)
        "EN_REVISION" -> Color(0xFF2196F3)
        "ACEPTADA" -> Color(0xFF4CAF50)
        "RECHAZADA" -> Color(0xFFF44336)
        else -> Color.Gray
    }
    val estadoIcon = when (postulacion.estado) {
        "PENDIENTE" -> Icons.Default.HourglassEmpty
        "EN_REVISION" -> Icons.Default.Refresh
        "ACEPTADA" -> Icons.Default.CheckCircle
        "RECHAZADA" -> Icons.Default.Cancel
        else -> Icons.Default.Info
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("detalle_oferta_estudiante/${postulacion.idOferta}")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = estadoIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = estadoColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = postulacion.estado.replace("_", " ").lowercase()
                        .replaceFirstChar { it.uppercase() },
                    fontSize = 12.sp,
                    color = estadoColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "📅 ${postulacion.fechaPostulacion.take(10)}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = postulacion.tituloOferta,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
            Text(
                text = postulacion.area.ifBlank { "Área no especificada" },
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (postulacion.salario > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF2E7D32)
                        )
                        Text(" ${postulacion.salario.toInt()}/mes", fontSize = 12.sp, color = Color(0xFF2E7D32))
                    }
                }
                if (postulacion.modalidad.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Work,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF0056D2)
                        )
                        Text(" ${postulacion.modalidad}", fontSize = 12.sp, color = Color(0xFF0056D2))
                    }
                }
            }
        }
    }
}