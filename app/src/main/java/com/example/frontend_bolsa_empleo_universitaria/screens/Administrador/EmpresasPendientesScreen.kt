package com.example.frontend_bolsa_empleo_universitaria.screens.Administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.ui.components.AdminMessageBanner
import com.example.frontend_bolsa_empleo_universitaria.ui.components.ProfilePhotoDisplay
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoColors
import com.example.frontend_bolsa_empleo_universitaria.viewModel.AdminViewModel

private val CleanWhite = UniEmpleoColors.Surface
private val CleanBackground = UniEmpleoColors.Background
private val AccentIndigo = UniEmpleoColors.Blue
private val TextMain = UniEmpleoColors.Text
private val TextSecondary = UniEmpleoColors.Muted
private val BorderLight = Color(0xFFE5E7EB)
private val StatusGold = Color(0xFFB45309)
private val StatusGoldBg = Color(0xFFFEF3C7)
private val StatusRed = Color(0xFFDC2626)
private val StatusRedBg = Color(0xFFFEF2F2)
private val SuccessGreen = Color(0xFF059669)
private val SuccessGreenBg = Color(0xFFECFDF5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpresasPendientesScreen(navController: NavController, viewModel: AdminViewModel) {
    val todasLasSolicitudes by viewModel.empresasPendientes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val adminMessage by viewModel.adminMessage.collectAsState()
    
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pendientes", "Rechazadas")

    val listaPendientes = todasLasSolicitudes.filter { it.estado.equals("PENDIENTE", ignoreCase = true) }
    val listaRechazadas = todasLasSolicitudes.filter { it.estado.equals("RECHAZADA", ignoreCase = true) }
    val listaAMostrar = if (selectedTabIndex == 0) listaPendientes else listaRechazadas

    LaunchedEffect(Unit) {
        viewModel.listarEmpresasPendientes()
    }

    Scaffold(
        containerColor = CleanBackground,
        topBar = {
            Column(modifier = Modifier.background(CleanWhite)) {
                CenterAlignedTopAppBar(
                    title = { Text("Solicitudes de Registro", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextMain) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextSecondary, modifier = Modifier.size(20.dp))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = CleanWhite)
                )
                
                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = CleanWhite,
                    contentColor = AccentIndigo,
                    divider = { HorizontalDivider(color = BorderLight) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        val count = if (index == 0) listaPendientes.size else listaRechazadas.size
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = title, fontSize = 13.sp, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium)
                                    if (count > 0) {
                                        Spacer(Modifier.width(6.dp))
                                        Surface(
                                            color = if (selectedTabIndex == index) AccentIndigo else Color.LightGray.copy(0.4f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = count.toString(),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontSize = 10.sp,
                                                color = if (selectedTabIndex == index) Color.White else TextSecondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (isLoading && todasLasSolicitudes.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentIndigo)
                    }
                } else if (listaAMostrar.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                            Surface(modifier = Modifier.size(72.dp), shape = CircleShape, color = Color(0xFFF3F4F6)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        if (selectedTabIndex == 0) Icons.Default.CheckCircleOutline else Icons.Default.Info, 
                                        null, 
                                        modifier = Modifier.size(32.dp), 
                                        tint = TextSecondary.copy(alpha = 0.4f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = if (selectedTabIndex == 0) "¡Bandeja limpia!" else "Sin rechazadas",
                                fontWeight = FontWeight.Bold, color = TextMain, fontSize = 16.sp
                            )
                            Text(
                                text = if (selectedTabIndex == 0) "No hay solicitudes esperando revisión." else "Las empresas rechazadas aparecerán aquí.",
                                fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(listaAMostrar) { empresa ->
                            val esRechazada = empresa.estado.equals("RECHAZADA", ignoreCase = true)
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { 
                                        navController.navigate("detalle_solicitud/${empresa.idEmpresaPendiente}")
                                    },
                                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().border(1.dp, BorderLight, RoundedCornerShape(12.dp))) {
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .height(48.dp)
                                            .align(Alignment.CenterStart)
                                            .background(if (esRechazada) StatusRed else if (empresa.actualizada) SuccessGreen else AccentIndigo)
                                    )

                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ProfilePhotoDisplay(
                                            photoUrl = "https://backend-sistema-empleo-universitario.onrender.com/api/archivos/foto/empresa/${empresa.idEmpresaPendiente}",
                                            size = 44,
                                            modifier = Modifier.clip(RoundedCornerShape(10.dp))
                                        )
                                        
                                        Spacer(modifier = Modifier.width(12.dp))
                                        
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = empresa.nombre, fontWeight = FontWeight.Bold, color = TextMain, fontSize = 14.sp)
                                                if (empresa.actualizada && !esRechazada) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Surface(color = SuccessGreenBg, shape = RoundedCornerShape(4.dp)) {
                                                        Text("ACTUALIZADA", color = SuccessGreen, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                    }
                                                }
                                            }
                                            Text(text = empresa.email, fontSize = 11.sp, color = TextSecondary)
                                        }

                                        Surface(
                                            color = if (esRechazada) StatusRedBg else StatusGoldBg,
                                            shape = RoundedCornerShape(100.dp)
                                        ) {
                                            Text(
                                                text = if (esRechazada) "${empresa.rechazos}/3" else "Pendiente",
                                                color = if (esRechazada) StatusRed else StatusGold,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            AdminMessageBanner(
                state = adminMessage,
                onDismiss = { viewModel.dismissAdminMessage() },
                modifier = Modifier.align(Alignment.TopCenter).padding(top = padding.calculateTopPadding() + 8.dp)
            )
        }
    }
}
