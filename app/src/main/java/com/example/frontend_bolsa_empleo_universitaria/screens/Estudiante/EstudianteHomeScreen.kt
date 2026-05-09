package com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.ui.platform.LocalContext
import com.example.frontend_bolsa_empleo_universitaria.utils.Token

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudianteHomeScreen(navController: NavController) {
    val context = LocalContext.current
    val token = remember { Token(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio - Estudiante") },
                actions = {
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Tarjeta de bienvenida
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "¡Bienvenido Estudiante!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Encuentra las mejores ofertas laborales para ti",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de ofertas destacadas
            Text(
                text = "Ofertas Destacadas",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de ofertas (ejemplo estático)
            LazyColumn {
                items(listOf(
                    "Desarrollador Android - Empresa Tech",
                    "Diseñador UI/UX - Estudio Creativo",
                    "Analista de Datos - Consultora Global"
                )) { oferta ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = oferta,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Publicado hace 2 días • Tiempo completo",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Button(
                                onClick = { /* Ver detalles */ },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Ver más")
                            }
                        }
                    }
                }
            }
        }
    }
}