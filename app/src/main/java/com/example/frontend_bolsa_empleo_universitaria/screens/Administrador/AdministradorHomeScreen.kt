package com.example.frontend_bolsa_empleo_universitaria.screens.Administrador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.ui.platform.LocalContext
import com.example.frontend_bolsa_empleo_universitaria.utils.Token

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdministradorHomeScreen(navController: NavController) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Administrador") },
                actions = {
                    IconButton(onClick = { /* Estadísticas */ }) {
                        Icon(Icons.Default.BarChart, contentDescription = "Estadísticas")
                    }
                    IconButton(onClick = {
                        token.clearSession()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.People, contentDescription = "Usuarios") },
                    label = { Text("Usuarios") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Business, contentDescription = "Empresas") },
                    label = { Text("Empresas") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Work, contentDescription = "Ofertas") },
                    label = { Text("Ofertas") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> DashboardScreen(paddingValues)
            1 -> UsuariosScreen(paddingValues)
            2 -> EmpresasScreen(paddingValues)
            3 -> OfertasAdminScreen(paddingValues)
        }
    }
}

@Composable
fun DashboardScreen(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjetas de estadísticas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Usuarios Totales", style = MaterialTheme.typography.titleMedium)
                    Text("1,234", style = MaterialTheme.typography.headlineLarge)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Empresas", style = MaterialTheme.typography.titleMedium)
                    Text("156", style = MaterialTheme.typography.headlineLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Ofertas Activas", style = MaterialTheme.typography.titleMedium)
                    Text("89", style = MaterialTheme.typography.headlineLarge)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Postulaciones", style = MaterialTheme.typography.titleMedium)
                    Text("2,345", style = MaterialTheme.typography.headlineLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Actividad Reciente",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(listOf(
                "Nuevo usuario registrado: Juan Pérez",
                "Nueva empresa: Tech Solutions S.A.",
                "Nueva oferta: Desarrollador Android"
            )) { actividad ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = actividad,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UsuariosScreen(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Lista de Usuarios", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { /* Agregar usuario */ }) {
                Text("+ Agregar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(listOf(
                Triple("ana@email.com", "Ana García", "Estudiante"),
                Triple("carlos@email.com", "Carlos López", "Estudiante"),
                Triple("admin@sistema.com", "Admin User", "Administrador")
            )) { (email, nombre, rol) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(nombre, style = MaterialTheme.typography.titleMedium)
                            Text(email, style = MaterialTheme.typography.bodySmall)
                            Text(rol, style = MaterialTheme.typography.bodySmall)
                        }
                        Row {
                            IconButton(onClick = { /* Editar */ }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = { /* Eliminar */ }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmpresasScreen(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text("Empresas Registradas", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(listOf(
                Triple("Tech Solutions", "tech@techsol.com", "Tecnología"),
                Triple("Global Consulting", "info@global.com", "Consultoría"),
                Triple("Creative Studio", "contact@creative.com", "Diseño")
            )) { (nombre, email, sector) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(nombre, style = MaterialTheme.typography.titleMedium)
                            Text(email, style = MaterialTheme.typography.bodySmall)
                            Text(sector, style = MaterialTheme.typography.bodySmall)
                        }
                        Button(onClick = { /* Ver detalles */ }) {
                            Text("Ver")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfertasAdminScreen(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text("Ofertas de Empleo", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(listOf(
                "Desarrollador Android - Tech Solutions",
                "Diseñador UX - Creative Studio",
                "Project Manager - Global Consulting"
            )) { oferta ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(oferta, style = MaterialTheme.typography.bodyLarge)
                        Row {
                            IconButton(onClick = { /* Editar */ }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = { /* Eliminar */ }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}