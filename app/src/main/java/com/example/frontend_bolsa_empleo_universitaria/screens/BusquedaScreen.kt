package com.example.frontend_bolsa_empleo_universitaria.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel
import kotlinx.coroutines.delay

// ─── Colores mockup ──────────────────
private val BlueGradientStart = Color(0xFF0056D2)
private val BlueGradientEnd = Color(0xFF007BFF)
private val BackgroundGray = Color(0xFFF8FAFF)
private val ChipHybridColor = Color(0xFFE3F2FD)
private val ChipHybridText = Color(0xFF1976D2)
private val PriceColor = Color(0xFF2E7D32)

// ─── NAV ITEMS ───────────────────────
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

    // Debounce para la búsqueda
    LaunchedEffect(busqueda) {
        if (busqueda.isNotBlank()) {
            delay(500) // Esperar 500ms después de que el usuario deje de escribir
            viewModel.buscarPorCargo(busqueda)
        } else if (filtroSeleccionado == "Todas") {
            viewModel.cargarActivas()
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
                            when(item.route) {
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
            // Header con Gradiente y Buscador flotante
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
                // Filtros Rápidos
                item {
                    FiltrosSection(
                        seleccionado = filtroSeleccionado,
                        onFiltroClick = { filtro ->
                            filtroSeleccionado = filtro
                            busqueda = "" // Limpiar búsqueda al cambiar filtro
                            when (filtro) {
                                "Todas" -> viewModel.cargarActivas()
                                else -> viewModel.buscarPorArea(filtro)
                            }
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
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
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
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
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
            // Fondo Gradiente
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
            // Espacio para que el buscador flote
            Spacer(modifier = Modifier.height(30.dp))
        }

        // Buscador Flotante
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
                placeholder = { Text("Buscar cargo, empresa o área...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BlueGradientStart) },
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
            // Icono de Empresa (Placeholder)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(oferta.area.lowercase()) {
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
                    // Chip Modalidad
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

