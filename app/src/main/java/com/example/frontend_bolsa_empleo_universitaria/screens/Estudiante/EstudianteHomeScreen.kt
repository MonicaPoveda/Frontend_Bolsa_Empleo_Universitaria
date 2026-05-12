package com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralResponse
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaSearchFilters
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.components.BolsaEmptySearchState
import com.example.frontend_bolsa_empleo_universitaria.ui.components.BolsaFilterTextField
import com.example.frontend_bolsa_empleo_universitaria.ui.components.BolsaFilterToggleRow
import com.example.frontend_bolsa_empleo_universitaria.ui.components.BolsaPrimarySearchBar
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    val nombreUsuario = token.getUserNombre()

    val repository = remember { OfertasRepository(RetrofitClient.ofertaLaboralApi) }
    val empresaRepository = remember { EmpresaRepository(RetrofitClient.empresaApi) }
    val viewModel: OfertasViewModel = viewModel(
        factory = OfertasViewModelFactory(repository, empresaRepository)
    )

    var filtrosLocales by remember { mutableStateOf(OfertaSearchFilters()) }
    var filtrosAvanzados by remember { mutableStateOf(false) }
    var salarioMinStr by remember { mutableStateOf("") }
    var salarioMaxStr by remember { mutableStateOf("") }

    val ofertas = viewModel.ofertas.value
    val loading = viewModel.loading.value
    val error = viewModel.error.value
    val empMap by viewModel.empresaNombrePorId

    LaunchedEffect(Unit) {
        viewModel.cargarActivas()
    }

    LaunchedEffect(filtrosLocales, salarioMinStr, salarioMaxStr) {
        delay(380)
        val min = salarioMinStr.replace(",", ".").trim().toDoubleOrNull()
        val max = salarioMaxStr.replace(",", ".").trim().toDoubleOrNull()
        viewModel.setSearchFilters(
            filtrosLocales.copy(
                salarioMin = min,
                salarioMax = max
            )
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = BolsaTokens.Palette.Surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BolsaTokens.headerGradientVertical)
                        .padding(vertical = 28.dp, horizontal = 22.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = nombreUsuario,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = token.getUserEmail() ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Menú",
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = BolsaTokens.Palette.TextSecondary
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

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BolsaTokens.Palette.Divider)

                Text(
                    text = "Cuenta",
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = BolsaTokens.Palette.TextSecondary
                )

                DrawerMenuItemGmail(
                    icon = Icons.Default.Settings,
                    text = "Configuración de la cuenta",
                    badge = null,
                    selected = selectedDrawerItem == "configuracion"
                ) {
                    selectedDrawerItem = "configuracion"
                    scope.launch { drawerState.close() }
                    navController.navigate("configuracion_cuenta")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BolsaTokens.Palette.Divider)

                DrawerMenuItemGmail(
                    icon = Icons.Default.Logout,
                    text = "Cerrar sesión",
                    badge = null,
                    iconTint = BolsaTokens.Palette.Error,
                    textColor = BolsaTokens.Palette.Error,
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
            containerColor = BolsaTokens.Palette.Background,
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.size(BolsaTokens.Dimens.touchMin)
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Abrir menú",
                                tint = Color.White,
                                modifier = Modifier.size(BolsaTokens.Dimens.iconLg)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BolsaTokens.Palette.HeaderStart
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = BolsaTokens.Palette.Surface,
                    tonalElevation = 6.dp
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
                            label = { Text(item.label, style = MaterialTheme.typography.labelMedium) },
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
            val activos = filtrosLocales.run {
                listOf(
                    nombreEmpresa.isNotBlank(),
                    cargo.isNotBlank(),
                    carrera.isNotBlank(),
                    oficio.isNotBlank(),
                    salarioMinStr.isNotBlank() || salarioMaxStr.isNotBlank(),
                    !modalidad.isNullOrBlank()
                ).count { it }
            }
            val listHorizontal = Modifier.padding(horizontal = BolsaTokens.Dimens.screenPadding)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Column(Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(118.dp)
                                .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                                .background(BolsaTokens.headerGradientVertical)
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WorkOutline,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            text = "UNIEMPLEO",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = "Tu carrera merece las mejores oportunidades",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.88f)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = BolsaTokens.Dimens.screenPadding),
                            shape = RoundedCornerShape(BolsaTokens.Dimens.cardRadius),
                            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                            colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface)
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                BolsaPrimarySearchBar(
                                    value = filtrosLocales.textoLibre,
                                    onValueChange = { v -> filtrosLocales = filtrosLocales.copy(textoLibre = v) },
                                    placeholder = "Buscar ofertas, empresas, áreas o palabras clave…"
                                )
                            }
                        }
                    }
                }

                item {
                    Column(listHorizontal.then(Modifier.fillMaxWidth())) {
                        BolsaFilterToggleRow(
                            expanded = filtrosAvanzados,
                            onToggle = { filtrosAvanzados = !filtrosAvanzados },
                            activeFilterCount = activos
                        )
                    }
                }

                item {
                    Column(listHorizontal.then(Modifier.fillMaxWidth())) {
                        AnimatedVisibility(
                            visible = filtrosAvanzados,
                            enter = fadeIn() + slideInVertically { it / 8 }
                        ) {
                            Column(Modifier.fillMaxWidth()) {
                                BolsaFilterTextField(
                                    label = "Empresa",
                                    value = filtrosLocales.nombreEmpresa,
                                    onValueChange = { filtrosLocales = filtrosLocales.copy(nombreEmpresa = it) },
                                    placeholder = "Nombre comercial o parte del nombre"
                                )
                                Spacer(Modifier.height(10.dp))
                                BolsaFilterTextField(
                                    label = "Cargo / puesto",
                                    value = filtrosLocales.cargo,
                                    onValueChange = { filtrosLocales = filtrosLocales.copy(cargo = it) },
                                    placeholder = "Coincide con el título de la oferta"
                                )
                                Spacer(Modifier.height(10.dp))
                                BolsaFilterTextField(
                                    label = "Carrera o área",
                                    value = filtrosLocales.carrera,
                                    onValueChange = { filtrosLocales = filtrosLocales.copy(carrera = it) },
                                    placeholder = "Área académica o campo de la oferta"
                                )
                                Spacer(Modifier.height(10.dp))
                                BolsaFilterTextField(
                                    label = "Oficio",
                                    value = filtrosLocales.oficio,
                                    onValueChange = { filtrosLocales = filtrosLocales.copy(oficio = it) },
                                    placeholder = "Palabras en título o descripción"
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Salario mensual (opcional)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = BolsaTokens.Palette.TextSecondary
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    BolsaFilterTextField(
                                        label = "Mínimo",
                                        value = salarioMinStr,
                                        onValueChange = { salarioMinStr = it.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' } },
                                        modifier = Modifier.weight(1f),
                                        placeholder = "Ej. 1000"
                                    )
                                    BolsaFilterTextField(
                                        label = "Máximo",
                                        value = salarioMaxStr,
                                        onValueChange = { salarioMaxStr = it.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' } },
                                        modifier = Modifier.weight(1f),
                                        placeholder = "Ej. 5000"
                                    )
                                }
                                Spacer(Modifier.height(14.dp))
                                Text(
                                    "Modalidad",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = BolsaTokens.Palette.TextSecondary
                                )
                                Spacer(Modifier.height(8.dp))
                                ModalidadChips(
                                    seleccionado = filtrosLocales.modalidad,
                                    onSelect = { m ->
                                        filtrosLocales = filtrosLocales.copy(modalidad = m)
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Column(listHorizontal.then(Modifier.fillMaxWidth())) {
                        FiltrosCategoriaSection(
                            seleccionado = filtrosLocales.categoria,
                            onFiltroClick = { cat ->
                                filtrosLocales = filtrosLocales.copy(categoria = cat)
                            }
                        )
                    }
                }

                item {
                    Column(listHorizontal.then(Modifier.fillMaxWidth())) {
                        if (error != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.PrimaryLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = error,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BolsaTokens.Palette.Error
                                )
                            }
                        } else {
                            Text(
                                text = if (filtrosLocales.textoLibre.isBlank()) "Ofertas recientes" else "Resultados de búsqueda",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BolsaTokens.Palette.TextPrimary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (loading) {
                    item {
                        Column(listHorizontal.then(Modifier.fillMaxWidth())) {
                            Box(
                                Modifier.fillMaxWidth().padding(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = BolsaTokens.Palette.Primary)
                            }
                        }
                    }
                }

                items(ofertas, key = { it.idOferta }) { oferta ->
                    Column(listHorizontal.then(Modifier.fillMaxWidth())) {
                        val nombreEmp = empMap[oferta.idEmpresa].orEmpty()
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically { it / 10 }
                        ) {
                            JobCard(
                                oferta = oferta,
                                nombreEmpresa = nombreEmp,
                                onClick = {
                                    navController.navigate("detalle_oferta_estudiante/${oferta.idOferta}")
                                }
                            )
                        }
                    }
                }

                if (!loading && ofertas.isEmpty()) {
                    item {
                        Column(listHorizontal.then(Modifier.fillMaxWidth())) {
                            BolsaEmptySearchState(
                                title = "Sin resultados por ahora",
                                subtitle = "Prueba a combinar menos filtros o usa la búsqueda por palabras clave.",
                                icon = Icons.Default.Search
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun ModalidadChips(
    seleccionado: String?,
    onSelect: (String?) -> Unit
) {
    data class Opcion(val etiqueta: String, val valor: String?)
    val opciones = listOf(
        Opcion("Todas", null),
        Opcion("Presencial", "Presencial"),
        Opcion("Remoto", "Remoto"),
        Opcion("Híbrido", "Híbrido")
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(opciones.size) { idx ->
            val (label, value) = opciones[idx].etiqueta to opciones[idx].valor
            val sel = if (value == null) {
                seleccionado.isNullOrBlank()
            } else {
                seleccionado?.contains(value, ignoreCase = true) == true ||
                    (value == "Híbrido" && seleccionado?.contains("Hibrido", ignoreCase = true) == true)
            }
            FilterChip(
                selected = sel,
                onClick = { onSelect(value) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BolsaTokens.Palette.Primary,
                    selectedLabelColor = Color.White,
                    containerColor = BolsaTokens.Palette.Surface,
                    labelColor = BolsaTokens.Palette.TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = sel,
                    borderColor = BolsaTokens.Palette.Divider,
                    selectedBorderColor = BolsaTokens.Palette.Primary
                )
            )
        }
    }
}

@Composable
fun FiltrosCategoriaSection(
    seleccionado: String,
    onFiltroClick: (String) -> Unit
) {
    val filtros = listOf("Todas", "Diseño", "Desarrollo", "Marketing", "Ventas", "TI")
    Column {
        Text(
            "Áreas sugeridas",
            style = MaterialTheme.typography.labelLarge,
            color = BolsaTokens.Palette.TextSecondary
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(filtros) { filtro ->
                FilterChip(
                    selected = filtro == seleccionado,
                    onClick = { onFiltroClick(filtro) },
                    label = { Text(filtro) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BolsaTokens.Palette.Primary,
                        selectedLabelColor = Color.White,
                        containerColor = BolsaTokens.Palette.Surface,
                        labelColor = BolsaTokens.Palette.TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filtro == seleccionado,
                        borderColor = BolsaTokens.Palette.Divider,
                        selectedBorderColor = BolsaTokens.Palette.Primary
                    )
                )
            }
        }
    }
}

@Composable
fun JobCard(
    oferta: OfertaLaboralResponse,
    nombreEmpresa: String = "",
    onClick: () -> Unit
) {
    val fechaPublicacionStr = oferta.fechaPublicacion.let { fecha ->
        try {
            val partes = fecha.split("-")
            if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fecha
        } catch (e: Exception) {
            fecha
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(BolsaTokens.Dimens.cardRadius),
        colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BolsaTokens.Palette.PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (oferta.area.lowercase()) {
                        "diseño" -> Icons.Default.Palette
                        "desarrollo", "ti" -> Icons.Default.Code
                        "ventas" -> Icons.AutoMirrored.Filled.TrendingUp
                        "marketing" -> Icons.Default.Campaign
                        else -> Icons.Default.Business
                    },
                    contentDescription = null,
                    tint = BolsaTokens.Palette.Primary,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = oferta.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = BolsaTokens.Palette.TextPrimary,
                    maxLines = 2
                )
                if (nombreEmpresa.isNotBlank()) {
                    Text(
                        text = nombreEmpresa,
                        style = MaterialTheme.typography.labelLarge,
                        color = BolsaTokens.Palette.Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = oferta.area,
                    style = MaterialTheme.typography.bodySmall,
                    color = BolsaTokens.Palette.TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.PrimaryLight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (oferta.modalidad.isNotBlank()) oferta.modalidad else "Modalidad",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = BolsaTokens.Palette.Primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• $fechaPublicacionStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = BolsaTokens.Palette.TextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (oferta.salario > 0) "$${oferta.salario.toInt()}" else "—",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = BolsaTokens.Palette.Success
                )
                Text(
                    text = "/mes",
                    style = MaterialTheme.typography.labelSmall,
                    color = BolsaTokens.Palette.TextSecondary
                )
            }
        }
    }
}

@Composable
fun DrawerMenuItemGmail(
    icon: ImageVector,
    text: String,
    badge: String?,
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
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = text,
            tint = if (selected) BolsaTokens.Palette.Primary else iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = text,
            color = if (selected) BolsaTokens.Palette.Primary else textColor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        if (badge != null) {
            Card(colors = CardDefaults.cardColors(containerColor = badgeColor), shape = RoundedCornerShape(50)) {
                Text(
                    text = badge,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
