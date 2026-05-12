package com.example.frontend_bolsa_empleo_universitaria.screens.Login

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.frontend_bolsa_empleo_universitaria.navigation.resolvePostSplashRoute
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val token = remember { Token(context) }
    var playIntro by remember { mutableStateOf(false) }

    val logoLift by animateFloatAsState(
        targetValue = if (playIntro) 0f else 72f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "logoLift"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (playIntro) 1f else 0.82f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "logoScale"
    )
    val brandAlpha by animateFloatAsState(
        targetValue = if (playIntro) 1f else 0f,
        animationSpec = tween(700, delayMillis = 280, easing = FastOutSlowInEasing),
        label = "brandAlpha"
    )
    val brandOffset by animateFloatAsState(
        targetValue = if (playIntro) 0f else 28f,
        animationSpec = tween(800, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "brandOffset"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (playIntro) 1f else 0f,
        animationSpec = tween(650, delayMillis = 520, easing = FastOutSlowInEasing),
        label = "taglineAlpha"
    )

    val infinite = rememberInfiniteTransition(label = "bubbles")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    LaunchedEffect(Unit) {
        playIntro = true
        delay(2600)
        val dest = resolvePostSplashRoute(token)
        navController.navigate(dest) {
            popUpTo("splash") { inclusive = true }
            launchSingleTop = true
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BolsaTokens.headerGradientVertical)
    ) {
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        Canvas(Modifier.fillMaxSize()) {
            val p = phase
            for (i in 0..5) {
                val cx = w * (0.12f + 0.18f * i) + sin(p * 6.28f + i) * 24f
                val cy = h * (0.25f + (i % 3) * 0.22f) + sin(p * 4f + i * 0.7f) * 40f
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f + 0.03f * i),
                    radius = 36.dp.toPx() + i * 10f,
                    center = Offset(cx, cy)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(y = logoLift.dp)
                    .scale(logoScale)
                    .background(Color.White.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.WorkOutline,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(Modifier.height(32.dp))
            Text(
                text = "UNIEMPLEO",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 36.sp,
                    letterSpacing = 1.2.sp
                ),
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(brandAlpha)
                    .offset(y = brandOffset.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Empleo y prácticas para tu futuro profesional",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.92f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier
                    .alpha(taglineAlpha)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}
