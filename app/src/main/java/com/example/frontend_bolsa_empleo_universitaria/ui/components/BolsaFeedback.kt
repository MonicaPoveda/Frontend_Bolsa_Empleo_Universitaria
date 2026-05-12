package com.example.frontend_bolsa_empleo_universitaria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens

enum class BolsaFeedbackKind { Success, Error, Warning, Info }

private fun parseBolsaSnackbarMessage(raw: String): Pair<BolsaFeedbackKind, String> {
    val t = raw.trim()
    return when {
        t.startsWith("[success]", ignoreCase = true) -> BolsaFeedbackKind.Success to t.removePrefix("[success]").removePrefix("[SUCCESS]").trim()
        t.startsWith("[error]", ignoreCase = true) -> BolsaFeedbackKind.Error to t.removePrefix("[error]").removePrefix("[ERROR]").trim()
        t.startsWith("[warn]", ignoreCase = true) -> BolsaFeedbackKind.Warning to t.removePrefix("[warn]").removePrefix("[WARN]").trim()
        else -> BolsaFeedbackKind.Info to t
    }
}

@Composable
fun BolsaSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) { data: SnackbarData ->
        val visuals = data.visuals
        val (kind, clean) = parseBolsaSnackbarMessage(visuals.message)
        val (container, iconVec) = when (kind) {
            BolsaFeedbackKind.Success -> BolsaTokens.Palette.Success to Icons.Default.CheckCircle
            BolsaFeedbackKind.Error -> BolsaTokens.Palette.Error to Icons.Default.ErrorOutline
            BolsaFeedbackKind.Warning -> BolsaTokens.Palette.Warning to Icons.Default.WarningAmber
            BolsaFeedbackKind.Info -> BolsaTokens.Palette.Primary to Icons.Default.Info
        }
        Snackbar(
            modifier = Modifier.padding(vertical = 4.dp),
            shape = RoundedCornerShape(BolsaTokens.Dimens.fieldRadius),
            containerColor = container,
            contentColor = Color.White,
            actionOnNewLine = false,
            action = {
                visuals.actionLabel?.let { label ->
                    TextButton(onClick = { data.performAction() }) {
                        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(iconVec, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.White)
                Text(
                    text = clean,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
        }
    }
}

suspend fun SnackbarHostState.showBolsaSuccess(message: String) =
    showSnackbar("[success] $message")

suspend fun SnackbarHostState.showBolsaError(message: String) =
    showSnackbar("[error] $message")

suspend fun SnackbarHostState.showBolsaWarning(message: String) =
    showSnackbar("[warn] $message")

@Composable
fun BolsaModernDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    icon: ImageVector,
    iconTint: Color = BolsaTokens.Palette.Primary,
    iconBackground: Color = BolsaTokens.Palette.PrimaryLight,
    confirmText: String = "Aceptar",
    onConfirm: () -> Unit,
    dismissText: String? = null,
    onDismiss: (() -> Unit)? = null,
    confirmColor: Color = BolsaTokens.Palette.Primary
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(24.dp),
        containerColor = BolsaTokens.Palette.Surface,
        tonalElevation = 4.dp,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(iconBackground, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
            }
        },
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BolsaTokens.Palette.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium,
                color = BolsaTokens.Palette.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onConfirm,
                    shape = RoundedCornerShape(BolsaTokens.Dimens.buttonRadius),
                    colors = ButtonDefaults.buttonColors(containerColor = confirmColor, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(confirmText, fontWeight = FontWeight.SemiBold)
                }
                if (dismissText != null && onDismiss != null) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(BolsaTokens.Dimens.buttonRadius),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(dismissText, color = BolsaTokens.Palette.TextSecondary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        dismissButton = { }
    )
}
