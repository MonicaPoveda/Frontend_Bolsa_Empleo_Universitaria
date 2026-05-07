package com.example.frontend_bolsa_empleo_universitaria.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro Estudiante") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Correo Institucional") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }
    }
}
