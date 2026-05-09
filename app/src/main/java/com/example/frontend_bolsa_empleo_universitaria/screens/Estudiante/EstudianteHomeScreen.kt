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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModelFactory
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import kotlinx.coroutines.delay

// ─── Colores (mismo estilo que BusquedaScreen) ──────────────────
private val BlueGradientStart = Color(0xFF0056D2)
private val BlueGradientEnd = Color(0xFF007BFF)
private val BackgroundGray = Color(0xFFF8FAFF)
private val ChipHybridColor = Color(0xFFE3F2FD)
private val ChipHybridText = Color(0xFF1976D2)
private val PriceColor = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudianteHomeScreen(
    navController: NavController,
    nombreUsuario: String = "Estudiante"
) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    val repository = remember { OfertasRepository(RetrofitClient.ofertaLaboralApi, token) }
    val viewModel: OfertasViewModel = viewModel(
        factory = OfertasViewModelFactory(repository)
    )

    val ofertas by viewModel.ofertas.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf("Todas") }
    var selectedTab by remember { mutableStateOf(0) }

    // Carga inicial
    LaunchedEffect(Unit) {
        viewModel.cargarActivas()
    }

    // Debounce para la búsqueda
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            delay(500)
            viewModel.buscarPorTexto(searchQuery)
        } else if (filtroSeleccionado == "Todas") {
            viewModel.cargarActivas()
        }
    }

    // Datos del usuario desde token
    val nombreEstudiante = remember {
        token.getNombre() ?: nombreUsuario
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bolsa de Empleo",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = {
                        token.clearSession()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Salir", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlueGradientStart
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val navItems = listOf(
                    "Inicio" to Icons.Default.Home,
                    "Búsqueda" to Icons.Default.Search,
                    "Postulaciones" to Icons.Outlined.AssignmentTurnedIn,
                    "Perfil" to Icons.Default.Person
                )

                navItems.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header con Gradiente y saludo
            HeaderSectionEstudiante(
                nombreUsuario = nombreEstudiante,
                onNotificationClick = { /* Mostrar notificaciones */ }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Filtros Rápidos
                item {
                    FiltrosSectionEstudiante(
                        seleccionado = filtroSeleccionado,
                        onFiltroClick = { filtro ->
                            filtroSeleccionado = filtro
                            searchQuery = ""
                            when (filtro) {
                                "Todas" -> viewModel.cargarActivas()
                                else -> viewModel.filtrarPorArea(filtro)
                            }
                        }
                    )
                }

                // Barra de búsqueda
                item {
                    SearchBarEstudiante(
                        searchQuery = searchQuery,
                        onSearchChange = {
                            searchQuery = it
                            filtroSeleccionado = "Todas"
                        },
                        onClearSearch = {
                            searchQuery = ""
                            viewModel.limpiarFiltros()
                        }
                    )
                }

                // Título de sección
                item {
                    Text(
                        text = if (searchQuery.isBlank()) "📌 Ofertas Recomendadas" else "🔍 Resultados para '$searchQuery'",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                // Estado de carga
                if (loading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = BlueGradientStart)
                        }
                    }
                }

                // Lista de ofertas
                items(ofertas) { oferta ->
                    JobCardEstudiante(
                        oferta = oferta,
                        onClick = {
                            navController.navigate("detalle_oferta/${oferta.idOferta}")
                        }
                    )
                }

                // Mensaje cuando no hay ofertas
                if (!loading && ofertas.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.LightGray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No se encontraron ofertas", color = Color.Gray)
                                Text(
                                    text = "Prueba con otros filtros o palabras clave",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
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
fun HeaderSectionEstudiante(
    nombreUsuario: String,
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
                        Spacer(modifier = Modifier.height(8.dp))
                        // Estadísticas rápidas
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "📊 150+ ofertas",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "🏢 50+ empresas",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
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
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FiltrosSectionEstudiante(
    seleccionado: String,
    onFiltroClick: (String) -> Unit
) {
    val filtros = listOf("Todas", "Tecnología", "Diseño", "Marketing", "Ventas", "Administración")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(filtros) { filtro ->
            val icono = when (filtro) {
                "Tecnología" -> "💻"
                "Diseño" -> "🎨"
                "Marketing" -> "📢"
                "Ventas" -> "💰"
                "Administración" -> "📋"
                else -> "📌"
            }

            FilterChip(
                selected = filtro == seleccionado,
                onClick = { onFiltroClick(filtro) },
                label = {
                    Text(
                        text = if (filtro == "Todas") filtro else "$icono $filtro",
                        fontSize = MaterialTheme.typography.labelMedium.fontSize
                    )
                },
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
fun SearchBarEstudiante(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "🔍 Buscar por cargo, área o título...",
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = BlueGradientStart
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Limpiar",
                            tint = Color.Gray
                        )
                    }
                }
            },
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

@Composable
fun JobCardEstudiante(
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
            // Icono de Empresa
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        oferta.area == null -> Icons.Default.Business
                        oferta.area.lowercase().contains("diseño") -> Icons.Default.Palette
                        oferta.area.lowercase().contains("desarrollo") ||
                                oferta.area.lowercase().contains("tecnología") ||
                                oferta.area.lowercase().contains("ti") -> Icons.Default.Code
                        oferta.area.lowercase().contains("venta") -> Icons.Default.TrendingUp
                        oferta.area.lowercase().contains("marketing") -> Icons.Default.Campaign
                        oferta.area.lowercase().contains("administración") -> Icons.Default.Business
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
                    text = oferta.area ?: "General",
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
                            text = oferta.modalidad.ifBlank { "Presencial" },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = ChipHybridText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Fecha
                    Text(
                        text = "• ${oferta.fechaPublicacion?.take(10) ?: "Reciente"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            // Salario
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