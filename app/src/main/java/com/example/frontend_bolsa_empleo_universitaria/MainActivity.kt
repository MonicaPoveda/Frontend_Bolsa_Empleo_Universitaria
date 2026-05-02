package com.example.frontend_bolsa_empleo_universitaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.frontend_bolsa_empleo_universitaria.Repository.PerfilRepository
import com.example.frontend_bolsa_empleo_universitaria.Repository.UsuarioRepository
import com.example.frontend_bolsa_empleo_universitaria.Screens.BusquedaScreen
import com.example.frontend_bolsa_empleo_universitaria.Screens.DetalleOfertaScreen
import com.example.frontend_bolsa_empleo_universitaria.Screens.HomeScreen
import com.example.frontend_bolsa_empleo_universitaria.Screens.LoginScreen
import com.example.frontend_bolsa_empleo_universitaria.Screens.NotificacionScreen
import com.example.frontend_bolsa_empleo_universitaria.Screens.RegistroScreen
import com.example.frontend_bolsa_empleo_universitaria.ViewModel.LoginState
import com.example.frontend_bolsa_empleo_universitaria.ViewModel.LoginViewModel
import com.example.frontend_bolsa_empleo_universitaria.ViewModel.LoginViewModelFactory
import com.example.frontend_bolsa_empleo_universitaria.ViewModel.OfertasViewModel
import com.example.frontend_bolsa_empleo_universitaria.ViewModel.RegistroState
import com.example.frontend_bolsa_empleo_universitaria.ViewModel.RegistroViewModel
import com.example.frontend_bolsa_empleo_universitaria.ViewModel.RegistroViewModelFactory
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.Frontend_Bolsa_Empleo_UniversitariaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Frontend_Bolsa_Empleo_UniversitariaTheme {
                val navController = rememberNavController()
                val usuarioRepository = remember { UsuarioRepository() }
                val perfilRepository = remember { PerfilRepository() }
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(usuarioRepository)
                )
                val registroViewModel: RegistroViewModel = viewModel(
                    factory = RegistroViewModelFactory(usuarioRepository, perfilRepository)
                )
                val ofertasViewModel: OfertasViewModel = viewModel()

                NavHost(navController = navController, startDestination = "login") {

                    composable("login") {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                val state = loginViewModel.uiState
                                val nombre = if (state is LoginState.Success) state.usuario.nombre else "Usuario"
                                navController.navigate("busqueda?nombre=$nombre") {
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
                                val state = registroViewModel.uiState
                                val nombre = if (state is RegistroState.Success) state.usuario.nombre else "Usuario"
                                navController.navigate("busqueda?nombre=$nombre") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(
                        route = "busqueda?nombre={nombre}",
                        arguments = listOf(navArgument("nombre") {
                            type = NavType.StringType
                            defaultValue = "Usuario"
                        })
                    ) { backStackEntry ->
                        val nombre = backStackEntry.arguments?.getString("nombre") ?: "Usuario"
                        BusquedaScreen(
                            viewModel = ofertasViewModel,
                            nombreUsuario = nombre,
                            onNotificationClick = { navController.navigate("notifications") },
                            onVerDetalle = { id: Long -> navController.navigate("detalle/$id") },
                            onNavigateToProfile = { navController.navigate("home") },
                            onNavigateToPostulations = { /* Pendiente */ }
                        )
                    }

                    composable("home") {
                        val state = loginViewModel.uiState
                        val usuario = if (state is LoginState.Success) state.usuario else null
                        HomeScreen(
                            usuario = usuario,
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo(0)
                                }
                            }
                        )
                    }

                    composable("notifications") {
                        NotificacionScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = "detalle/{ofertaId}",
                        arguments = listOf(navArgument("ofertaId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val ofertaId = backStackEntry.arguments?.getLong("ofertaId") ?: 0L
                        val oferta = ofertasViewModel.ofertas.value.find { it.idOferta == ofertaId }
                        if (oferta != null) {
                            DetalleOfertaScreen(
                                oferta = oferta,
                                empresaNombre = "Tech Solutions S.A.",
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}