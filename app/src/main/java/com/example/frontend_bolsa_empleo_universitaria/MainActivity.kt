package com.example.frontend_bolsa_empleo_universitaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend_bolsa_empleo_universitaria.screens.Administrador.AdministradorHomeScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Empresa.EmpresaHomeScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Empresa.RegistroEmpresaScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante.EstudianteHomeScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante.RegistroEstudianteScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Login.LoginScreen
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.Frontend_Bolsa_Empleo_UniversitariaTheme
import com.example.frontend_bolsa_empleo_universitaria.utils.Token

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar Retrofit con el contexto de la aplicación
        RetrofitClient.init(this)

        enableEdgeToEdge()
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
    val context = LocalContext.current
    val token = remember { Token(context) }
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (token.isLoggedIn()) {
            when (token.getUserRole()) {
                "ESTUDIANTE" -> "estudiante_home"
                "EMPRESA" -> "empresa_home"
                "ADMIN" -> "admin_home"
                else -> "login"
            }
        } else "login"
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
        composable("registro_estudiante") {
            RegistroEstudianteScreen(navController = navController)
        }
        composable("registro_empresa") {
            RegistroEmpresaScreen(navController = navController)
        }
    }
}