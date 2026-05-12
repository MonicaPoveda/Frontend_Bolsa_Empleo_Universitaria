package com.example.frontend_bolsa_empleo_universitaria.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens

@Composable
fun BolsaOutlinedFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val multiline = maxLines > 1 || minLines > 1
    val effectiveSingleLine = singleLine && !multiline
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = effectiveSingleLine,
        minLines = minLines,
        maxLines = maxOf(minLines, maxLines),
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder, style = MaterialTheme.typography.bodySmall, color = BolsaTokens.Palette.TextSecondary) }
        } else null,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(BolsaTokens.Dimens.fieldRadius),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = BolsaTokens.Palette.TextPrimary),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BolsaTokens.Palette.Primary,
            unfocusedBorderColor = BolsaTokens.Palette.Divider,
            errorBorderColor = BolsaTokens.Palette.Error,
            focusedLabelColor = BolsaTokens.Palette.Primary,
            unfocusedLabelColor = BolsaTokens.Palette.TextSecondary,
            errorLabelColor = BolsaTokens.Palette.Error,
            focusedContainerColor = BolsaTokens.Palette.Surface,
            unfocusedContainerColor = BolsaTokens.Palette.Surface,
            errorContainerColor = BolsaTokens.Palette.Surface,
            cursorColor = BolsaTokens.Palette.Primary,
            errorSupportingTextColor = BolsaTokens.Palette.Error
        )
    )
}
