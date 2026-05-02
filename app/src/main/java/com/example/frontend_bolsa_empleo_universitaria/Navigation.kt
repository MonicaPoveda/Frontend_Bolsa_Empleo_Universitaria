package com.example.frontend_bolsa_empleo_universitaria

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.frontend_bolsa_empleo_universitaria.Screens.*
import com.example.frontend_bolsa_empleo_universitaria.ViewModel.*
import com.example.frontend_bolsa_empleo_universitaria.Repository.UsuarioRepository

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registro : Screen("registro")
    object Busqueda : Screen("busqueda?nombre={nombre}") {
        fun createRoute(nombre: String) = "busqueda?nombre=$nombre"
    }
    object Home : Screen("home")
    object Notifications : Screen("notifications")
    object DetalleOferta : Screen("detalle/{ofertaId}") {
        fun createRoute(ofertaId: Long) = "detalle/$ofertaId"
    }
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val ofertasViewModel: OfertasViewModel = viewModel()
    val usuarioRepository = remember { UsuarioRepository() }
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(usuarioRepository))
    val registroViewModel: RegistroViewModel = viewModel(factory = RegistroViewModelFactory(usuarioRepository))

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    val state = loginViewModel.uiState
                    val nombre = if (state is LoginState.Success) state.usuario.nombre else "Usuario"
                    navController.navigate(Screen.Busqueda.createRoute(nombre)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Registro.route)
                }
            )
        }
        composable(Screen.Registro.route) {
            RegistroScreen(
                viewModel = registroViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegistroSuccess = {
                    val state = registroViewModel.uiState
                    val nombre = if (state is RegistroState.Success) state.usuario.nombre else "Usuario"
                    navController.navigate(Screen.Busqueda.createRoute(nombre)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Busqueda.route,
            arguments = listOf(navArgument("nombre") { 
                type = NavType.StringType
                defaultValue = "Usuario"
            })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: "Usuario"
            BusquedaScreen(
                viewModel = ofertasViewModel,
                nombreUsuario = nombre,
                onNotificationClick = { navController.navigate(Screen.Notifications.route) },
                onVerDetalle = { id: Long -> navController.navigate(Screen.DetalleOferta.createRoute(id)) },
                onNavigateToProfile = { navController.navigate(Screen.Home.route) },
                onNavigateToPostulations = { /* Pendiente */ }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                usuario = null, 
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(Screen.Notifications.route) {
            NotificacionScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.DetalleOferta.route,
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
