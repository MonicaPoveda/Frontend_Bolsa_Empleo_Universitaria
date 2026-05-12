package com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.PostulacionRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.SeguimientoPostulacionRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.components.BolsaModernDialog
import com.example.frontend_bolsa_empleo_universitaria.ui.components.BolsaSnackbarHost
import com.example.frontend_bolsa_empleo_universitaria.ui.components.showBolsaSuccess
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModelFactory
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PostulacionViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PostulacionViewModelFactory
import kotlinx.coroutines.launch

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

    val repository = remember { OfertasRepository(RetrofitClient.ofertaLaboralApi) }
    val empresaRepository = remember { EmpresaRepository(RetrofitClient.empresaApi) }
    val ofertasViewModel: OfertasViewModel = viewModel(
        factory = OfertasViewModelFactory(repository, empresaRepository)
    )

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

    val postulacionViewModel: PostulacionViewModel = viewModel(
        factory = PostulacionViewModelFactory(
            postulacionRepository,
            seguimientoRepository,
            RetrofitClient.postulacionApi
        )
    )

    var oferta by remember { mutableStateOf<OfertaLaboralResponse?>(null) }
    var nombreEmpresa by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showPostularDialog by remember { mutableStateOf(false) }
    var postulando by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessageText by remember { mutableStateOf("") }

    LaunchedEffect(ofertaId) {
        isLoading = true
        var encontrada = ofertasViewModel.ofertas.value.find { it.idOferta == ofertaId }
        if (encontrada == null) {
            try {
                val todas = OfertasRepository(RetrofitClient.ofertaLaboralApi).listarTodas()
                encontrada = todas.find { it.idOferta == ofertaId }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        oferta = encontrada
        isLoading = false
    }

    LaunchedEffect(oferta?.idEmpresa) {
        val id = oferta?.idEmpresa ?: return@LaunchedEffect
        nombreEmpresa = try {
            empresaRepository.listarEmpresas().find { it.idEmpresa == id }?.nombre?.takeIf { !it.isNullOrBlank() }
        } catch (_: Exception) {
            null
        }
    }

    if (showPostularDialog && oferta != null) {
        BolsaModernDialog(
            onDismissRequest = { showPostularDialog = false },
            title = "Confirmar postulación",
            text = "¿Deseas postularte a la oferta «${oferta!!.titulo}»?",
            icon = Icons.Default.Work,
            iconTint = BolsaTokens.Palette.Primary,
            confirmText = "Sí, postularme",
            onConfirm = {
                scope.launch {
                    showPostularDialog = false
                    postulando = true
                    val userId = tokenManager.getUserId()
                    if (userId != null) {
                        postulacionViewModel.postularse(
                            idUsuario = userId,
                            idOferta = ofertaId,
                            onSuccess = {
                                postulando = false
                                scope.launch {
                                    snackbarHostState.showBolsaSuccess("Postulación registrada correctamente")
                                }
                            },
                            onError = { error ->
                                postulando = false
                                errorMessageText = error
                                showErrorDialog = true
                            }
                        )
                    } else {
                        postulando = false
                        errorMessageText = "Sesión no válida. Inicia sesión nuevamente."
                        showErrorDialog = true
                    }
                }
            },
            dismissText = "Cancelar",
            onDismiss = { showPostularDialog = false },
            confirmColor = BolsaTokens.Palette.Primary
        )
    }

    if (showErrorDialog) {
        BolsaModernDialog(
            onDismissRequest = { showErrorDialog = false },
            title = "No se pudo completar",
            text = errorMessageText,
            icon = Icons.Default.ErrorOutline,
            iconTint = BolsaTokens.Palette.Error,
            iconBackground = BolsaTokens.Palette.Error.copy(alpha = 0.12f),
            confirmText = "Entendido",
            onConfirm = { showErrorDialog = false },
            confirmColor = BolsaTokens.Palette.Error
        )
    }

    Scaffold(
        snackbarHost = { BolsaSnackbarHost(snackbarHostState) },
        containerColor = BolsaTokens.Palette.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle de la oferta",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BolsaTokens.Palette.HeaderStart)
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BolsaTokens.Palette.Primary)
                }
            }
            oferta == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = BolsaTokens.Palette.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No se encontró la oferta", color = BolsaTokens.Palette.TextSecondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(containerColor = BolsaTokens.Palette.Primary)
                        ) {
                            Text("Volver", color = Color.White)
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
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(BolsaTokens.Palette.HeaderStart, BolsaTokens.Palette.HeaderEnd)
                                    ),
                                    shape = RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp)
                                )
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            Column {
                                Text(
                                    text = o.titulo.ifBlank { "Sin título" },
                                    color = Color.White,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Color.White.copy(alpha = 0.2f)
                                    ) {
                                        Row(
                                            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Business,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = nombreEmpresa?.ifBlank { "Empresa" } ?: "Empresa",
                                                color = Color.White,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = o.area.ifBlank { "Área no especificada" },
                                    color = Color.White.copy(alpha = 0.88f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DetalleInfoChipEstudiante(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.AttachMoney,
                                label = "Salario",
                                value = if (o.salario > 0) "$${o.salario.toInt()}/mes" else "No especificado",
                                color = BolsaTokens.Palette.Success
                            )
                            DetalleInfoChipEstudiante(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Work,
                                label = "Modalidad",
                                value = o.modalidad.ifBlank { "No especificada" },
                                color = BolsaTokens.Palette.Primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DetalleInfoChipEstudiante(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.CalendarToday,
                                label = "Publicada",
                                value = formatFecha(o.fechaPublicacion),
                                color = BolsaTokens.Palette.Secondary
                            )
                            DetalleInfoChipEstudiante(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.EventBusy,
                                label = "Cierre",
                                value = formatFecha(o.fechaCierre),
                                color = BolsaTokens.Palette.Warning
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(BolsaTokens.Dimens.cardRadius),
                            colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = null,
                                        tint = BolsaTokens.Palette.Primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Descripción del puesto",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = BolsaTokens.Palette.TextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = o.descripcion.ifBlank { "Sin descripción disponible." },
                                    color = BolsaTokens.Palette.TextSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Surface(
                        tonalElevation = 6.dp,
                        shadowElevation = 0.dp,
                        color = BolsaTokens.Palette.Surface
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Button(
                                onClick = { showPostularDialog = true },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                shape = RoundedCornerShape(BolsaTokens.Dimens.buttonRadius),
                                colors = ButtonDefaults.buttonColors(containerColor = BolsaTokens.Palette.Primary),
                                enabled = !postulando
                            ) {
                                if (postulando) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                                } else {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Send,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        "Postularme ahora",
                                        style = MaterialTheme.typography.labelLarge,
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
private fun DetalleInfoChipEstudiante(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(BolsaTokens.Dimens.fieldRadius),
        colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = BolsaTokens.Palette.TextSecondary)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = BolsaTokens.Palette.TextPrimary)
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
    } catch (e: Exception) {
        fecha
    }
}
