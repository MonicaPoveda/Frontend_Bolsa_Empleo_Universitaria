package com.example.frontend_bolsa_empleo_universitaria.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.frontend_bolsa_empleo_universitaria.ViewModel.LoginState
import com.example.frontend_bolsa_empleo_universitaria.ViewModel.LoginViewModel

import com.example.frontend_bolsa_empleo_universitaria.Repository.UsuarioRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.Frontend_Bolsa_Empleo_UniversitariaTheme


@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val uiState = viewModel.uiState

    val primaryBlue = Color(0xFF001F3F) // Azul institucional profundo

    var showForgotPassword by remember { mutableStateOf(false) }

    if (showForgotPassword) {
        ForgotPasswordDialog(viewModel = viewModel, onDismiss = { showForgotPassword = false })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Logo y Título
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.School,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = primaryBlue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Portal de Empleo",
                style = MaterialTheme.typography.titleLarge,
                color = primaryBlue,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Bienvenido de nuevo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Por favor, inicia sesión para acceder a tu panel.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Selector de tipo (Visual únicamente según diseño solicitado)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Estudiante / Egresado", fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campos de entrada
        Text(
            "Correo Institucional",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("usuario@universidad.edu.co") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            leadingIcon = { Icon(Icons.Default.Email, null, tint = Color.LightGray) },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Contraseña", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                "¿Olvidaste tu contraseña?",
                style = MaterialTheme.typography.labelMedium,
                color = primaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { showForgotPassword = true },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("••••••••") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.LightGray) },
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { viewModel.login(email, password) })
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
            enabled = uiState !is LoginState.Loading
        ) {
            if (uiState is LoginState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Iniciar Sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                }
            }
        }

        if (uiState is LoginState.Error) {
            Text(
                text = uiState.mensaje,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
            Text(" O CONTINÚA CON ", modifier = Modifier.padding(horizontal = 8.dp), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Google (Visual - Opcional)
        OutlinedButton(
            onClick = { /* TODO: Implementar Google Sign-In */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icono simple para representar Google
                Text("G ", fontWeight = FontWeight.ExtraBold, color = primaryBlue, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Continuar con Google", color = Color.Black, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.padding(bottom = 32.dp)) {
            Text("¿No tienes una cuenta? ", color = Color.Gray)
            Text(
                "Activa tu perfil",
                color = primaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }

        if (uiState is LoginState.Error) {
            LaunchedEffect(uiState) {
                // Snackbar o aviso de error
            }
        }

        LaunchedEffect(uiState) {
            if (uiState is LoginState.Success) onLoginSuccess()
        }
    }
}

@Composable
fun ForgotPasswordDialog(viewModel: LoginViewModel, onDismiss: () -> Unit) {
    var email by remember { mutableStateOf("") }
    val primaryBlue = Color(0xFF001F3F)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recuperar Contraseña", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Ingresa tu correo institucional y te enviaremos instrucciones para restablecer tu contraseña.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Institucional") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.recuperarPassword(email); onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Enviar Instrucciones")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    Frontend_Bolsa_Empleo_UniversitariaTheme {
        LoginScreen(
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = com.example.frontend_bolsa_empleo_universitaria.ViewModel.LoginViewModelFactory(
                    com.example.frontend_bolsa_empleo_universitaria.Repository.UsuarioRepository()
                )
            ),
            onLoginSuccess = {},
            onNavigateToRegister = {}
        )
    }
}
