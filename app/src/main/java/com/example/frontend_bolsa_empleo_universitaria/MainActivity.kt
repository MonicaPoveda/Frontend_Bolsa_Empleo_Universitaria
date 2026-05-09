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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import com.example.frontend_bolsa_empleo_universitaria.screens.Administrador.AdministradorHomeScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Empresa.DetalleOfertaEmpresaScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Empresa.EditarPerfilEmpresaScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Empresa.EmpresaHomeScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Empresa.PostulantesOfertaScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Empresa.RegistroEmpresaScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante.EstudianteHomeScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante.RegistroEstudianteScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Login.LoginScreen
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.Frontend_Bolsa_Empleo_UniversitariaTheme
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModel
import com.example.frontend_bolsa_empleo_universitaria.viewModel.OfertasViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        composable("registro_estudiante") {
            RegistroEstudianteScreen(navController = navController)
        }
        composable("registro_empresa") {
            RegistroEmpresaScreen(navController = navController)
        }
        composable("editar_perfil_empresa") {
            EditarPerfilEmpresaScreen(navController = navController)
        }

        // ✅ Ruta: detalle de oferta para empresa
        composable("detalle_oferta/{ofertaId}") { backStackEntry ->
            val ofertaId = backStackEntry.arguments
                ?.getString("ofertaId")
                ?.toLongOrNull() ?: 0L

            val empresaEntry = remember(backStackEntry) {
                navController.getBackStackEntry("empresa_home")
            }
            val viewModel: OfertasViewModel = viewModel(
                viewModelStoreOwner = empresaEntry,
                factory = OfertasViewModelFactory(
                    OfertasRepository(RetrofitClient.ofertaLaboralApi)
                )
            )

            DetalleOfertaEmpresaScreen(
                ofertaId = ofertaId,
                navController = navController,
                viewModel = viewModel
            )
        }

        // ✅ Ruta: postulantes de una oferta (CORREGIDA - fuera del composable anterior)
        composable("postulantes_oferta/{ofertaId}/{ofertaTitulo}") { backStackEntry ->
            val ofertaId = backStackEntry.arguments?.getString("ofertaId")?.toLongOrNull() ?: 0
            val ofertaTitulo = backStackEntry.arguments?.getString("ofertaTitulo") ?: "Oferta"
            PostulantesOfertaScreen(
                ofertaId = ofertaId,
                ofertaTitulo = ofertaTitulo,
                navController = navController
            )
        }
    }
}