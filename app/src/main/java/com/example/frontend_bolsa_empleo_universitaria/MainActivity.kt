package com.example.frontend_bolsa_empleo_universitaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    }
}