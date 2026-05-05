package com.example.frontend_bolsa_empleo_universitaria.screens

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel
import kotlinx.coroutines.delay

private val BlueGradientStart = Color(0xFF0056D2)
private val BlueGradientEnd = Color(0xFF007BFF)
private val BackgroundGray = Color(0xFFF8FAFF)
private val ChipHybridColor = Color(0xFFE3F2FD)
private val ChipHybridText = Color(0xFF1976D2)
private val PriceColor = Color(0xFF2E7D32)

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

@Composable
fun BusquedaScreen(
    viewModel: OfertasViewModel = viewModel(),
    nombreUsuario: String = "Usuario",
    onNotificationClick: () -> Unit = {},
    onVerDetalle: (Long) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPostulations: () -> Unit = {}
) {
    var busqueda by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var filtroSeleccionado by remember { mutableStateOf("Todas") }

    val ofertas = viewModel.ofertas.value
    val loading = viewModel.loading.value

    // Carga inicial
    LaunchedEffect(Unit) {
        viewModel.cargarActivas()
    }

    //  Escucha AMBOS cambios juntos para evitar que se pisen
    LaunchedEffect(busqueda, filtroSeleccionado) {
        delay(300)
        if (busqueda.isNotBlank()) {
            viewModel.buscarGeneral(busqueda)
        } else {
            viewModel.filtrarPorCategoria(filtroSeleccionado)
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            when (item.route) {
                                "perfil" -> onNavigateToProfile()
                                "postulaciones" -> onNavigateToPostulations()
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
            HeaderSection(
                nombreUsuario = nombreUsuario,
                busqueda = busqueda,
                onBusquedaChange = { busqueda = it },
                onNotificationClick = onNotificationClick
            )

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
                            busqueda = "" // ← limpia búsqueda, LaunchedEffect se encarga del resto
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

                items(ofertas) { oferta ->
                    JobCard(
                        oferta = oferta,
                        onClick = { onVerDetalle(oferta.idOferta) }
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

@Composable
fun HeaderSection(
    nombreUsuario: String,
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    onNotificationClick: () -> Unit
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
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Hola, $nombreUsuario 👋",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Encuentra tu próximo empleo",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
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
                onValueChange = onBusquedaChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar cargo, modalidad, área...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = BlueGradientStart)
                },
                trailingIcon = if (busqueda.isNotEmpty()) {
                    {
                        IconButton(onClick = { onBusquedaChange("") }) {
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

@Composable
fun JobCard(
    oferta: OfertaLaboral,
    onClick: () -> Unit
) {
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
                        text = "• ${oferta.fechaPublicacion}",
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

//package com.example.frontend_bolsa_empleo_universitaria.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ExitToApp
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.frontend_bolsa_empleo_universitaria.model.Usuario
//import com.example.frontend_bolsa_empleo_universitaria.viewModel.NotificacionViewModel
//import androidx.lifecycle.viewmodel.compose.viewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(
//    usuario: Usuario?,
//    onNavigateToPostulaciones: () -> Unit,
//    onNavigateToSearch: () -> Unit,
//    onNavigateToNotificaciones: () -> Unit,
//    onLogout: () -> Unit,
//    notifViewModel: NotificacionViewModel = viewModel()
//) {
//    val primaryBlue = Color(0xFF001F3F)
//    val notificaciones by notifViewModel.notificaciones
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Column {
//                        Text(
//                            "Hola, ${usuario?.nombre ?: "Usuario"}",
//                            style = MaterialTheme.typography.titleMedium,
//                            fontWeight = FontWeight.Bold
//                        )
//                        Text(
//                            "Explora nuevas oportunidades",
//                            style = MaterialTheme.typography.labelSmall,
//                            color = Color.White.copy(alpha = 0.7f)
//                        )
//                    }
//                },
//                actions = {
//                    BadgedBox(
//                        badge = {
//                            if (notificaciones.isNotEmpty()) {
//                                Badge(containerColor = Color.Red) {
//                                    Text(notificaciones.size.toString(), color = Color.White)
//                                }
//                            }
//                        },
//                        modifier = Modifier.padding(end = 8.dp)
//                    ) {
//                        IconButton(onClick = onNavigateToNotificaciones) {
//                            Icon(Icons.Default.Notifications, null)
//                        }
//                    }
//                    IconButton(onClick = onLogout) {
//                        Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = primaryBlue,
//                    titleContentColor = Color.White,
//                    actionIconContentColor = Color.White
//                )
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .background(Color(0xFFF8F9FA))
//                .padding(24.dp)
//        ) {
//            OutlinedTextField(
//                value = "",
//                onValueChange = {},
//                readOnly = true,
//                placeholder = { Text("Buscar vacantes...") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { onNavigateToSearch() },
//                enabled = false,
//                leadingIcon = { Icon(Icons.Default.Search, null) },
//                shape = RoundedCornerShape(12.dp),
//                colors = OutlinedTextFieldDefaults.colors(
//                    disabledContainerColor = Color.White,
//                    disabledBorderColor = Color.LightGray,
//                    disabledPlaceholderColor = Color.Gray,
//                    disabledLeadingIconColor = Color.Gray
//                )
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Text("Acciones Rápidas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//                HomeQuickActionCard(
//                    icon = Icons.Default.Business,
//                    label = "Empresas",
//                    color = Color(0xFFE3F2FD),
//                    iconColor = Color(0xFF1976D2),
//                    modifier = Modifier.weight(1f),
//                    onClick = { onNavigateToSearch() }
//                )
//                HomeQuickActionCard(
//                    icon = Icons.Default.Search,
//                    label = "Mis Postulaciones",
//                    color = Color(0xFFF3E5F5),
//                    iconColor = Color(0xFF7B1FA2),
//                    modifier = Modifier.weight(1f),
//                    onClick = onNavigateToPostulaciones
//                )
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Text("Tu Perfil Profesional", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
//
//            Card(
//                modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
//                shape = RoundedCornerShape(16.dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//            ) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Surface(shape = CircleShape, color = primaryBlue.copy(alpha = 0.1f), modifier = Modifier.size(48.dp)) {
//                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, tint = primaryBlue) }
//                        }
//                        Spacer(modifier = Modifier.width(16.dp))
//                        Column {
//                            Text("${usuario?.nombre ?: "Sin"} ${usuario?.apellido ?: "Nombre"}", fontWeight = FontWeight.Bold)
//                            Text(usuario?.email ?: "Sin Email", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
//                        }
//                    }
//
//                    Spacer(Modifier.height(16.dp))
//                    Text("Perfil al 80%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
//                    LinearProgressIndicator(
//                        progress = { 0.8f },
//                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
//                        color = primaryBlue,
//                        trackColor = Color(0xFFE0E0E0)
//                    )
//
//                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
//                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                        HomeInfoItem("Tipo", usuario?.tipoUsuario ?: "N/A")
//                        HomeInfoItem("Estado", if (usuario?.estado == true) "Activo" else "Inactivo")
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun HomeQuickActionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, iconColor: Color, modifier: Modifier, onClick: () -> Unit) {
//    Surface(modifier = modifier.clickable { onClick() }, color = color, shape = RoundedCornerShape(16.dp)) {
//        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
//            Icon(icon, null, tint = iconColor, modifier = Modifier.size(32.dp))
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.Black)
//        }
//    }
//}
//
//@Composable
//fun HomeInfoItem(label: String, value: String) {
//    Column {
//        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
//        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
//    }
//}