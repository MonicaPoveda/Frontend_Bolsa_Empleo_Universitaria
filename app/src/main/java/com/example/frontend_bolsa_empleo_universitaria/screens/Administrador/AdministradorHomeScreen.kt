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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.model.*
import com.example.frontend_bolsa_empleo_universitaria.ui.components.BolsaOutlinedFormField
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoColors
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.AdminViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val AdminIndigo = UniEmpleoColors.Navy
private val AdminIndigoLight = UniEmpleoColors.Blue
private val BackgroundGray = UniEmpleoColors.Background
private val StatusGold = Color(0xFFB45309)
private val StatusGoldBg = Color(0xFFFEF3C7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdministradorHomeScreen(navController: NavController, adminViewModel: AdminViewModel) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Recolectamos los estados del ViewModel con los delegados corregidos
    val empresasAceptadas by adminViewModel.empresasAceptadas.collectAsState()
    val empresasPendientes by adminViewModel.empresasPendientes.collectAsState()
    
    // Conteo basado en la lógica de solicitudes que necesitan atención
    val pendingCount = empresasPendientes.count { it.estado.equals("PENDIENTE", ignoreCase = true) }

    var busqueda by remember { mutableStateOf("") }
    val empresasFiltradas = empresasAceptadas.filter {
        it.nombre?.contains(busqueda, ignoreCase = true) == true || 
        it.sector?.contains(busqueda, ignoreCase = true) == true
    }

    LaunchedEffect(Unit) {
        adminViewModel.listarEmpresasPendientes()
        adminViewModel.listarEmpresasAceptadas()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = Brush.verticalGradient(listOf(AdminIndigo, AdminIndigoLight)))
                        .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
                ) {
                    Column {
                        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White.copy(0.2f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Shield, null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Administrador", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(token.getUserEmail() ?: "admin@sistema.com", color = Color.White.copy(0.7f), fontSize = 13.sp)
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    AdminDrawerItem(Icons.Outlined.GridView, "Panel principal", selected = true) { scope.launch { drawerState.close() } }
                    AdminDrawerItem(Icons.Outlined.Business, "Empresas", badge = empresasAceptadas.size.toString()) { 
                        scope.launch { drawerState.close(); navController.navigate("admin_empresas") }
                    }
                    AdminDrawerItem(Icons.Outlined.Description, "Solicitudes", badge = if(pendingCount > 0) "$pendingCount" else null) { 
                        scope.launch { drawerState.close(); navController.navigate("notificaciones") }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                
                AdminDrawerItem(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión", iconTint = Color.Red, textColor = Color.Red) {
                    scope.launch {
                        drawerState.close()
                        token.clearSession()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            containerColor = BackgroundGray,
            topBar = {
                TopAppBar(
                    title = { Text("UNIEMPLEO Admin", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, null, tint = Color.White) } },
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
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                        Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(brush = Brush.verticalGradient(listOf(AdminIndigo, AdminIndigoLight)), shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)))
                        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).align(Alignment.BottomCenter), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(6.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            BolsaOutlinedFormField(value = busqueda, onValueChange = { busqueda = it }, label = "Buscar empresa", placeholder = "Nombre o sector...", leadingIcon = { Icon(Icons.Default.Search, null) }, modifier = Modifier.padding(8.dp))
                        }
                    }
                }

                item {
                    Column(Modifier.padding(16.dp)) {
                        DashboardStats(adminViewModel, navController)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Directorio Corporativo", fontWeight = FontWeight.Bold, color = AdminIndigo, fontSize = 18.sp)
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
                            ModernAdminCard(empresa.nombre ?: "Empresa", empresa.sector ?: "General") {
                                navController.navigate("perfil_empresa_admin/${empresa.idEmpresa}")
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun DashboardStats(viewModel: AdminViewModel, navController: NavController) {
    val aceptadas by viewModel.empresasAceptadas.collectAsState()
    val pendientes by viewModel.empresasPendientes.collectAsState()
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("EMPRESAS", aceptadas.size.toString(), Icons.Default.Verified, Color(0xFF2E7D32), Modifier.weight(1f).clickable { navController.navigate("admin_empresas") })
        StatCard("SOLICITUDES", pendientes.size.toString(), Icons.Default.Pending, Color(0xFFFFA000), Modifier.weight(1f).clickable { navController.navigate("notificaciones") })
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ModernAdminCard(nombre: String, sector: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(AdminIndigo.copy(0.1f)), contentAlignment = Alignment.Center) {
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
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(nombre, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(sector, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
        }
    }
}

@Composable
fun AdminDrawerItem(icon: ImageVector, text: String, badge: String? = null, selected: Boolean = false, iconTint: Color? = null, textColor: Color? = null, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(8.dp)).background(if (selected) AdminIndigo.copy(0.1f) else Color.Transparent).clickable { onClick() }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = iconTint ?: if (selected) AdminIndigo else Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, color = textColor ?: if (selected) AdminIndigo else Color.DarkGray, modifier = Modifier.weight(1f))
        if (badge != null) {
            Surface(color = if (selected) AdminIndigo else StatusGoldBg, shape = RoundedCornerShape(100.dp)) {
                Text(badge, color = if (selected) Color.White else StatusGold, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
            }
        }
    }
}
