package com.example.frontend_bolsa_empleo_universitaria.screens.Empresa

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
fun EmpresaHomeScreen(navController: NavController) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Empresa") },
                actions = {
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
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
                    icon = { Icon(Icons.Default.Business, contentDescription = "Mis Ofertas") },
                    label = { Text("Mis Ofertas") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Nueva Oferta") },
                    label = { Text("Nueva Oferta") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.People, contentDescription = "Postulantes") },
                    label = { Text("Postulantes") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Configuración") },
                    label = { Text("Configuración") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> MisOfertasScreen(paddingValues)
            1 -> NuevaOfertaScreen(paddingValues)
            2 -> PostulantesScreen(paddingValues)
            3 -> ConfiguracionEmpresaScreen(paddingValues)
        }
    }
}

@Composable
fun MisOfertasScreen(paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        items(listOf(
            "Desarrollador Android Senior",
            "Diseñador UX/UI",
            "Project Manager"
        )) { oferta ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(oferta, style = MaterialTheme.typography.titleMedium)
                    Text("Publicado: 15/05/2026", style = MaterialTheme.typography.bodySmall)
                    Row {
                        Text("5 postulantes", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(16.dp))
                        TextButton(onClick = { /* Editar */ }) {
                            Text("Editar")
                        }
                        TextButton(onClick = { /* Eliminar */ }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NuevaOfertaScreen(paddingValues: PaddingValues) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var requisitos by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = "Publicar Nueva Oferta",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título de la oferta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = requisitos,
            onValueChange = { requisitos = it },
            label = { Text("Requisitos") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Publicar oferta */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar Oferta")
        }
    }
}

@Composable
fun PostulantesScreen(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = "Postulantes Recientes",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(listOf(
                Triple("Juan Pérez", "Desarrollador Android", "CV_2026.pdf"),
                Triple("Maria García", "Diseñadora UX", "Portafolio_2026.pdf"),
                Triple("Carlos López", "Project Manager", "Experiencia.pdf")
            )) { (nombre, perfil, archivo) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(nombre, style = MaterialTheme.typography.titleMedium)
                            Text(perfil, style = MaterialTheme.typography.bodySmall)
                            Text(archivo, style = MaterialTheme.typography.bodySmall)
                        }
                        Button(onClick = { /* Ver CV */ }) {
                            Text("Ver CV")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfiguracionEmpresaScreen(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = "Configuración de la Empresa",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Datos de la empresa", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Nombre: Tech Solutions S.A.")
                Text("Email: contacto@techsolutions.com")
                Text("Teléfono: +123 456 7890")
                Text("Sitio web: www.techsolutions.com")

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* Editar perfil */ }) {
                    Text("Editar Perfil")
                }
            }
        }
    }
}