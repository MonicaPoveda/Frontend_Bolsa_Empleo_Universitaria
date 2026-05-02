package com.example.frontend_bolsa_empleo_universitaria.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Colores Corporativos ──────────────────────────────────────────────────
private val AzulPrimario   = Color(0xFF1A3C6E)
private val AzulSecundario = Color(0xFF2D6BE4)
private val FondoGris      = Color(0xFFF4F6FA)
private val TextoGris      = Color(0xFF8A94A6)
private val TextoOscuro    = Color(0xFF1C2A3A)
private val Blanco         = Color.White

// ─── Modelo de Datos ────────────────────────────────────────────────────────
data class Notificacion(
    val id: Int,
    val categoria: String,
    val titulo: String,
    val descripcion: String?,
    val tiempo: String,
    val tipo: TipoNotificacion,
    val estado: String? = null
)

enum class TipoNotificacion {
    NUEVA_OFERTA, POSTULACION, PERFIL
}

@Composable
fun NotificacionScreen(
    onBack: () -> Unit = {}
) {
    // Datos de ejemplo basados en la imagen
    val notificaciones = listOf(
        Notificacion(
            1, "NUEVA OFERTA", "Nueva oferta acorde a tu perfil",
            "Hemos encontrado una nueva oportunidad laboral...", "Hace 2 horas",
            TipoNotificacion.NUEVA_OFERTA
        ),
        Notificacion(
            2, "POSTULACIONES", "Cambio de estado en tu postulación a TechCorp",
            null, "Ayer", TipoNotificacion.POSTULACION, "En Revisión"
        ),
        Notificacion(
            3, "PERFIL", "Tu CV ha sido visualizado",
            "Un reclutador ha accedido a tu currículum vitae a través del portal de la bolsa de empleo.",
            "Hace 3 días", TipoNotificacion.PERFIL
        )
    )

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Blanco)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notificaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPrimario
                    )
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = AzulPrimario
                        )
                    }
                }
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            }
        },
        containerColor = FondoGris
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notificaciones) { notificacion ->
                NotificacionCard(notificacion)
            }
        }
    }
}

@Composable
fun NotificacionCard(notificacion: Notificacion) {
    val icono: ImageVector
    val colorIconoFondo: Color
    val colorIconoTint: Color
    val tieneBordeLateral: Boolean

    when (notificacion.tipo) {
        TipoNotificacion.NUEVA_OFERTA -> {
            icono = Icons.Default.Work
            colorIconoFondo = Color(0xFFD6E4FF)
            colorIconoTint = AzulSecundario
            tieneBordeLateral = true
        }
        TipoNotificacion.POSTULACION -> {
            icono = Icons.AutoMirrored.Filled.Assignment
            colorIconoFondo = Color(0xFFF1F3F7)
            colorIconoTint = TextoGris
            tieneBordeLateral = false
        }
        TipoNotificacion.PERFIL -> {
            icono = Icons.Default.Visibility
            colorIconoFondo = Color(0xFFF1F3F7)
            colorIconoTint = TextoGris
            tieneBordeLateral = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Borde lateral azul para destacar nuevas ofertas
            if (tieneBordeLateral) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(AzulPrimario)
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Contenedor del Icono
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorIconoFondo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = colorIconoTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Texto y Contenido
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = notificacion.categoria,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoGris
                        )
                        Text(
                            text = notificacion.tiempo,
                            fontSize = 11.sp,
                            color = TextoGris
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = notificacion.titulo,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro,
                        lineHeight = 18.sp
                    )

                    if (notificacion.descripcion != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = notificacion.descripcion,
                            fontSize = 13.sp,
                            color = TextoGris,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 16.sp
                        )
                    }

                    // Badge de estado para postulaciones
                    if (notificacion.estado != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE8F0FE)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AzulPrimario)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = notificacion.estado,
                                    fontSize = 11.sp,
                                    color = AzulPrimario,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificacionScreenPreview() {
    NotificacionScreen()
}
