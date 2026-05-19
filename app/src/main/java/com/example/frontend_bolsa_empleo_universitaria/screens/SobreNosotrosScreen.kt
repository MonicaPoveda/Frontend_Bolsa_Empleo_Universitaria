package com.example.frontend_bolsa_empleo_universitaria.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoLogo
import com.example.frontend_bolsa_empleo_universitaria.ui.components.UniEmpleoScaffold
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SobreNosotrosScreen(navController: NavController) {
    UniEmpleoScaffold(
        title = "Sobre nosotros",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BolsaTokens.Palette.Background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(BolsaTokens.Dimens.cardRadius),
                colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UniEmpleoLogo(
                        modifier = Modifier.size(96.dp),
                        containerColor = BolsaTokens.Palette.PrimaryLight,
                        cornerRadius = 24.dp
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "UNIEMPLEO",
                        style = MaterialTheme.typography.headlineMedium,
                        color = BolsaTokens.Palette.Primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Bolsa de Empleo Universitaria",
                        style = MaterialTheme.typography.titleSmall,
                        color = BolsaTokens.Palette.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Plataforma académica diseñada para conectar estudiantes, egresados y empresas mediante la publicación de ofertas, gestión de postulaciones y seguimiento de oportunidades laborales.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BolsaTokens.Palette.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            InfoSection(
                icon = Icons.Outlined.Verified,
                title = "Objetivo",
                body = "Facilitar una experiencia confiable, ordenada y profesional para que la comunidad universitaria acceda a oportunidades laborales y las empresas gestionen sus procesos de selección."
            )

            PeopleSection(
                title = "Desarrolladores",
                people = listOf("Monica Poveda", "Tania Beltrán", "Lizeth Moreno", "Fabián Meléndez", "Paula Ramírez")
            )

            InfoSection(
                icon = Icons.Outlined.School,
                title = "Información académica",
                body = "Docente: Alex Matallana\nInstitución: Universidad de Cundinamarca\nPrograma: Ingeniería de Sistemas y Computación\nMateria: Ingeniería de Software"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(BolsaTokens.Dimens.cardRadius),
                colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        text = "Tecnologías",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BolsaTokens.Palette.TextPrimary
                    )
                    Spacer(Modifier.height(10.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Android", "Kotlin", "Jetpack Compose", "Material 3", "Retrofit", "JWT", "Render").forEach {
                            AssistChip(onClick = {}, label = { Text(it) })
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetaPill("Versión 1.0")
                        MetaPill("Año 2026")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoSection(icon: ImageVector, title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(BolsaTokens.Dimens.cardRadius),
        colors = CardDefaults.cardColors(containerColor = BolsaTokens.Palette.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.Top) {
            Surface(color = BolsaTokens.Palette.PrimaryLight, shape = RoundedCornerShape(14.dp)) {
                Icon(icon, null, tint = BolsaTokens.Palette.Primary, modifier = Modifier.padding(10.dp).size(24.dp))
            }
            Column(Modifier.padding(start = 14.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BolsaTokens.Palette.TextPrimary)
                Spacer(Modifier.height(6.dp))
                Text(body, style = MaterialTheme.typography.bodyMedium, color = BolsaTokens.Palette.TextSecondary)
            }
        }
    }
}

@Composable
private fun PeopleSection(title: String, people: List<String>) {
    InfoSection(
        icon = Icons.Outlined.Groups,
        title = title,
        body = people.joinToString(separator = "\n")
    )
}

@Composable
private fun MetaPill(text: String) {
    Surface(color = BolsaTokens.Palette.PrimaryLight, shape = RoundedCornerShape(50.dp)) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium,
            color = BolsaTokens.Palette.Primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}
