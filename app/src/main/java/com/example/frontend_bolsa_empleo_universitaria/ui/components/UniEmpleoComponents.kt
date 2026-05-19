package com.example.frontend_bolsa_empleo_universitaria.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.frontend_bolsa_empleo_universitaria.R
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens

/** @deprecated Usar [BolsaTokens.Palette]; se mantiene por compatibilidad con imports existentes. */
object UniEmpleoColors {
    val Navy = BolsaTokens.Palette.HeaderStart
    val Blue = BolsaTokens.Palette.Primary
    val Teal = BolsaTokens.Palette.Info
    val Gold = BolsaTokens.Palette.Accent
    val Background = BolsaTokens.Palette.Background
    val Surface = BolsaTokens.Palette.Surface
    val SurfaceSoft = BolsaTokens.Palette.PrimaryLight
    val Text = BolsaTokens.Palette.TextPrimary
    val Muted = BolsaTokens.Palette.TextSecondary
    val Success = BolsaTokens.Palette.Success
    val Warning = BolsaTokens.Palette.Warning
    val Danger = BolsaTokens.Palette.Error
}

object UniEmpleoDimens {
    val ScreenPadding = BolsaTokens.Dimens.screenPadding
    val CardRadius = BolsaTokens.Dimens.cardRadius
    val SectionRadius = 28.dp
    val FieldRadius = BolsaTokens.Dimens.fieldRadius
}

val UniEmpleoGradient: Brush get() = BolsaTokens.headerGradientVertical

@Composable
fun UniEmpleoLogo(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    cornerRadius: Dp = 18.dp,
    imageScale: Float = 1.22f
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(containerColor)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = "UNIEMPLEO",
            modifier = Modifier
                .fillMaxWidth()
                .scale(imageScale)
                .alpha(0.96f),
            contentScale = ContentScale.Crop
        )
    }
}

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
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(BolsaTokens.Dimens.touchMin)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White,
                            modifier = Modifier.size(BolsaTokens.Dimens.iconMd)
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
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
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
    unfocusedBorderColor = BolsaTokens.Palette.Divider,
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
