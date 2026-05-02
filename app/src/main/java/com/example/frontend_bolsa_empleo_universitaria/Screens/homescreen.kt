package com.example.frontend_bolsa_empleo_universitaria.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material.icons.outlined.WifiTethering
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon as M3Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend_bolsa_empleo_universitaria.Model.OfertaLaboral
import com.example.frontend_bolsa_empleo_universitaria.ViewModel.OfertasViewModel

private val AzulPrimario = Color(0xFF1A3C6E)
private val AzulSecundario = Color(0xFF2D6BE4)
private val FondoGris = Color(0xFFF4F6FA)
private val TextoGris = Color(0xFF8A94A6)
private val TextoOscuro = Color(0xFF1C2A3A)
private val Verde = Color(0xFF27AE60)
private val Naranja = Color(0xFFF39C12)
private val Blanco = Color.White

private data class NavItem(
    val label: String,
    val iconOutline: ImageVector,
    val iconFilled: ImageVector
)

private val navItems = listOf(
    NavItem("Buscar", Icons.Outlined.Search, Icons.Filled.Search),
    NavItem("Postulaciones", Icons.Outlined.AssignmentTurnedIn, Icons.Outlined.AssignmentTurnedIn),
    NavItem("Perfil", Icons.Outlined.Person, Icons.Filled.Person)
)

private val empresaNombres = mapOf(
    1L to "Tech Solutions S.A.",
    2L to "Global Finance",
    3L to "Creative Studio",
    4L to "DesignHub"
)

@Composable
fun HomeScreen(
    ofertaViewModel: OfertasViewModel = viewModel(),
    onOfertaClick: (OfertaLaboral) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onBuscarClick: () -> Unit = {},
    onPostulacionesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var busqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        ofertaViewModel.cargarActivas()
    }

    val ofertas by ofertaViewModel.ofertas
    val loading by ofertaViewModel.loading

    Scaffold(
        containerColor = FondoGris,
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    selectedTab = index
                    when (index) {
                        0 -> onBuscarClick()
                        1 -> onPostulacionesClick()
                        2 -> onPerfilClick()
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                HeaderConBusqueda(
                    busqueda = busqueda,
                    onBusquedaChange = { busqueda = it },
                    onNotificationClick = onNotificationClick
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ofertas Recomendadas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextoOscuro
                    )
                    Text(
                        text = "Ver todas",
                        fontSize = 13.sp,
                        color = AzulSecundario,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AzulSecundario)
                    }
                }
            }

            if (!loading) {
                items(items = ofertas, key = { it.idOferta }) { oferta ->
                    OfertaCard(
                        oferta = oferta,
                        empresaNombre = empresaNombres[oferta.idEmpresa] ?: "Empresa",
                        onClick = { onOfertaClick(oferta) }
                    )
                }
            }

            if (!loading && ofertas.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            M3Icon(
                                imageVector = Icons.Outlined.Work,
                                contentDescription = "Sin ofertas",
                                tint = TextoGris,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay ofertas disponibles",
                                fontSize = 16.sp,
                                color = TextoGris,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderConBusqueda(
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    onNotificationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AzulPrimario, AzulSecundario)
                )
            )
            .padding(horizontal = 20.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4A90D9)),
                contentAlignment = Alignment.Center
            ) {
                M3Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Avatar",
                    tint = Blanco,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Hola, Sofía 👋", color = Color(0xFFB8D4F5), fontSize = 13.sp)
                Text(
                    text = "Encuentra tu trabajo ideal",
                    color = Blanco,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            IconButton(onClick = onNotificationClick) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    M3Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Blanco,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            placeholder = {
                Text(
                    text = "Buscar puesto, empresa o área...",
                    color = TextoGris,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                M3Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Buscar",
                    tint = TextoGris
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Blanco,
                unfocusedContainerColor = Blanco,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = AzulPrimario,
                unfocusedTextColor = AzulPrimario,
                cursorColor = AzulSecundario
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun OfertaCard(
    oferta: OfertaLaboral,
    empresaNombre: String,
    onClick: () -> Unit
) {
    var guardado by remember { mutableStateOf(false) }

    val iconoArea: ImageVector = when {
        oferta.area.contains("Tecnolog", ignoreCase = true) -> Icons.Outlined.Code
        oferta.area.contains("Finanza", ignoreCase = true) -> Icons.Outlined.BarChart
        oferta.area.contains("Marketing", ignoreCase = true) -> Icons.Outlined.Campaign
        oferta.area.contains("Diseño", ignoreCase = true) -> Icons.Outlined.Brush
        else -> Icons.Outlined.Work
    }

    val colorIcono: Color = when {
        oferta.area.contains("Tecnolog", ignoreCase = true) -> AzulSecundario
        oferta.area.contains("Finanza", ignoreCase = true) -> Verde
        oferta.area.contains("Marketing", ignoreCase = true) -> Naranja
        else -> Color(0xFF9B59B6)
    }

    val colorModalidad: Color = when (oferta.modalidad.lowercase()) {
        "remoto" -> Color(0xFF1A7A4A)
        "híbrido", "hibrido" -> Color(0xFF1A5FA0)
        else -> Color(0xFF7A4A1A)
    }

    val iconoModalidad: ImageVector = when (oferta.modalidad.lowercase()) {
        "remoto" -> Icons.Outlined.WifiTethering
        "híbrido", "hibrido" -> Icons.Outlined.SyncAlt
        else -> Icons.Outlined.LocationOn
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorIcono.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    M3Icon(
                        imageVector = iconoArea,
                        contentDescription = oferta.area,
                        tint = colorIcono,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = oferta.titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextoOscuro,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = empresaNombre, fontSize = 12.sp, color = TextoGris)
                }

                IconButton(
                    onClick = { guardado = !guardado },
                    modifier = Modifier.size(32.dp)
                ) {
                    M3Icon(
                        imageVector = if (guardado) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Guardar",
                        tint = if (guardado) AzulSecundario else TextoGris,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = FondoGris, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    M3Icon(
                        imageVector = Icons.Outlined.AttachMoney,
                        contentDescription = "Salario",
                        tint = TextoGris,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$${oferta.salario.toInt().toCommas()} USD",
                        fontSize = 12.sp,
                        color = TextoGris
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = colorModalidad.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        M3Icon(
                            imageVector = iconoModalidad,
                            contentDescription = oferta.modalidad,
                            tint = colorModalidad,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = oferta.modalidad,
                            fontSize = 11.sp,
                            color = colorModalidad,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    M3Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Jornada",
                        tint = TextoGris,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "Tiempo completo", fontSize = 11.sp, color = TextoGris)
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Blanco,
        tonalElevation = 8.dp
    ) {
        navItems.forEachIndexed { index, item ->
            val selected = selectedTab == index
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(index) },
                icon = {
                    M3Icon(
                        imageVector = if (selected) item.iconFilled else item.iconOutline,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AzulSecundario,
                    selectedTextColor = AzulSecundario,
                    unselectedIconColor = TextoGris,
                    unselectedTextColor = TextoGris,
                    indicatorColor = Color(0xFFE8F0FE)
                )
            )
        }
    }
}

private fun Int.toCommas(): String = "%,d".format(this)

@Preview
@Composable
fun HomeScreenPreview() {
    MaterialTheme { HomeScreen() }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenConDatosPreview() {
    val ofertasMock = listOf(
        OfertaLaboral(
            1, "Desarrollador Frontend Jr.", "Diseño de interfaces web",
            "Tecnología", 1200.0, "Remoto", "2025-04-01", "2025-05-01", true, 1
        ),
        OfertaLaboral(
            2, "Analista de Datos", "Análisis y visualización de datos",
            "Finanzas", 1000.0, "Híbrido", "2025-04-05", "2025-05-15", true, 2
        )
    )

    MaterialTheme {
        LazyColumn {
            items(ofertasMock) { oferta ->
                OfertaCard(
                    oferta = oferta,
                    empresaNombre = "Empresa de Ejemplo",
                    onClick = {}
                )
            }
        }
    }
}