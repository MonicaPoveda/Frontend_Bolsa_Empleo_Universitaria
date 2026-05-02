package com.example.frontend_bolsa_empleo_universitaria.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend_bolsa_empleo_universitaria.Model.OfertaLaboral

// ─── Colores Corporativos ────────────────────────────────────────────────────
private val AzulPrimario = Color(0xFF1A3C6E)
private val AzulSecundario = Color(0xFF2D6BE4)
private val TextoGris = Color(0xFF8A94A6)
private val TextoOscuro = Color(0xFF1C2A3A)
private val Blanco = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOfertaScreen(
    oferta: OfertaLaboral,
    empresaNombre: String = "Empresa no disponible",
    onBack: () -> Unit = {},
    onEmpresaClick: (Long) -> Unit = {},
    onPostularseClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Detalle de Oferta",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Blanco),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = TextoOscuro,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Blanco
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Blanco
            ) {
                Button(
                    onClick = onPostularseClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
                ) {
                    Text(
                        "Postularse ahora",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blanco
                    )
                }
            }
        },
        containerColor = Blanco
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo Placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF0F5FF))
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = AzulPrimario,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título y Empresa
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = oferta.titulo,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = empresaNombre,
                    fontSize = 16.sp,
                    color = TextoGris,
                    modifier = Modifier.clickable { onEmpresaClick(oferta.idEmpresa) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Fila de Información (Salario, Modalidad)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Salario",
                    value = "$${oferta.salario.toInt()} USD",
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    icon = Icons.Default.LocationOn,
                    label = "Modalidad",
                    value = oferta.modalidad,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Descripción
            SectionTitle("Descripción del puesto")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FB)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = oferta.descripcion,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 15.sp,
                    color = TextoOscuro,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Fechas de la Oferta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionTitle("Fecha de apertura")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = AzulSecundario,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = oferta.fechaPublicacion,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextoOscuro
                            )
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    SectionTitle("Fecha de cierre")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventBusy,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = oferta.fechaCierre,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextoOscuro
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Área
            SectionTitle("Área")
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F5FF)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = oferta.area,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    color = AzulSecundario,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FB)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AzulSecundario, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 12.sp, color = TextoGris)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextoOscuro,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun DetalleOfertaPreview() {
    DetalleOfertaScreen(
        oferta = OfertaLaboral(
            titulo = "Desarrollador Frontend Jr.",
            descripcion = "Estamos buscando un Desarrollador Frontend Jr. apasionado por crear experiencias de usuario excepcionales. Trabajarás en colaboración con nuestro equipo de diseño y desarrollo backend.",
            area = "Tecnología de la Información (TI)",
            salario = 1500.0,
            modalidad = "Remoto",
            fechaPublicacion = "2024-05-15",
            fechaCierre = "2024-06-15"
        ),
        empresaNombre = "Tech Solutions S.A."
    )
}
