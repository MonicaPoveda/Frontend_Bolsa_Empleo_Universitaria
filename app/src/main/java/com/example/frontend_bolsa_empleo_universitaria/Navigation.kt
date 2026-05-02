package com.example.frontend_bolsa_empleo_universitaria

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.frontend_bolsa_empleo_universitaria.Screens.DetalleOfertaScreen
import com.example.frontend_bolsa_empleo_universitaria.Screens.HomeScreen
import com.example.frontend_bolsa_empleo_universitaria.Screens.NotificacionScreen
import com.example.frontend_bolsa_empleo_universitaria.ViewModel.OfertasViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Notifications : Screen("notifications")
    object DetalleOferta : Screen("detalle/{ofertaId}") {
        fun createRoute(ofertaId: Long) = "detalle/$ofertaId"
    }
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val ofertasViewModel: OfertasViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                ofertaViewModel = ofertasViewModel,
                onNotificationClick = {
                    navController.navigate(Screen.Notifications.route)
                },
                onOfertaClick = { oferta ->
                    navController.navigate(Screen.DetalleOferta.createRoute(oferta.idOferta))
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
                    empresaNombre = "Tech Solutions S.A.", // Aquí podrías buscar el nombre real si lo tienes en un mapa
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
