package com.example.frontend_bolsa_empleo_universitaria.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
import kotlinx.coroutines.delay

enum class AdminMessageType {
    SUCCESS, ERROR, INFO, WARNING
}

data class AdminMessageState(
    val message: String = "",
    val type: AdminMessageType = AdminMessageType.INFO,
    val visible: Boolean = false
)

@Composable
fun AdminMessageBanner(
    state: AdminMessageState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state.visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val backgroundColor = when (state.type) {
            AdminMessageType.SUCCESS -> BolsaTokens.Palette.Success
            AdminMessageType.ERROR -> BolsaTokens.Palette.Error
            AdminMessageType.WARNING -> BolsaTokens.Palette.Warning
            AdminMessageType.INFO -> BolsaTokens.Palette.Info
        }

        val icon = when (state.type) {
            AdminMessageType.SUCCESS -> Icons.Default.CheckCircle
            AdminMessageType.ERROR -> Icons.Default.Error
            AdminMessageType.WARNING -> Icons.Default.Warning
            AdminMessageType.INFO -> Icons.Default.Info
        }

        Surface(
            color = backgroundColor,
            contentColor = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Auto-dismiss logic
        LaunchedEffect(state.visible) {
            if (state.visible) {
                delay(5000)
                onDismiss()
            }
        }
    }
}
