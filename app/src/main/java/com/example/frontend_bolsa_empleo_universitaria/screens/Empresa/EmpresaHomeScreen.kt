package com.example.frontend_bolsa_empleo_universitaria.screens.Empresa

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralResponse
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.components.ProfilePhotoDisplay
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoLogo
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
import com.example.frontend_bolsa_empleo_universitaria.utils.ArchivoUrls
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private val BlueGradientStart = BolsaTokens.Palette.HeaderStart
private val BackgroundGray = BolsaTokens.Palette.Background
private val ChipHybridColor = BolsaTokens.Palette.PrimaryLight
private val ChipHybridText = BolsaTokens.Palette.Primary
private val PriceColor = BolsaTokens.Palette.Success

data class NavItemEmpresa(val label: String, val icon: ImageVector, val route: String)

val navItemsEmpresa = listOf(
    NavItemEmpresa("Inicio", Icons.Default.Home, "inicio"),
    NavItemEmpresa("Agregar", Icons.Default.Add, "agregar"),
    NavItemEmpresa("Perfil", Icons.Default.Person, "perfil")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpresaHomeScreen(navController: NavController) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    var selectedTab by remember { mutableStateOf(0) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDrawerItem by remember { mutableStateOf("inicio") }

    val nombreEmpresa = token.getUserNombre().ifEmpty { 
        token.getUserEmail()?.split("@")?.firstOrNull() ?: "Empresa" 
    }
    val cacheBuster = remember { System.currentTimeMillis() }

    val repository = remember { OfertasRepository(RetrofitClient.ofertaLaboralApi) }
    val empresaRepository = remember { EmpresaRepository(RetrofitClient.empresaApi) }
    val viewModel: OfertasViewModel = viewModel(
        factory = OfertasViewModelFactory(repository, empresaRepository)
    )

    val idEmpresa = token.getEmpresaId()
    val ofertas = viewModel.ofertasEmpresa.value
    val loading = viewModel.loading.value

    var backgroundRefreshTrigger by remember { mutableStateOf(0) }
    var isFirstLoad by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (idEmpresa > 0) {
            viewModel.cargarOfertasPorEmpresa(idEmpresa)
            isFirstLoad = false
        }
    }

    LaunchedEffect(idEmpresa, backgroundRefreshTrigger) {
        if (idEmpresa > 0 && !isFirstLoad) {
            viewModel.cargarOfertasPorEmpresaSilent(idEmpresa)
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(30000)
            if (!isFirstLoad && idEmpresa > 0) {
                backgroundRefreshTrigger++
            }
        }
    }

    val forceRefresh = {
        if (idEmpresa > 0) {
            backgroundRefreshTrigger++
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = BolsaTokens.Palette.Surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BolsaTokens.headerGradientVertical)
                        .padding(vertical = 28.dp, horizontal = 20.dp)
                ) {
                    Column {
                        ProfilePhotoDisplay(
                            photoUrl = ArchivoUrls.fotoEmpresa(idEmpresa),
                            cacheBuster = cacheBuster,
                            size = 64,
                            placeholderIcon = Icons.Default.Business,
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.White.copy(alpha = 0.22f))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = nombreEmpresa, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = token.getUserEmail() ?: "empresa@email.com", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Menú", modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BolsaTokens.Palette.TextSecondary)
                DrawerMenuItemEmpresa(icon = Icons.Default.Home, text = "Inicio", selected = selectedDrawerItem == "inicio") {
                    selectedDrawerItem = "inicio"
                    scope.launch { drawerState.close() }
                    selectedTab = 0
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BolsaTokens.Palette.Divider)
                Text(text = "Cuenta", modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BolsaTokens.Palette.TextSecondary)
                DrawerMenuItemEmpresa(icon = Icons.Default.Settings, text = "Configuración de cuenta", selected = selectedDrawerItem == "configuracion") {
                    selectedDrawerItem = "configuracion"
                    scope.launch { drawerState.close() }
                    navController.navigate("editar_perfil_empresa")
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BolsaTokens.Palette.Divider)
                DrawerMenuItemEmpresa(icon = Icons.Default.Info, text = "Sobre nosotros", selected = selectedDrawerItem == "sobre_nosotros") {
                    selectedDrawerItem = "sobre_nosotros"
                    scope.launch { drawerState.close() }
                    navController.navigate("sobre_nosotros")
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BolsaTokens.Palette.Divider)
                DrawerMenuItemEmpresa(
                    icon = Icons.Default.Logout,
                    text = "Cerrar sesión",
                    iconTint = BolsaTokens.Palette.Error,
                    textColor = BolsaTokens.Palette.Error,
                    selected = false
                ) {
                    scope.launch { drawerState.close() }
                    token.clearSession()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = BackgroundGray,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(
                            brush = BolsaTokens.headerGradientVertical,
                            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                        )
                ) {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = "UNIEMPLEO Empresas",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        letterSpacing = 1.sp
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = BolsaTokens.Palette.Surface,
                    tonalElevation = 8.dp
                ) {
                    navItemsEmpresa.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = index == selectedTab,
                            onClick = { selectedTab = index },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BolsaTokens.Palette.Primary,
                                selectedTextColor = BolsaTokens.Palette.Primary,
                                unselectedIconColor = BolsaTokens.Palette.TextSecondary,
                                unselectedTextColor = BolsaTokens.Palette.TextSecondary,
                                indicatorColor = BolsaTokens.Palette.PrimaryLight
                            )
                        )
                    }
                }
            }
        ) { padding ->
            when (selectedTab) {
                0 -> EmpresaOfertasScreen(
                    padding = padding,
                    ofertas = ofertas,
                    loading = isFirstLoad && loading,
                    onVerDetalle = { navController.navigate("detalle_oferta/$it") },
                    onEliminar = { viewModel.eliminarOferta(it, idEmpresa); forceRefresh() },
                    navController = navController
                )
                1 -> AgregarOfertaScreen(
                    padding = padding,
                    idEmpresa = idEmpresa,
                    onOfertaAgregada = {
                        forceRefresh()
                        scope.launch { delay(500); selectedTab = 0 }
                    }
                )
                2 -> EmpresaPerfilScreen(padding)
            }
        }
    }
}

// ======================== RESTO DE FUNCIONES ========================
// (Se mantienen igual que en tu código original: EmpresaOfertasScreen, FiltroCard, EmpresaJobCard, DrawerMenuItemEmpresa)
// Por brevedad no los repito aquí, pero asegúrate de incluirlos.
// Si necesitas que los incluya de nuevo, dímelo.

// ======================== COMPONENTES AUXILIARES ========================

@Composable
fun EmpresaOfertasScreen(
    padding: PaddingValues,
    ofertas: List<OfertaLaboralResponse>,
    loading: Boolean,
    onVerDetalle: (Long) -> Unit,
    onEliminar: (Long) -> Unit,
    navController: NavController
) {
    var filtroSeleccionado by remember { mutableStateOf("Todas") }
    var busqueda by remember { mutableStateOf("") }

    val totalOfertas = ofertas.size
    val activas = ofertas.count { it.estado }
    val inactivas = ofertas.count { !it.estado }

    val ofertasPorEstado = when (filtroSeleccionado) {
        "Activas" -> ofertas.filter { it.estado }
        "Inactivas" -> ofertas.filter { !it.estado }
        else -> ofertas
    }

    val ofertasFiltradas = if (busqueda.isNotBlank()) {
        ofertasPorEstado.filter { oferta ->
            oferta.titulo.contains(busqueda, ignoreCase = true) ||
                    oferta.area.contains(busqueda, ignoreCase = true)
        }
    } else {
        ofertasPorEstado
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            // Card de búsqueda sin offset negativo para evitar tapar el header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(BolsaTokens.Dimens.fieldRadius),
                elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
                colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface)
            ) {
                TextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar por título, área...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = BolsaTokens.Palette.Primary)
                    },
                    trailingIcon = if (busqueda.isNotEmpty()) {
                        {
                            IconButton(onClick = { busqueda = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = BolsaTokens.Palette.TextSecondary)
                            }
                        }
                    } else null,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = BolsaTokens.Palette.TextPrimary,
                        unfocusedTextColor = BolsaTokens.Palette.TextPrimary
                    ),
                    singleLine = true
                )
            }
        }

        // Filtros
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FiltroCard(
                modifier = Modifier.weight(1f),
                titulo = "Todas",
                numero = totalOfertas,
                color = BlueGradientStart,
                isSelected = filtroSeleccionado == "Todas",
                onClick = { filtroSeleccionado = "Todas" }
            )
            FiltroCard(
                modifier = Modifier.weight(1f),
                titulo = "Activas",
                numero = activas,
                color = Color(0xFF2E7D32),
                isSelected = filtroSeleccionado == "Activas",
                onClick = { filtroSeleccionado = "Activas" }
            )
            FiltroCard(
                modifier = Modifier.weight(1f),
                titulo = "Inactivas",
                numero = inactivas,
                color = Color(0xFFE53935),
                isSelected = filtroSeleccionado == "Inactivas",
                onClick = { filtroSeleccionado = "Inactivas" }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = when {
                        busqueda.isNotBlank() -> "Resultados para '$busqueda'"
                        filtroSeleccionado == "Activas" -> "Ofertas Activas"
                        filtroSeleccionado == "Inactivas" -> "Ofertas Inactivas"
                        else -> "Mis Ofertas Publicadas"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            if (loading) {
                item {
                    Box(
                        Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BlueGradientStart)
                    }
                }
            }

            items(ofertasFiltradas) { oferta ->
                EmpresaJobCard(
                    oferta = oferta,
                    onClick = { onVerDetalle(oferta.idOferta) },
                    onVerPostulantes = {
                        val encodedTitulo = Uri.encode(oferta.titulo)
                        navController.navigate("postulantes_oferta/${oferta.idOferta}/$encodedTitulo")
                    },
                    onEliminar = { onEliminar(oferta.idOferta) }
                )
            }

            if (!loading && ofertasFiltradas.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                when {
                                    busqueda.isNotBlank() -> "No se encontraron ofertas que coincidan con '$busqueda'"
                                    filtroSeleccionado == "Activas" -> "No tienes ofertas activas"
                                    filtroSeleccionado == "Inactivas" -> "No tienes ofertas inactivas"
                                    else -> "No tienes ofertas publicadas"
                                },
                                color = Color.Gray
                            )
                            if (filtroSeleccionado != "Inactivas" && busqueda.isBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Toca el botón + para crear una nueva oferta",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun FiltroCard(
    modifier: Modifier = Modifier,
    titulo: String,
    numero: Int,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color else BolsaTokens.Palette.Surface,
            contentColor = if (isSelected) Color.White else color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else BolsaTokens.Palette.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = numero.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else color
            )
        }
    }
}

@Composable
fun EmpresaJobCard(
    oferta: OfertaLaboralResponse,
    onClick: () -> Unit,
    onVerPostulantes: () -> Unit = {},
    onEliminar: () -> Unit = {}
) {
    var postulantesCount by remember { mutableStateOf(0) }
    var isLoadingCount by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val fechaPublicacionStr = oferta.fechaPublicacion?.let { fecha ->
        try {
            val partes = fecha.split("-")
            if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fecha
        } catch (e: Exception) { fecha }
    } ?: "Fecha no disponible"

    LaunchedEffect(oferta.idOferta) {
        isLoadingCount = true
        try {
            val api = RetrofitClient.postulacionApi
            val response = api.listarPorOferta(oferta.idOferta)
            if (response.isSuccessful) {
                postulantesCount = response.body()?.size ?: 0
            }
        } catch (e: Exception) {
            println("Error al contar postulantes: ${e.message}")
        } finally {
            isLoadingCount = false
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = BolsaTokens.Palette.Surface,
            title = {
                Text("Eliminar oferta", fontWeight = FontWeight.Bold, color = BolsaTokens.Palette.Error, fontSize = 20.sp)
            },
            text = {
                Column {
                    Text("¿Estás seguro de que deseas eliminar esta oferta?", color = BolsaTokens.Palette.TextPrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Esta acción no se puede deshacer.", color = BolsaTokens.Palette.TextSecondary, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false; onEliminar() },
                    colors = ButtonDefaults.buttonColors(containerColor = BolsaTokens.Palette.Error, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", fontWeight = FontWeight.Medium, color = BolsaTokens.Palette.TextSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(BolsaTokens.Dimens.cardRadius),
        colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(54.dp).clip(RoundedCornerShape(12.dp)).background(BackgroundGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (oferta.area.lowercase()) {
                                "diseño" -> Icons.Default.Palette
                                "desarrollo", "ti" -> Icons.Default.Code
                                "ventas" -> Icons.Default.TrendingUp
                                "marketing" -> Icons.Default.Campaign
                                else -> Icons.Default.Business
                            },
                            contentDescription = null,
                            tint = BlueGradientStart,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = oferta.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1)
                        Text(text = oferta.area, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(color = ChipHybridColor, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = if (oferta.modalidad.isNotBlank()) oferta.modalidad else "Presencial",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ChipHybridText,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(text = "• $fechaPublicacionStr", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(16.dp), tint = PriceColor)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "$${oferta.salario.toInt()} / mes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PriceColor)
                        }
                    }
                }
                IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = BolsaTokens.Palette.Error, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = if (postulantesCount > 0) BolsaTokens.Palette.PrimaryLight else BolsaTokens.Palette.Background,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().clickable { onVerPostulantes() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isLoadingCount) {
                            Box(modifier = Modifier.size(16.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp, color = BlueGradientStart)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Cargando postulantes...", fontSize = 13.sp, color = Color.Gray)
                        } else {
                            Icon(Icons.Default.People, contentDescription = "Postulantes", modifier = Modifier.size(20.dp), tint = BlueGradientStart)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "$postulantesCount postulante${if (postulantesCount != 1) "s" else ""}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BlueGradientStart)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Ver postulantes", modifier = Modifier.size(20.dp), tint = BlueGradientStart)
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItemEmpresa(
    icon: ImageVector,
    text: String,
    badge: String? = null,
    badgeColor: Color = BolsaTokens.Palette.Primary,
    iconTint: Color = BolsaTokens.Palette.TextPrimary,
    textColor: Color = BolsaTokens.Palette.TextPrimary,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = if (selected) BolsaTokens.Palette.PrimaryLight else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(BolsaTokens.Dimens.chipRadius))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = text,
            tint = if (selected) BlueGradientStart else iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = if (selected) BlueGradientStart else textColor,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        if (badge != null) {
            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = badge,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmpresaHomeScreen() {
    val navController = rememberNavController()
    EmpresaHomeScreen(navController = navController)
}