package com.example.frontend_bolsa_empleo_universitaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.Frontend_Bolsa_Empleo_UniversitariaTheme

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend_bolsa_empleo_universitaria.repository.UsuarioRepository
import com.example.frontend_bolsa_empleo_universitaria.screens.LoginScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.RegistroScreen
import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.RegistroViewModel

import com.example.frontend_bolsa_empleo_universitaria.screens.HomeScreen
import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val repository = UsuarioRepository()
        val loginViewModel = LoginViewModel(repository)
        val registroViewModel = RegistroViewModel(repository)

        setContent {
            Frontend_Bolsa_Empleo_UniversitariaTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate("registro")
                            }
                        )
                    }
                    composable("registro") {
                        RegistroScreen(
                            viewModel = registroViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onRegistroSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("home") {
                        val state = loginViewModel.uiState
                        val usuario = if (state is LoginState.Success) state.usuario else null
                        
                        HomeScreen(
                            usuario = usuario,
                            onLogout = {
                                loginViewModel.resetState()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Frontend_Bolsa_Empleo_UniversitariaTheme {
        Greeting("Android")
    }
}