package com.example.frontend_bolsa_empleo_universitaria.screens.Administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoColors
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.AdminViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val AdminIndigo = UniEmpleoColors.Navy
private val AdminIndigoLight = UniEmpleoColors.Blue
private val BackgroundGray = UniEmpleoColors.Background
private val AccentIndigo = UniEmpleoColors.Teal
private val TextSecondary = UniEmpleoColors.Muted
private val StatusGold = Color(0xFFB45309)
private val StatusGoldBg = Color(0xFFFEF3C7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdministradorHomeScreen(navController: NavController, adminViewModel: AdminViewModel) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val empresasAceptadas by adminViewModel.empresasAceptadas.collectAsState()
    val empresasPendientes by adminViewModel.empresasPendientes.collectAsState()
    val pendingCount = empresasPendientes.count { it.estado == "PENDIENTE" }

    var busqueda by remember { mutableStateOf("") }
    val empresasFiltradas = empresasAceptadas.filter {
        it.nombre?.contains(busqueda, ignoreCase = true) == true || 
        it.sector?.contains(busqueda, ignoreCase = true) == true
    }

    LaunchedEffect(Unit) {
        while(true) {
            adminViewModel.listarEmpresasPendientes()
            adminViewModel.listarEmpresasAceptadas()
            delay(30000) 
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = Color.White,
                drawerShape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp)
            ) {
                // Header Perfil Premium (Exacto al diseño solicitado)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = Brush.verticalGradient(listOf(AdminIndigo, AdminIndigoLight)))
                        .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(0.2f)), 
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Shield, null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Administrador", 
                            color = Color.White, 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = token.getUserEmail() ?: "admin@sistema.com", 
                            color = Color.White.copy(0.7f), 
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = Color(0xFF00C853).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Activo",
                                color = Color(0xFF00E676),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    // Sección: PRINCIPAL
                    Text(
                        "PRINCIPAL", 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = TextSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 8.dp),
                        letterSpacing = 1.sp
                    )
                    
                    AdminDrawerItem(
                        icon = Icons.Outlined.GridView, 
                        text = "Panel principal", 
                        selected = true,
                        onClick = { scope.launch { drawerState.close() } }
                    )
                    
                    AdminDrawerItem(
                        icon = Icons.Outlined.Business, 
                        text = "Empresas", 
                        badge = empresasAceptadas.size.toString(),
                        badgeColor = Color(0xFFE0E7FF),
                        badgeTextColor = Color(0xFF4338CA),
                        onClick = { 
                            scope.launch { 
                                drawerState.close()
                                navController.navigate("admin_empresas")
                            }
                        }
                    )

                    AdminDrawerItem(
                        icon = Icons.Outlined.Description, 
                        text = "Solicitudes", 
                        badge = if(pendingCount > 0) "$pendingCount nuevas" else null,
                        badgeColor = StatusGoldBg,
                        badgeTextColor = StatusGold,
                        onClick = { 
                            scope.launch { 
                                drawerState.close()
                                navController.navigate("notificaciones")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Pie del Drawer: Cerrar sesión (Exacto al diseño solicitado)
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                scope.launch {
                                    drawerState.close()
                                    token.clearSession()
                                    navController.navigate("login") { 
                                        popUpTo(0) { inclusive = true } 
                                    }
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout, 
                            null, 
                            tint = Color(0xFFEF4444), 
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Cerrar sesión", 
                            color = Color(0xFFEF4444), 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = BackgroundGray,
            topBar = {
                TopAppBar(
                    title = { Text("UNIEMPLEO Admin", color = Color.White, fontWeight = FontWeight.ExtraBold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null, tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("notificaciones") }) {
                            BadgedBox(badge = { if (pendingCount > 0) Badge { Text(pendingCount.toString()) } }) {
                                Icon(Icons.Default.Notifications, null, tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminIndigo)
                )
            }
        ) { padding ->
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                        Column {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(140.dp).background(
                                    brush = Brush.verticalGradient(listOf(AdminIndigo, AdminIndigoLight)),
                                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp)) {
                                        Icon(Icons.Default.CorporateFare, null, tint = Color.White, modifier = Modifier.padding(12.dp).size(36.dp))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("UNIEMPLEO", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).align(Alignment.BottomCenter),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            TextField(
                                value = busqueda, onValueChange = { busqueda = it }, modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Buscar empresas o sectores...") },
                                leadingIcon = { Icon(Icons.Default.Search, null, tint = AdminIndigo) },
                                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                                singleLine = true
                            )
                        }
                    }
                }

                item {
                    Column(Modifier.padding(16.dp)) {
                        DashboardStats(adminViewModel, navController)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Directorio Corporativo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AdminIndigo)
                    }
                }

                if (empresasFiltradas.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No se encontraron empresas", color = Color.Gray)
                        }
                    }
                } else {
                    items(empresasFiltradas) { empresa ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            ModernAdminCard(
                                nombre = empresa.nombre ?: "Empresa",
                                sector = empresa.sector ?: "General",
                                onClick = { navController.navigate("perfil_empresa_admin/${empresa.idEmpresa}") }
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun ModernAdminCard(nombre: String, sector: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(AdminIndigo.copy(0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(sector.lowercase()) {
                        "tecnología", "ti", "sistemas" -> Icons.Default.Code
                        "ventas", "comercial" -> Icons.AutoMirrored.Filled.TrendingUp
                        "salud", "medicina" -> Icons.Default.MedicalServices
                        "educación" -> Icons.Default.School
                        else -> Icons.Default.Business
                    },
                    contentDescription = null,
                    tint = AdminIndigo,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(nombre, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(sector, color = AdminIndigoLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
        }
    }
}

@Composable
fun DashboardStats(viewModel: AdminViewModel, navController: NavController) {
    val aceptadas by viewModel.empresasAceptadas.collectAsState()
    val pendientes by viewModel.empresasPendientes.collectAsState()
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("EMPRESAS", aceptadas.size.toString(), Icons.Default.Verified, Color(0xFF2E7D32), Modifier.weight(1f).clickable { navController.navigate("admin_empresas") })
        StatCard("PENDIENTES", pendientes.count { it.estado == "PENDIENTE" }.toString(), Icons.Default.Pending, Color(0xFFFFA000), Modifier.weight(1f).clickable { navController.navigate("notificaciones") })
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }
    }
}

@Composable
fun AdminDrawerItem(
    icon: ImageVector, 
    text: String, 
    badge: String? = null, 
    badgeColor: Color = Color.Transparent,
    badgeTextColor: Color = Color.White,
    selected: Boolean = false, 
    iconTint: Color? = null, 
    textColor: Color? = null, 
    onClick: () -> Unit
) {
    val bgColor = if (selected) Color(0xFFF0F4FF) else Color.Transparent
    val contentColor = if (selected) Color(0xFF1E3A8A) else (textColor ?: Color(0xFF4B5563))
    val tint = if (selected) Color(0xFF1E3A8A) else (iconTint ?: Color(0xFF9CA3AF))
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text, 
            color = contentColor, 
            fontSize = 14.sp, 
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, 
            modifier = Modifier.weight(1f)
        )
        if (badge != null) {
            Surface(
                color = badgeColor, 
                shape = RoundedCornerShape(100.dp)
            ) {
                Text(
                    text = badge, 
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), 
                    fontSize = 10.sp, 
                    color = badgeTextColor, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E3A8A))
            )
        }
    }
}
