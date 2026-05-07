package com.example.frontend_bolsa_empleo_universitaria

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontend_bolsa_empleo_universitaria.screens.LoginScreen
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.Frontend_Bolsa_Empleo_UniversitariaTheme
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: Token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = Token(this)

        setContent {
            Frontend_Bolsa_Empleo_UniversitariaTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation(tokenManager)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(tokenManager: Token) {
    var isAuthenticated by remember { mutableStateOf(false) }
    var rol by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val onLogout: () -> Unit = {
        scope.launch {
            tokenManager.clear()
        }
    }

    LaunchedEffect(Unit) {
        tokenManager.getTokenFlow().collect { token ->
            val currentRol = tokenManager.getRolFlow().first()
            isAuthenticated = !token.isNullOrEmpty()
            rol = currentRol
            isLoading = false
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        isAuthenticated && rol != null -> {
            when (rol) {
                "ESTUDIANTE" -> EstudianteHomeScreen(onLogout)
                "EMPRESA" -> EmpresaHomeScreen(onLogout)
                "ADMIN" -> AdminHomeScreen(onLogout)
                else -> LoginScreen { _, _ -> }
            }
        }
        else -> LoginScreen { _, _ -> }
    }
}

@Composable
fun EstudianteHomeScreen(onLogout: () -> Unit) {
    HomeScreenTemplate(title = "Bienvenido Estudiante", onLogout = onLogout)
}

@Composable
fun EmpresaHomeScreen(onLogout: () -> Unit) {
    HomeScreenTemplate(title = "Bienvenido Empresa", onLogout = onLogout)
}

@Composable
fun AdminHomeScreen(onLogout: () -> Unit) {
    HomeScreenTemplate(title = "Bienvenido Administrador", onLogout = onLogout)
}

@Composable
fun HomeScreenTemplate(title: String, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLogout) {
            Text("Cerrar Sesión")
        }
    }
}