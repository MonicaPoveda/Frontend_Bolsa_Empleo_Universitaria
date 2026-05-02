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

import com.example.frontend_bolsa_empleo_universitaria.repository.PerfilRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.UsuarioRepository

import com.example.frontend_bolsa_empleo_universitaria.screens.BusquedaScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.DetalleOfertaScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.HomeScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.LoginScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.NotificacionScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.RegistroScreen

import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginState
import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.RegistroViewModel

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

                val loginViewModel = remember {
                    LoginViewModel(usuarioRepository)
                }

                val registroViewModel = remember {
                    RegistroViewModel(usuarioRepository, perfilRepository)
                }

                val ofertasViewModel: OfertasViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {

                    composable("login") {

                        LoginScreen(
                            viewModel = loginViewModel,

                            onLoginSuccess = {

                                val state = loginViewModel.uiState

                                val nombre =
                                    if (state is LoginState.Success)
                                        state.usuario.nombre
                                    else
                                        "Usuario"

                                navController.navigate("busqueda?nombre=$nombre") {
                                    popUpTo("login") {
                                        inclusive = true
                                    }
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

                            onRegistroSuccess = { usuario ->

                                loginViewModel.setSuccessState(usuario)

                                navController.navigate("home") {

                                    popUpTo("login") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable("home") {

                        val state = loginViewModel.uiState

                        val usuario =
                            if (state is LoginState.Success)
                                state.usuario
                            else
                                null

                        HomeScreen(

                            usuario = usuario,

                            onLogout = {

                                loginViewModel.resetState()

                                navController.navigate("login") {

                                    popUpTo("home") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable(
                        route = "busqueda?nombre={nombre}",

                        arguments = listOf(
                            navArgument("nombre") {
                                type = NavType.StringType
                                defaultValue = "Usuario"
                            }
                        )

                    ) { backStackEntry ->

                        val nombre =
                            backStackEntry.arguments
                                ?.getString("nombre")
                                ?: "Usuario"

                        BusquedaScreen(

                            viewModel = ofertasViewModel,

                            nombreUsuario = nombre,

                            onNotificationClick = {
                                navController.navigate("notifications")
                            },

                            onVerDetalle = { id: Long ->
                                navController.navigate("detalle/$id")
                            },

                            onNavigateToProfile = {
                                navController.navigate("home")
                            },

                            onNavigateToPostulations = {

                            }
                        )
                    }

                    composable("notifications") {

                        NotificacionScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(
                        route = "detalle/{ofertaId}",

                        arguments = listOf(
                            navArgument("ofertaId") {
                                type = NavType.LongType
                            }
                        )

                    ) { backStackEntry ->

                        val ofertaId =
                            backStackEntry.arguments
                                ?.getLong("ofertaId")
                                ?: 0L

                        val oferta =
                            ofertasViewModel.ofertas.value.find {
                                it.idOferta == ofertaId
                            }

                        if (oferta != null) {

                            DetalleOfertaScreen(

                                oferta = oferta,

                                empresaNombre = "Tech Solutions S.A.",

                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}