package com.example.frontend_bolsa_empleo_universitaria.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend_bolsa_empleo_universitaria.repository.UsuarioRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.Frontend_Bolsa_Empleo_UniversitariaTheme
import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginState
import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginViewModel

private val AzulPrimario     = Color(0xFF1565C0)
private val AzulOscuro       = Color(0xFF0D47A1)
private val AzulClaro        = Color(0xFFE3F2FD)
private val TextoPrimario    = Color(0xFF1A1C1E)
private val TextoGris        = Color(0xFF292C31)
private val TextoPlaceholder = Color(0xB26B6C70)
private val CampoFondo       = Color(0xFFF1F4F9)
private val CampoBorde       = Color(0xFFC4C6D0)
private val ErrorColor       = Color(0xFFBA1A1A)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.resetState() }

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgot      by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState) {
        if (uiState is LoginState.Success) onLoginSuccess()
    }

    if (showForgot) {
        ForgotPasswordDialog(viewModel = viewModel, onDismiss = { showForgot = false })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AzulOscuro, AzulPrimario, Color.White),
                    startY = 0f,
                    endY = 1600f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Portal de Empleo", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Tu futuro profesional comienza aquí", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Accede a tu cuenta", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextoPrimario)
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    CampoTextoLogin(
                        value = email,
                        onValueChange = { email = it },
                        label = "Correo Electrónico",
                        placeholder = "ejemplo@correo.com/.edu.co",
                        leadingIcon = Icons.Default.AlternateEmail,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CampoTextoLogin(
                        value = password,
                        onValueChange = { password = it },
                        label = "Contraseña",
                        placeholder = "Ingresa tu contraseña",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onTogglePassword = { passwordVisible = !passwordVisible },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { viewModel.login(email, password) })
                    )

                    Text(
                        "¿Olvidaste tu contraseña?",
                        fontSize = 13.sp,
                        color = AzulPrimario,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.End).padding(top = 12.dp).clickable { showForgot = true }
                    )

                    AnimatedVisibility(visible = uiState is LoginState.Error) {
                        Surface(
                            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Error, null, tint = ErrorColor, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text((uiState as? LoginState.Error)?.mensaje ?: "Error", color = ErrorColor, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.login(email, password) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                        enabled = uiState !is LoginState.Loading && email.isNotBlank() && password.isNotBlank()
                    ) {
                        if (uiState is LoginState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Entrar al Portal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("¿No tienes cuenta? ", color = TextoGris, fontSize = 14.sp)
                        Text(
                            "Regístrate aquí",
                            color = AzulPrimario,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CampoTextoLogin(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoGris, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextoPlaceholder, fontSize = 14.sp) },
            leadingIcon = { Icon(leadingIcon, null, tint = AzulPrimario, modifier = Modifier.size(20.dp)) },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onTogglePassword) {
                        Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = TextoGris, modifier = Modifier.size(20.dp))
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AzulPrimario,
                unfocusedBorderColor = CampoBorde,
                focusedContainerColor = AzulClaro.copy(alpha = 0.2f),
                unfocusedContainerColor = CampoFondo
            )
        )
    }
}

@Composable
fun ForgotPasswordDialog(viewModel: LoginViewModel, onDismiss: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var nuevaPass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var step by remember { mutableIntStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(if (step == 1) Icons.Default.MailOutline else Icons.Default.LockReset, null, tint = AzulPrimario, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(if (step == 1) "Recuperar Acceso" else "Nueva Contraseña", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        },
        text = {
            Column {
                Text(if (step == 1) "Ingresa tu correo institucional." else "Escribe tu nueva contraseña.", color = TextoGris, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(20.dp))
                CampoTextoLogin(
                    value = if (step == 1) email else nuevaPass,
                    onValueChange = { if (step == 1) email = it else nuevaPass = it },
                    label = if (step == 1) "Correo" else "Contraseña",
                    placeholder = if (step == 1) "usuario@universidad.edu.co" else "Mínimo 6 caracteres",
                    leadingIcon = if (step == 1) Icons.Default.AlternateEmail else Icons.Default.Lock,
                    isPassword = step == 2,
                    passwordVisible = passVisible,
                    onTogglePassword = { passVisible = !passVisible },
                    keyboardOptions = KeyboardOptions(keyboardType = if (step == 1) KeyboardType.Email else KeyboardType.Password)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (step == 1) { if(email.isNotBlank()) step = 2 }
                    else { if(nuevaPass.isNotBlank()) { viewModel.actualizarPassword(email, nuevaPass); onDismiss() } }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (step == 1) "Continuar" else "Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancelar", color = TextoGris) }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    Frontend_Bolsa_Empleo_UniversitariaTheme {
        val repository = UsuarioRepository()
        val viewModel  = LoginViewModel(repository)
        LoginScreen(viewModel = viewModel, onLoginSuccess = {}, onNavigateToRegister = {})
    }
}
