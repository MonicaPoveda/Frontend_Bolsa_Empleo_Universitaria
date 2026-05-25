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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    
    var searchQuery by remember { mutableStateOf("") }
    
    var empresaAEliminar by remember { mutableStateOf<Long?>(null) }
    var nombreEmpresaAEliminar by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.listarEmpresasAceptadas()
    }

    val empresasFiltradas = empresas.filter {
        it.nombre?.contains(searchQuery, ignoreCase = true) == true ||
        it.sector?.contains(searchQuery, ignoreCase = true) == true ||
        it.ciudad?.contains(searchQuery, ignoreCase = true) == true
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
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar por nombre, sector o ciudad...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = AdminIndigo) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, null, tint = Color.Gray)
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AdminIndigo,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                if (isLoading && empresas.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AdminIndigo)
                    }
                } else if (empresasFiltradas.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            if (searchQuery.isEmpty()) "No hay empresas registradas" 
                            else "No se encontraron resultados para '$searchQuery'", 
                            color = Color.Gray
                        )
                    }
                } else {
                    // Contador de empresas
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Business, null, tint = AdminIndigo, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "${empresas.size} empresa${if (empresas.size != 1) "s" else ""} registrada${if (empresas.size != 1) "s" else ""}",
                            color = AdminIndigo,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(empresasFiltradas) { empresa ->
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AdminIndigoLight.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                ProfilePhotoDisplay(
                    photoUrl = "https://backend-sistema-empleo-universitario.onrender.com/api/archivos/foto/empresa/$idEmpresa",
                    placeholderIcon = Icons.Default.Business,
                    size = 58,
                    modifier = Modifier.clip(RoundedCornerShape(14.dp))
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = AdminIndigoLight.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = sector,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = AdminIndigoLight,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = ciudad, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            IconButton(onClick = { onDelete() }) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Red.copy(alpha = 0.08f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFBBBBBB),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
