package com.example.frontend_bolsa_empleo_universitaria.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontend_bolsa_empleo_universitaria.model.Usuario
import com.example.frontend_bolsa_empleo_universitaria.viewModel.NotificacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    usuario: Usuario?,
    onNavigateToPostulaciones: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotificaciones: () -> Unit,
    onLogout: () -> Unit,
    notifViewModel: NotificacionViewModel = viewModel()
) {
    val primaryBlue = Color(0xFF1A3C6E)
    val notificaciones by notifViewModel.notificaciones

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Hola, ${usuario?.nombre ?: "Usuario"}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Explora nuevas oportunidades",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (notificaciones.isNotEmpty()) {
                                Badge(containerColor = Color.Red) {
                                    Text(notificaciones.size.toString(), color = Color.White)
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(onClick = onNavigateToNotificaciones) {
                            Icon(Icons.Default.Notifications, null)
                        }
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryBlue,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
                .padding(24.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Buscar vacantes...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSearch() },
                enabled = false,
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = Color.White,
                    disabledBorderColor = Color.LightGray,
                    disabledPlaceholderColor = Color.Gray,
                    disabledLeadingIconColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Acciones Rápidas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HomeQuickActionCard(
                    icon = Icons.Default.Business,
                    label = "Empresas",
                    color = Color(0xFFE3F2FD),
                    iconColor = Color(0xFF1976D2),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSearch() }
                )
                HomeQuickActionCard(
                    icon = Icons.Default.Assignment,
                    label = "Postulaciones",
                    color = Color(0xFFF3E5F5),
                    iconColor = Color(0xFF7B1FA2),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPostulaciones
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Tu Perfil Profesional", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Card(
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = primaryBlue.copy(alpha = 0.1f), modifier = Modifier.size(48.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, tint = primaryBlue) }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("${usuario?.nombre ?: "Sin"} ${usuario?.apellido ?: "Nombre"}", fontWeight = FontWeight.Bold)
                            Text(usuario?.email ?: "Sin Email", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("Perfil Completo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
                    LinearProgressIndicator(
                        progress = { 1.0f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = primaryBlue,
                        trackColor = Color(0xFFE0E0E0)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        HomeInfoItem("Tipo", usuario?.tipoUsuario ?: "N/A")
                        HomeInfoItem("Estado", if (usuario?.estado == true) "Activo" else "Inactivo")
                    }
                }
            }
        }
    }
}

@Composable
fun HomeQuickActionCard(icon: ImageVector, label: String, color: Color, iconColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(modifier = modifier.clickable { onClick() }, color = color, shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun HomeInfoItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
