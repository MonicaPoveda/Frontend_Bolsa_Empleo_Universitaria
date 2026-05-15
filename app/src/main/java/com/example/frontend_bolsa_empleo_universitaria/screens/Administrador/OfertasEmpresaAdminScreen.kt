package com.example.frontend_bolsa_empleo_universitaria.screens.Administrador

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.frontend_bolsa_empleo_universitaria.model.*
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoColors
import com.example.frontend_bolsa_empleo_universitaria.viewModel.AdminViewModel

private val AdminIndigo = UniEmpleoColors.Navy
private val AdminIndigoLight = UniEmpleoColors.Blue
private val BackgroundGray = UniEmpleoColors.Background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfertasEmpresaAdminScreen(idEmpresa: Long, nombreEmpresa: String, navController: NavController, viewModel: AdminViewModel) {
    // Especificamos el valor inicial para ayudar a la inferencia de tipos
    val ofertas by viewModel.ofertasEmpresa.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val nombreMostrar = remember(nombreEmpresa) { Uri.decode(nombreEmpresa) }

    LaunchedEffect(idEmpresa) {
        viewModel.listarOfertasPorEmpresa(idEmpresa)
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Ofertas de Empleo", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(nombreMostrar, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminIndigo)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AdminIndigo)
                }
            } else if (ofertas.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay ofertas publicadas por esta empresa", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(ofertas) { oferta ->
                        AdminJobCard(
                            titulo = oferta.titulo,
                            modalidad = oferta.modalidad,
                            salario = oferta.salario.toInt().toString(),
                            onClick = {
                                val encodedTitulo = Uri.encode(oferta.titulo)
                                navController.navigate("postulantes_oferta/${oferta.idOferta}/$encodedTitulo")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminJobCard(titulo: String, modalidad: String, salario: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(AdminIndigo.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Work, contentDescription = null, tint = AdminIndigo)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(modalidad, color = AdminIndigoLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$$salario", fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                Text("Postulantes >", color = Color.Gray, fontSize = 10.sp)
            }
        }
    }
}
