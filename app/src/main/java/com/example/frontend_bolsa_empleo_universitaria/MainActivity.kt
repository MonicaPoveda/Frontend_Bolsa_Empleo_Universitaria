package com.example.frontend_bolsa_empleo_universitaria

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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
import com.example.frontend_bolsa_empleo_universitaria.screens.PostulacionesScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.DetalleEmpresaScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.LoginScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.NotificacionScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.PerfilScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.RegistroScreen

import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginState
import com.example.frontend_bolsa_empleo_universitaria.viewModel.LoginViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.RegistroViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PostulacionesViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.NotificacionViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.PerfilViewModel

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
                
                val loginViewModel = remember { LoginViewModel(usuarioRepository) }
                val registroViewModel = remember { RegistroViewModel(usuarioRepository, perfilRepository) }
                val ofertasViewModel: OfertasViewModel = viewModel()
                val perfilViewModel: PerfilViewModel = viewModel()
                val postulacionesViewModel: PostulacionesViewModel = viewModel()
                val notificacionViewModel: NotificacionViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                val state = loginViewModel.uiState
                                val usuario = (state as? LoginState.Success)?.usuario
                                navController.navigate("busqueda?nombre=${usuario?.nombre ?: "Usuario"}") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToRegister = { navController.navigate("registro") }
                        )
                    }

                    composable("registro") {
                        RegistroScreen(
                            viewModel = registroViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onRegistroSuccess = { usuario ->
                                loginViewModel.setSuccessState(usuario)
                                navController.navigate("busqueda?nombre=${usuario.nombre ?: "Usuario"}") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("postulaciones") {
                        val state = loginViewModel.uiState
                        val usuarioId = (state as? LoginState.Success)?.usuario?.idUsuario ?: 0L
                        PostulacionesScreen(
                            idUsuario = usuarioId,
                            viewModel = postulacionesViewModel,
                            onBack = { 
                                navController.navigate("busqueda") {
                                    popUpTo("busqueda") { inclusive = true }
                                }
                            },
                            onEmpresaClick = { idEmp -> navController.navigate("detalle_empresa/$idEmp") },
                            onNavigateToProfile = { navController.navigate("perfil") }
                        )
                    }

                    composable(
                        route = "detalle_empresa/{empresaId}",
                        arguments = listOf(navArgument("empresaId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getLong("empresaId") ?: 0L
                        DetalleEmpresaScreen(
                            idEmpresa = id,
                            viewModel = ofertasViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = "busqueda?nombre={nombre}",
                        arguments = listOf(navArgument("nombre") { type = NavType.StringType; defaultValue = "Usuario" })
                    ) { backStackEntry ->
                        val nombre = backStackEntry.arguments?.getString("nombre") ?: "Usuario"
                        BusquedaScreen(
                            viewModel = ofertasViewModel,
                            nombreUsuario = nombre,
                            onNotificationClick = { navController.navigate("notifications") },
                            onVerDetalle = { id -> navController.navigate("detalle/$id") },
                            onNavigateToProfile = { navController.navigate("perfil") },
                            onNavigateToPostulations = { navController.navigate("postulaciones") }
                        )
                    }

                    composable("notifications") {
                        val state = loginViewModel.uiState
                        val usuarioId = (state as? LoginState.Success)?.usuario?.idUsuario ?: 0L
                        NotificacionScreen(
                            idUsuario = usuarioId,
                            viewModel = notificacionViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = "detalle/{ofertaId}",
                        arguments = listOf(navArgument("ofertaId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val ofertaId = backStackEntry.arguments?.getLong("ofertaId") ?: 0L
                        val oferta = ofertasViewModel.ofertas.value.find { it.idOferta == ofertaId }
                        val state = loginViewModel.uiState
                        val usuario = (state as? LoginState.Success)?.usuario
                        val context = LocalContext.current

                        if (oferta != null) {
                            LaunchedEffect(oferta.idEmpresa) {
                                ofertasViewModel.cargarEmpresa(oferta.idEmpresa)
                            }
                            DetalleOfertaScreen(
                                oferta = oferta,
                                empresaNombre = ofertasViewModel.empresaNombre.value,
                                onBack = { navController.popBackStack() },
                                onEmpresaClick = { idEmp -> navController.navigate("detalle_empresa/$idEmp") },
                                onPostularseClick = {
                                    if (usuario != null) {
                                        postulacionesViewModel.aplicarAOferta(usuario, oferta) { success ->
                                            if (success) {
                                                Toast.makeText(context, "¡Postulación enviada!", Toast.LENGTH_SHORT).show()
                                                navController.navigate("postulaciones")
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }

                    composable("perfil") {
                        val state = loginViewModel.uiState
                        val usuario = (state as? LoginState.Success)?.usuario
                        if (usuario != null) {
                            PerfilScreen(
                                usuario = usuario,
                                viewModel = perfilViewModel,
                                onBack = { 
                                    navController.navigate("busqueda") {
                                        popUpTo("busqueda") { inclusive = true }
                                    }
                                },
                                onUsuarioActualizado = { loginViewModel.setSuccessState(it) },
                                onLogout = {
                                    loginViewModel.resetState()
                                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                                },
                                onNavigateToPostulations = { navController.navigate("postulaciones") }
                            )
                        }
                    }
                }
            }
        }
    }
}
