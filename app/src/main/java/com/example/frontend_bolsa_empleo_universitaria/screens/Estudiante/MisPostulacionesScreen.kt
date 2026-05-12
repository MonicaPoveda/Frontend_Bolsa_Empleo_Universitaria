package com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionEnriquecida
import com.example.frontend_bolsa_empleo_universitaria.repository.PostulacionRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.SeguimientoPostulacionRepository
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PostulacionViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PostulacionViewModelFactory
import kotlinx.coroutines.delay
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPostulacionesScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = remember { Token(context) }

    val postulacionRepository = remember {
        PostulacionRepository(
            RetrofitClient.postulacionApi,
            RetrofitClient.usuarioApi,
            RetrofitClient.ofertaLaboralApi
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

    val postulaciones      by viewModel.postulacionesEstudiante.collectAsState()
    val loading            by viewModel.loadingEstudiante.collectAsState()
    val error              by viewModel.errorEstudiante.collectAsState()

    var postulacionesEnriquecidas by remember { mutableStateOf<List<PostulacionEnriquecida>>(emptyList()) }
    var cargandoOfertas           by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // ── Carga periódica ───────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        val userId = tokenManager.getUserId() ?: return@LaunchedEffect
        viewModel.cargarPostulacionesEstudiante(userId)
        while (true) {
            delay(25_000)
            viewModel.cargarPostulacionesEstudiante(userId)
        }
    }

    // ── Enriquecimiento ───────────────────────────────────────────────────────
    LaunchedEffect(postulaciones) {
        if (postulaciones.isNotEmpty()) {
            cargandoOfertas = true
            try {
                val responseOfertas = RetrofitClient.ofertaLaboralApi.listar()
                val responseEmpresas = RetrofitClient.empresaApi.listar()
                
                if (responseOfertas.isSuccessful) {
                    val ofertasMap = responseOfertas.body()?.associateBy { it.idOferta } ?: emptyMap()
                    val empresasMap = if (responseEmpresas.isSuccessful) {
                        responseEmpresas.body()?.associateBy { it.idEmpresa } ?: emptyMap()
                    } else emptyMap()

                    postulacionesEnriquecidas = postulaciones.map { p ->
                        val oferta = ofertasMap[p.idOferta]
                        val empresa = oferta?.let { empresasMap[it.idEmpresa] }
                        PostulacionEnriquecida(
                            idPostulacion    = p.idPostulacion,
                            fechaPostulacion = p.fechaPostulacion,
                            estado           = p.estado,
                            idOferta         = p.idOferta,
                            tituloOferta     = oferta?.titulo ?: "Oferta #${p.idOferta}",
                            area             = oferta?.area ?: "",
                            salario          = oferta?.salario ?: 0.0,
                            modalidad        = oferta?.modalidad ?: "",
                            nombreEmpresa    = empresa?.nombre ?: "Empresa"
                        )
                    }
                } else {
                    postulacionesEnriquecidas = postulaciones.map { p ->
                        PostulacionEnriquecida(
                            idPostulacion    = p.idPostulacion,
                            fechaPostulacion = p.fechaPostulacion,
                            estado           = p.estado,
                            idOferta         = p.idOferta,
                            tituloOferta     = "Oferta #${p.idOferta}",
                            area = "", salario = 0.0, modalidad = "", nombreEmpresa = ""
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                postulacionesEnriquecidas = postulaciones.map { p ->
                    PostulacionEnriquecida(
                        idPostulacion    = p.idPostulacion,
                        fechaPostulacion = p.fechaPostulacion,
                        estado           = p.estado,
                        idOferta         = p.idOferta,
                        tituloOferta     = "Oferta #${p.idOferta}",
                        area = "", salario = 0.0, modalidad = "", nombreEmpresa = ""
                    )
                }
            } finally {
                cargandoOfertas = false
            }
        } else {
            postulacionesEnriquecidas = emptyList()
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Scaffold(
        containerColor = BolsaTokens.Palette.Background,
        topBar = { PostulacionesTopBar(navController, postulacionesEnriquecidas.size) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                (loading || cargandoOfertas) && postulacionesEnriquecidas.isEmpty() -> {
                    LoadingState()
                }
                error != null && error!!.contains("403") -> {
                    ErrorState403(navController)
                }
                error != null && !error!!.contains("403") -> {
                    ErrorStateGeneral(error!!)
                }
                postulacionesEnriquecidas.isEmpty() -> {
                    EmptyState(navController)
                }
                else -> {
                    LazyColumn(
                        state        = listState,
                        contentPadding = PaddingValues(
                            start  = 20.dp, end = 20.dp,
                            top    = 20.dp, bottom = 32.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            postulacionesEnriquecidas,
                            key = { it.idPostulacion }
                        ) { postulacion ->
                            AnimatedVisibility(
                                visible = true,
                                enter   = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                            ) {
                                PostulacionCardModerna(
                                    postulacion = postulacion,
                                    navController = navController,
                                    onEliminar = { p ->
                                        viewModel.eliminarPostulacion(
                                            idPostulacion = p.idPostulacion,
                                            onSuccess = {
                                                val userId = tokenManager.getUserId()
                                                if (userId != null) {
                                                    viewModel.cargarPostulacionesEstudiante(userId)
                                                }
                                            },
                                            onError = { msg ->
                                                println("Error al eliminar: $msg")
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostulacionesTopBar(navController: NavController, count: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BolsaTokens.headerGradientLinear)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 16.dp, bottom = 28.dp)
        ) {
            // Fila: botón atrás + contador
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                if (count > 0) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (count == 1) "1 postulación" else "$count postulaciones",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Título centrado
            Text(
                text       = "Mis Postulaciones",
                fontSize   = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White,
                letterSpacing = (-0.5).sp,
                textAlign  = androidx.compose.ui.text.style.TextAlign.Center,
                modifier   = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text      = "Seguimiento de tus postulaciones laborales",
                fontSize  = 14.sp,
                color     = Color.White.copy(alpha = 0.75f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }

        // Onda decorativa inferior
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .align(Alignment.BottomCenter)
        ) {
            val path = Path().apply {
                moveTo(0f, size.height)
                cubicTo(
                    size.width * 0.25f, 0f,
                    size.width * 0.75f, size.height,
                    size.width, 0f
                )
                lineTo(size.width, size.height)
                close()
            }
            drawPath(path, BolsaTokens.Palette.Background)
        }
    }
}

// ── Card de postulación ────────────────────────────────────────────────────────
@Composable
fun PostulacionCardModerna(
    postulacion: PostulacionEnriquecida,
    navController: NavController,
    onEliminar: ((PostulacionEnriquecida) -> Unit)? = null
) {
    data class EstadoStyle(
        val color: Color,
        val bgColor: Color,
        val label: String,
        val icon: ImageVector
    )

    val estilo = when (postulacion.estado) {
        "PENDIENTE"   -> EstadoStyle(BolsaTokens.Palette.Warning,  Color(0xFFFEF3C7), "Pendiente",   Icons.Default.HourglassEmpty)
        "EN_REVISION" -> EstadoStyle(BolsaTokens.Palette.Info,   Color(0xFFEFF6FF), "En revisión", Icons.Default.Refresh)
        "ACEPTADA"    -> EstadoStyle(BolsaTokens.Palette.Success, Color(0xFFECFDF5), "Aceptada",    Icons.Default.CheckCircle)
        "RECHAZADA"   -> EstadoStyle(BolsaTokens.Palette.Error, Color(0xFFFEF2F2), "Rechazada",   Icons.Default.Cancel)
        else          -> EstadoStyle(BolsaTokens.Palette.TextSecondary, BolsaTokens.Palette.Divider, postulacion.estado, Icons.Default.Info)
    }

    val accentBrush = Brush.verticalGradient(
        listOf(estilo.color, estilo.color.copy(alpha = 0.4f))
    )

    // Estado del diálogo de confirmación
    var mostrarDialogo by remember { mutableStateOf(false) }

    // ── Diálogo de confirmación de eliminación ────────────────────────────
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = BolsaTokens.Palette.Surface,
            icon = {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = BolsaTokens.Palette.Error,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            },
            title = {
                Text(
                    "¿Eliminar postulación?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BolsaTokens.Palette.TextPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Se eliminará tu postulación a \"${postulacion.tituloOferta}\". Esta acción no se puede deshacer.",
                    fontSize = 14.sp,
                    color = BolsaTokens.Palette.TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogo = false
                        onEliminar?.invoke(postulacion)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BolsaTokens.Palette.Error),
                    shape  = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Sí, eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarDialogo = false },
                    shape   = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    border  = BorderStroke(1.dp, BolsaTokens.Palette.Divider)
                ) {
                    Text("Cancelar", color = BolsaTokens.Palette.TextSecondary, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("detalle_oferta_estudiante/${postulacion.idOferta}") },
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // Barra lateral de color de estado
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(
                        accentBrush,
                        RoundedCornerShape(
                            topStart = 20.dp,
                            bottomStart = 20.dp
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 18.dp, end = 14.dp, top = 18.dp, bottom = 16.dp)
            ) {

                // ── Fila superior: chip estado + fecha + caneca ───────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chip de estado
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = estilo.bgColor
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = estilo.icon,
                                contentDescription = null,
                                tint    = estilo.color,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text       = estilo.label,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color      = estilo.color
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // Botón caneca roja eliminar
                    IconButton(
                        onClick  = { mostrarDialogo = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFFEE2E2), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar postulación",
                            tint     = BolsaTokens.Palette.Error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Título de la oferta ───────────────────────────────────
                Text(
                    text       = postulacion.tituloOferta,
                    fontSize   = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = BolsaTokens.Palette.TextPrimary,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                    lineHeight = 25.sp
                )

                // ── Nombre de la Empresa ──────────────────────────────────
                if (postulacion.nombreEmpresa.isNotBlank()) {
                    Text(
                        text     = postulacion.nombreEmpresa,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color    = BolsaTokens.Palette.Primary,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                // ── Área ──────────────────────────────────────────────────
                if (postulacion.area.isNotBlank()) {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text     = postulacion.area,
                        fontSize = 15.sp,
                        color    = BolsaTokens.Palette.TextSecondary
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Fecha debajo del título
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = BolsaTokens.Palette.TextSecondary.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text     = postulacion.fechaPostulacion.take(10),
                        fontSize = 13.sp,
                        color    = BolsaTokens.Palette.TextSecondary.copy(alpha = 0.7f)
                    )
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = BolsaTokens.Palette.Divider, thickness = 1.dp)
                Spacer(Modifier.height(12.dp))

                // ── Fila inferior: chips salario + modalidad + "Ver detalle" ─
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (postulacion.salario > 0) {
                            InfoChip(
                                icon  = Icons.Default.AttachMoney,
                                text  = "$${postulacion.salario.toInt()}/mes",
                                color = BolsaTokens.Palette.Success
                            )
                        }
                        if (postulacion.modalidad.isNotBlank()) {
                            InfoChip(
                                icon  = Icons.Default.Work,
                                text  = postulacion.modalidad,
                                color = BolsaTokens.Palette.Primary
                            )
                        }
                    }

                    // Hint de navegación
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text       = "Ver detalle",
                            fontSize   = 13.sp,
                            color      = BolsaTokens.Palette.Primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = BolsaTokens.Palette.Primary
                        )
                    }
                }
            }
        }
    }
}

// ── Chip de información ────────────────────────────────────────────────────────
@Composable
private fun InfoChip(icon: ImageVector, text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(13.dp), tint = color)
            Spacer(Modifier.width(4.dp))
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}

// ── Estado: cargando ──────────────────────────────────────────────────────────
@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color       = BolsaTokens.Palette.Primary,
                strokeWidth = 3.dp,
                modifier    = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("Cargando postulaciones…", color = BolsaTokens.Palette.TextSecondary, fontSize = 14.sp)
        }
    }
}

// ── Estado: 403 ───────────────────────────────────────────────────────────────
@Composable
private fun ErrorState403(navController: NavController) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFFFF3E0),
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.padding(20.dp),
                    tint = Color(0xFFF59E0B)
                )
            }
            Spacer(Modifier.height(20.dp))
            Text("Acceso restringido", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BolsaTokens.Palette.TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(
                "No se pueden cargar tus postulaciones.\nContacta al administrador.",
                color = BolsaTokens.Palette.TextSecondary,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors  = ButtonDefaults.buttonColors(containerColor = BolsaTokens.Palette.Primary),
                shape   = RoundedCornerShape(12.dp)
            ) {
                Text("Volver", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ── Estado: error general ─────────────────────────────────────────────────────
@Composable
private fun ErrorStateGeneral(error: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFFEF2F2),
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.padding(20.dp),
                    tint = BolsaTokens.Palette.Error
                )
            }
            Spacer(Modifier.height(16.dp))
            Text("Ocurrió un error", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BolsaTokens.Palette.TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(error, color = BolsaTokens.Palette.TextSecondary, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

// ── Estado: lista vacía ───────────────────────────────────────────────────────
@Composable
private fun EmptyState(navController: NavController) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(40.dp)
        ) {
            // Ícono con fondo degradado simulado
            Surface(
                shape  = RoundedCornerShape(28.dp),
                color  = BolsaTokens.Palette.PrimaryLight,
                modifier = Modifier.size(96.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.BusinessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = BolsaTokens.Palette.Primary
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Aún no tienes postulaciones",
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp,
                color      = BolsaTokens.Palette.TextPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Explora ofertas disponibles y comienza tu búsqueda laboral.",
                color    = BolsaTokens.Palette.TextSecondary,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = { navController.navigate("estudiante_home") },
                colors  = ButtonDefaults.buttonColors(containerColor = BolsaTokens.Palette.Primary),
                shape   = RoundedCornerShape(14.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Explorar ofertas", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFFF4F7FF)
@Composable
fun MisPostulacionesScreenPreview() {
    MisPostulacionesScreen(navController = rememberNavController())
}