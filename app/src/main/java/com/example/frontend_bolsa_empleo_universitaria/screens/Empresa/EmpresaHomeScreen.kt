package com.example.frontend_bolsa_empleo_universitaria.screens.Empresa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Colores
private val BlueGradientStart = Color(0xFF0056D2)
private val BlueGradientEnd = Color(0xFF007BFF)
private val BackgroundGray = Color(0xFFF8FAFF)
private val ChipHybridColor = Color(0xFFE3F2FD)
private val ChipHybridText = Color(0xFF1976D2)
private val PriceColor = Color(0xFF2E7D32)

// Nav Items para Empresa
data class NavItemEmpresa(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItemsEmpresa = listOf(
    NavItemEmpresa("Inicio", Icons.Default.Home, "inicio"),
    NavItemEmpresa("Agregar", Icons.Default.Add, "agregar"),
    NavItemEmpresa("PerfilRequest", Icons.Default.Person, "perfil")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpresaHomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    var selectedTab by remember { mutableStateOf(0) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDrawerItem by remember { mutableStateOf("inicio") }

    val nombreEmpresa = token.getUserEmail()?.split("@")?.firstOrNull() ?: "Empresa"

    val repository = remember { OfertasRepository(RetrofitClient.ofertaLaboralApi) }
    val viewModel: OfertasViewModel = viewModel(
        factory = OfertasViewModelFactory(repository)
    )

    val idEmpresa = token.getEmpresaId()
    val ofertas = viewModel.ofertasEmpresa.value
    val loading = viewModel.loading.value

    LaunchedEffect(Unit) {
        if (idEmpresa > 0) {
            println("Cargando ofertas para empresa ID: $idEmpresa")
            viewModel.cargarOfertasPorEmpresa(idEmpresa)
        } else {
            println("⚠️ No hay ID de empresa guardado")
        }
    }

    if (viewModel.error.value != null) {
        Text(
            text = "Error: ${viewModel.error.value}",
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(BlueGradientStart, BlueGradientEnd)
                            )
                        )
                        .padding(vertical = 28.dp, horizontal = 20.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Business,
                                contentDescription = "Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = nombreEmpresa,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = token.getUserEmail() ?: "empresa@email.com",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "MENÚ",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                DrawerMenuItemEmpresa(
                    icon = Icons.Default.Home,
                    text = "Inicio",
                    badge = null,
                    selected = selectedDrawerItem == "inicio"
                ) {
                    selectedDrawerItem = "inicio"
                    scope.launch { drawerState.close() }
                    selectedTab = 0
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                Text(
                    text = "CUENTA",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                DrawerMenuItemEmpresa(
                    icon = Icons.Default.Settings,
                    text = "Configuración de Cuenta",
                    badge = null,
                    selected = selectedDrawerItem == "configuracion"
                ) {
                    selectedDrawerItem = "configuracion"
                    scope.launch { drawerState.close() }
                    navController.navigate("editar_perfil_empresa")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                Text(
                    text = "SOPORTE",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                DrawerMenuItemEmpresa(
                    icon = Icons.Default.Info,
                    text = "Acerca de",
                    badge = null,
                    selected = selectedDrawerItem == "acerca"
                ) {
                    selectedDrawerItem = "acerca"
                    scope.launch { drawerState.close() }
                    navController.navigate("acerca_de")
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                DrawerMenuItemEmpresa(
                    icon = Icons.Default.Logout,
                    text = "Cerrar Sesión",
                    badge = null,
                    iconTint = Color(0xFFE53935),
                    textColor = Color(0xFFE53935),
                    selected = false
                ) {
                    scope.launch { drawerState.close() }
                    token.clearSession()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = BackgroundGray,
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BlueGradientStart
                    ),
                    modifier = Modifier.height(56.dp)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    navItemsEmpresa.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = index == selectedTab,
                            onClick = { selectedTab = index },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BlueGradientStart,
                                selectedTextColor = BlueGradientStart,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = ChipHybridColor
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
                    loading = loading,
                    onVerDetalle = { ofertaId ->
                        navController.navigate("detalle_oferta/$ofertaId")
                    },
                    onEliminar = { ofertaId ->
                        viewModel.eliminarOferta(ofertaId, idEmpresa)
                    },
                    navController = navController
                )
                1 -> AgregarOfertaScreen(
                    padding = padding,
                    idEmpresa = idEmpresa,
                    onOfertaAgregada = {
                        viewModel.cargarOfertasPorEmpresa(idEmpresa)
                        selectedTab = 0
                    }
                )
                2 -> EmpresaPerfilScreen(padding)
            }
        }
    }
}

@Composable
fun EmpresaOfertasScreen(
    padding: PaddingValues,
    ofertas: List<OfertaLaboralResponse>,
    loading: Boolean,
    onVerDetalle: (Long) -> Unit,
    onEliminar: (Long) -> Unit,
    navController: NavController
) {
    // Estado para el filtro seleccionado
    var filtroSeleccionado by remember { mutableStateOf("Todas") }

    // Estado para la búsqueda
    var busqueda by remember { mutableStateOf("") }

    // Calcular estadísticas
    val totalOfertas = ofertas.size
    val activas = ofertas.count { it.estado }
    val inactivas = ofertas.count { !it.estado }

    // Filtrar por estado primero
    val ofertasPorEstado = when (filtroSeleccionado) {
        "Activas" -> ofertas.filter { it.estado }
        "Inactivas" -> ofertas.filter { !it.estado }
        else -> ofertas
    }

    // Filtrar por búsqueda (título o área)
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
                .wrapContentHeight()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(BlueGradientStart, BlueGradientEnd)
                            ),
                            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = "Empresa",
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Panel de Empresa",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 24.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Gestiona tus ofertas laborales",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Card de búsqueda flotante
            // Card de búsqueda flotante
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                TextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar por título, área...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = BlueGradientStart)
                    },
                    trailingIcon = if (busqueda.isNotEmpty()) {
                        {
                            IconButton(onClick = { busqueda = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = Color.Gray)
                            }
                        }
                    } else null,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,        // ← AGREGAR
                        unfocusedTextColor = Color.Black,      // ← AGREGAR
                        focusedPlaceholderColor = Color.Gray,  // ← OPCIONAL
                        unfocusedPlaceholderColor = Color.Gray // ← OPCIONAL
                    ),
                    singleLine = true
                )
            }
        }

        // Filtros en tarjetas
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
                        navController.navigate("postulantes_oferta/${oferta.idOferta}/${oferta.titulo}")
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
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color else Color.White,
            contentColor = if (isSelected) Color.White else color
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else Color.DarkGray
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
            if (partes.size == 3) {
                "${partes[2]}/${partes[1]}/${partes[0]}"
            } else {
                fecha
            }
        } catch (e: Exception) {
            fecha
        }
    } ?: "Fecha no disponible"

    LaunchedEffect(oferta.idOferta) {
        isLoadingCount = true
        try {
            val api = RetrofitClient.postulacionApi
            val response = api.listarPorOferta(oferta.idOferta)
            if (response.isSuccessful) {
                postulantesCount = response.body()?.size ?: 0
                println("Postulantes para oferta ${oferta.idOferta}: $postulantesCount")
            }
        } catch (e: Exception) {
            println("Error al contar postulantes: ${e.message}")
        } finally {
            isLoadingCount = false
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    "Eliminar oferta",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53935),
                    fontSize = 20.sp
                )
            },
            text = {
                Column {
                    Text(
                        "¿Estás seguro de que deseas eliminar esta oferta?",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Esta acción no se puede deshacer.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onEliminar()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", fontWeight = FontWeight.Medium, color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Fila con icono, contenido y botón eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Icono y contenido principal
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundGray),
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
                        Text(
                            text = oferta.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1
                        )
                        Text(
                            text = oferta.area,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = ChipHybridColor,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (oferta.modalidad.isNotBlank()) oferta.modalidad else "Presencial",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ChipHybridText,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "• $fechaPublicacionStr",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Salario debajo de modalidad y fecha
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = PriceColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$${oferta.salario.toInt()} / mes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PriceColor
                            )
                        }
                    }
                }

                // Botón de eliminar (solo ícono)
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón de postulantes (ocupa todo el ancho)
            Surface(
                color = if (postulantesCount > 0) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVerPostulantes() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isLoadingCount) {
                            Box(modifier = Modifier.size(16.dp)) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 1.5.dp,
                                    color = BlueGradientStart
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cargando postulantes...",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        } else {
                            Icon(
                                Icons.Default.People,
                                contentDescription = "Postulantes",
                                modifier = Modifier.size(20.dp),
                                tint = BlueGradientStart
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$postulantesCount postulante${if (postulantesCount != 1) "s" else ""}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = BlueGradientStart
                            )
                        }
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Ver postulantes",
                        modifier = Modifier.size(20.dp),
                        tint = BlueGradientStart
                    )
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
    badgeColor: Color = Color(0xFF1976D2),
    iconTint: Color = Color(0xFF444444),
    textColor: Color = Color(0xFF222222),
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = if (selected) Color(0xFFE3F2FD) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(50.dp))
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