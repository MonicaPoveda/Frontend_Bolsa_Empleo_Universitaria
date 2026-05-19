package com.example.frontend_bolsa_empleo_universitaria.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens

@Composable
fun BolsaPrimarySearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar ofertas o palabras clave...",
    onSearch: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = BolsaTokens.Palette.TextSecondary)
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = BolsaTokens.Palette.Primary,
                modifier = Modifier.size(BolsaTokens.Dimens.iconMd)
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar búsqueda", tint = BolsaTokens.Palette.TextSecondary)
                }
            }
        },
        shape = RoundedCornerShape(BolsaTokens.Dimens.fieldRadius),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BolsaTokens.Palette.Primary,
            unfocusedBorderColor = BolsaTokens.Palette.Divider,
            focusedContainerColor = BolsaTokens.Palette.Surface,
            unfocusedContainerColor = BolsaTokens.Palette.Surface,
            cursorColor = BolsaTokens.Palette.Primary
        ),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = BolsaTokens.Palette.TextPrimary)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BolsaFilterToggleRow(
    expanded: Boolean,
    onToggle: () -> Unit,
    activeFilterCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
        onClick = onToggle,
        shape = RoundedCornerShape(BolsaTokens.Dimens.fieldRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (expanded) BolsaTokens.Palette.PrimaryLight else BolsaTokens.Palette.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Tune,
                    contentDescription = null,
                    tint = BolsaTokens.Palette.Primary,
                    modifier = Modifier.size(BolsaTokens.Dimens.iconMd)
                )
                Spacer(Modifier.size(12.dp))
                Column {
                    Text(
                        "Filtros avanzados",
                        style = MaterialTheme.typography.titleSmall,
                        color = BolsaTokens.Palette.TextPrimary
                    )
                    Text(
                        if (activeFilterCount == 0) "Combina empresa, cargo, carrera y más"
                        else "$activeFilterCount filtros activos",
                        style = MaterialTheme.typography.bodySmall,
                        color = BolsaTokens.Palette.TextSecondary
                    )
                }
            }
            Icon(
                Icons.Default.FilterList,
                contentDescription = if (expanded) "Ocultar filtros" else "Mostrar filtros",
                tint = BolsaTokens.Palette.Primary
            )
        }
    }
}

@Composable
fun BolsaFilterTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        placeholder = {
            if (placeholder.isNotEmpty()) {
                Text(placeholder, style = MaterialTheme.typography.bodySmall, color = BolsaTokens.Palette.TextSecondary)
            }
        },
        supportingText = supportingText,
        isError = isError,
        singleLine = true,
        shape = RoundedCornerShape(BolsaTokens.Dimens.fieldRadius),
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
        ),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = BolsaTokens.Palette.TextPrimary)
    )
}

@Composable
fun BolsaEmptySearchState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Search
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(BolsaTokens.Palette.PrimaryLight, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = BolsaTokens.Palette.Primary
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = BolsaTokens.Palette.TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = BolsaTokens.Palette.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
