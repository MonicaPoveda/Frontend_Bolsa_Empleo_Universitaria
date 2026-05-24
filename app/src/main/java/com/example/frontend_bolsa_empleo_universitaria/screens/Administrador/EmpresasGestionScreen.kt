package com.example.frontend_bolsa_empleo_universitaria.screens.Administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.ui.components.AdminMessageBanner
import com.example.frontend_bolsa_empleo_universitaria.ui.components.ProfilePhotoDisplay
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoColors
import com.example.frontend_bolsa_empleo_universitaria.viewModel.AdminViewModel

private val AdminIndigo = UniEmpleoColors.Navy
private val AdminIndigoLight = UniEmpleoColors.Blue
private val BackgroundGray = UniEmpleoColors.Background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpresasGestionScreen(navController: NavController, viewModel: AdminViewModel) {
    val empresas by viewModel.empresasAceptadas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val adminMessage by viewModel.adminMessage.collectAsState()
    
    var empresaAEliminar by remember { mutableStateOf<Long?>(null) }
    var nombreEmpresaAEliminar by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.listarEmpresasAceptadas()
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Empresa", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar a '$nombreEmpresaAEliminar' del directorio? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        empresaAEliminar?.let { viewModel.eliminarEmpresa(it) }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = { Text("Directorio Empresarial", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminIndigo)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (isLoading && empresas.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AdminIndigo)
                    }
                } else if (empresas.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay empresas registradas", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(empresas) { empresa ->
                            AdminEnterpriseCard(
                                idEmpresa = empresa.idEmpresa,
                                nombre = empresa.nombre ?: "Sin nombre",
                                sector = empresa.sector ?: "General",
                                ciudad = empresa.ciudad ?: "N/A",
                                onClick = {
                                    navController.navigate("perfil_empresa_admin/${empresa.idEmpresa}")
                                },
                                onDelete = {
                                    empresaAEliminar = empresa.idEmpresa
                                    nombreEmpresaAEliminar = empresa.nombre ?: ""
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
            
            // Banner centralizado
            AdminMessageBanner(
                state = adminMessage,
                onDismiss = { viewModel.dismissAdminMessage() },
                modifier = Modifier.align(Alignment.TopCenter).padding(top = padding.calculateTopPadding() + 8.dp)
            )
        }
    }
}

@Composable
fun AdminEnterpriseCard(idEmpresa: Long, nombre: String, sector: String, ciudad: String, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePhotoDisplay(
                photoUrl = "https://backend-sistema-empleo-universitario.onrender.com/api/archivos/foto/empresa/$idEmpresa",
                size = 52,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = sector,
                    style = MaterialTheme.typography.bodySmall,
                    color = AdminIndigoLight,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = ciudad, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            // Botón de eliminar con superficie de clic propia
            IconButton(
                onClick = { 
                    // No llamamos a onClick() de la tarjeta
                    onDelete() 
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red.copy(alpha = 0.8f)
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}
