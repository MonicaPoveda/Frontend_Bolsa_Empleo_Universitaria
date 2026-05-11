package com.example.frontend_bolsa_empleo_universitaria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object UniEmpleoColors {
    val Navy = Color(0xFF123047)
    val Blue = Color(0xFF1E5A7A)
    val Teal = Color(0xFF0F8B8D)
    val Gold = Color(0xFFE7B75F)
    val Background = Color(0xFFF4F8FA)
    val Surface = Color.White
    val SurfaceSoft = Color(0xFFEAF3F5)
    val Text = Color(0xFF17212B)
    val Muted = Color(0xFF647782)
    val Success = Color(0xFF2E7D32)
    val Warning = Color(0xFFB45309)
    val Danger = Color(0xFFE53935)
}

object UniEmpleoDimens {
    val ScreenPadding = 16.dp
    val CardRadius = 18.dp
    val SectionRadius = 28.dp
    val FieldRadius = 16.dp
}

val UniEmpleoGradient = Brush.verticalGradient(
    listOf(UniEmpleoColors.Navy, UniEmpleoColors.Blue)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniEmpleoScaffold(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = UniEmpleoColors.Background,
        topBar = {
            UniEmpleoTopBar(
                title = title,
                onBack = onBack,
                navigationIcon = navigationIcon,
                actions = actions
            )
        },
        bottomBar = bottomBar,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniEmpleoTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        navigationIcon = {
            when {
                navigationIcon != null -> navigationIcon()
                onBack != null -> {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = UniEmpleoColors.Navy,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun UniEmpleoHero(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    height: Dp = 150.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                brush = UniEmpleoGradient,
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(alpha = 0.82f), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun UniEmpleoCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(UniEmpleoDimens.CardRadius),
        colors = CardDefaults.cardColors(containerColor = UniEmpleoColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content = { Box(Modifier.padding(16.dp)) { content() } }
    )
}

@Composable
fun uniEmpleoTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = UniEmpleoColors.Blue,
    unfocusedBorderColor = Color(0xFFB8C8CF),
    focusedContainerColor = UniEmpleoColors.Surface,
    unfocusedContainerColor = UniEmpleoColors.Surface,
    focusedLabelColor = UniEmpleoColors.Blue,
    cursorColor = UniEmpleoColors.Blue
)

@Composable
fun UniEmpleoEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector = Icons.Default.Inbox
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, modifier = Modifier.size(56.dp), tint = UniEmpleoColors.Muted.copy(alpha = 0.55f))
        Spacer(modifier = Modifier.height(10.dp))
        Text(title, color = UniEmpleoColors.Text, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = UniEmpleoColors.Muted, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun uniEmpleoButtonColors() = ButtonDefaults.buttonColors(
    containerColor = UniEmpleoColors.Blue,
    contentColor = Color.White
)
