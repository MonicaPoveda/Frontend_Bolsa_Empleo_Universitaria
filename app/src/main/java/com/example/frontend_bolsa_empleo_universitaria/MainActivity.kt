package com.example.frontend_bolsa_empleo_universitaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend_bolsa_empleo_universitaria.screens.Administrador.AdministradorHomeScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Empresa.EmpresaHomeScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante.EstudianteHomeScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.login.LoginScreen
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.Frontend_Bolsa_Empleo_UniversitariaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Frontend_Bolsa_Empleo_UniversitariaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("estudiante_home") {
            EstudianteHomeScreen(navController = navController)
        }
        composable("empresa_home") {
            EmpresaHomeScreen(navController = navController)
        }
        composable("admin_home") {
            AdministradorHomeScreen(navController = navController)
        }

        composable("notificaciones") {
            Text(
                "Pantalla de Notificaciones",
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        }
        composable("configuracion_cuenta") {
            Text(
                "Pantalla de Configuración de Cuenta",
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        }

        composable("acerca_de") {
            Text(
                "Pantalla Acerca de",
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        }
        composable("mis_postulaciones") {
            Text(
                "Pantalla de Mis Postulaciones",
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        }
        composable("detalle_oferta/{ofertaId}") { backStackEntry ->
            val ofertaId = backStackEntry.arguments?.getString("ofertaId")
            Text(
                "Detalle de oferta ID: $ofertaId",
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        }
    }
}