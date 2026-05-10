package com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante

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
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
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

// Nav Items
data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItems = listOf(
    NavItem("Buscar", Icons.Default.Search, "busqueda"),
    NavItem("Postulaciones", Icons.Outlined.AssignmentTurnedIn, "postulaciones"),
    NavItem("Perfil", Icons.Default.Person, "perfil")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudianteHomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    var selectedTab by remember { mutableStateOf(0) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDrawerItem by remember { mutableStateOf("inicio") }

    // ✅ CORREGIDO: Obtener el nombre real del token (se actualiza al volver de configuración)
    val nombreUsuario = token.getUserNombre() ?: "Estudiante"

    val repository = remember { OfertasRepository(RetrofitClient.ofertaLaboralApi) }
    val viewModel: OfertasViewModel = viewModel(
        factory = OfertasViewModelFactory(repository)
    )

    var busqueda by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf("Todas") }

    // ✅ CORREGIDO: Usar OfertaLaboralResponse
    val ofertas = viewModel.ofertas.value
    val loading = viewModel.loading.value

    LaunchedEffect(Unit) {
        viewModel.cargarActivas()
    }

    if (viewModel.error.value != null) {
        Text(
            text = "Error: ${viewModel.error.value}",
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
    }

    LaunchedEffect(busqueda, filtroSeleccionado) {
        delay(300)
        if (busqueda.isNotBlank()) {
            viewModel.buscarGeneral(busqueda)
        } else {
            viewModel.filtrarPorCategoria(filtroSeleccionado)
        }
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
                                Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = nombreUsuario,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = token.getUserEmail() ?: "usuario@email.com",
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

                DrawerMenuItemGmail(
                    icon = Icons.Default.Home,
                    text = "Inicio",
                    badge = null,
                    selected = selectedDrawerItem == "inicio"
                ) {
                    selectedDrawerItem = "inicio"
                    scope.launch { drawerState.close() }
                }

                DrawerMenuItemGmail(
                    icon = Icons.Default.Notifications,
                    text = "Notificaciones",
                    badge = "3",
                    badgeColor = Color(0xFF1976D2),
                    selected = selectedDrawerItem == "notificaciones"
                ) {
                    selectedDrawerItem = "notificaciones"
                    scope.launch { drawerState.close() }
                    navController.navigate("notificaciones")
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                Text(
                    text = "CUENTA",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                DrawerMenuItemGmail(
                    icon = Icons.Default.Settings,
                    text = "Configuración de Cuenta",
                    badge = null,
                    selected = selectedDrawerItem == "configuracion"
                ) {
                    selectedDrawerItem = "configuracion"
                    scope.launch { drawerState.close() }
                    navController.navigate("configuracion_cuenta")
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                Text(
                    text = "SOPORTE",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                DrawerMenuItemGmail(
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
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                DrawerMenuItemGmail(
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
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = index == selectedTab,
                            onClick = {
                                selectedTab = index
                                when (item.route) {
                                    "postulaciones" -> navController.navigate("mis_postulaciones")
                                    "perfil" -> navController.navigate("mi_perfil")
                                }
                            },
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
                                        imageVector = Icons.Default.School,
                                        contentDescription = "Graduado",
                                        tint = Color.White,
                                        modifier = Modifier.size(50.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Bolsa de Empleo",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 24.sp
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "Encuentra tu próximo empleo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

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
                            placeholder = { Text("Buscar cargo, modalidad, área...") },
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
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        FiltrosSection(
                            seleccionado = filtroSeleccionado,
                            onFiltroClick = { filtro ->
                                filtroSeleccionado = filtro
                                busqueda = ""
                            }
                        )
                    }

                    item {
                        Text(
                            text = if (busqueda.isBlank()) "Ofertas Recientes" else "Resultados para '$busqueda'",
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

                    // ✅ Ahora oferta es OfertaLaboralResponse y tiene idOferta
                    items(ofertas) { oferta ->
                        JobCard(
                            oferta = oferta,
                            onClick = {
                                navController.navigate("detalle_oferta_estudiante/${oferta.idOferta}")
                            }
                        )
                    }

                    if (!loading && ofertas.isEmpty()) {
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
                                    Text("No se encontraron ofertas", color = Color.Gray)
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
fun FiltrosSection(
    seleccionado: String,
    onFiltroClick: (String) -> Unit
) {
    val filtros = listOf("Todas", "Diseño", "Desarrollo", "Marketing", "Ventas", "TI")
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        items(filtros) { filtro ->
            FilterChip(
                selected = filtro == seleccionado,
                onClick = { onFiltroClick(filtro) },
                label = { Text(filtro) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BlueGradientStart,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = Color.Gray
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = filtro == seleccionado,
                    borderColor = Color.LightGray,
                    selectedBorderColor = BlueGradientStart,
                    borderWidth = 1.dp
                )
            )
        }
    }
}

// ✅ CORREGIDO: Usar OfertaLaboralResponse
@Composable
fun JobCard(
    oferta: OfertaLaboralResponse,
    onClick: () -> Unit
) {
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
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

            Column(modifier = Modifier.weight(1f)) {
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

                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• $fechaPublicacionStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${oferta.salario.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PriceColor
                )
                Text(
                    text = "/mes",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun DrawerMenuItemGmail(
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