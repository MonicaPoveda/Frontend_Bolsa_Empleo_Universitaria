package com.example.frontend_bolsa_empleo_universitaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import com.example.frontend_bolsa_empleo_universitaria.screens.Administrador.*
import com.example.frontend_bolsa_empleo_universitaria.screens.Empresa.*
import com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante.*
import com.example.frontend_bolsa_empleo_universitaria.screens.Login.LoginScreen
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.Frontend_Bolsa_Empleo_UniversitariaTheme
import com.example.frontend_bolsa_empleo_universitaria.viewModel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Instancia compartida del ViewModel para el módulo Admin
    val adminViewModel: AdminViewModel = viewModel(
        factory = AdminViewModelFactory(context)
    )

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(navController = navController) }
        composable("estudiante_home") { EstudianteHomeScreen(navController = navController) }
        composable("empresa_home") { EmpresaHomeScreen(navController = navController) }
        
        // ✅ Pantalla principal admin
        composable("admin_home") { 
            AdministradorHomeScreen(navController = navController, adminViewModel = adminViewModel) 
        }
        
        composable("registro_estudiante") { RegistroEstudianteScreen(navController = navController) }
        composable("registro_empresa") { RegistroEmpresaScreen(navController = navController) }
        composable("editar_perfil_empresa") { EditarPerfilEmpresaScreen(navController = navController) }
        composable("mensaje_alerta_crear_perfil") { MensajeAlertaCrearPerfilScreen(navController) }
        composable("crear_perfil_estudiante") { CrearPerfilEstudianteScreen(navController) }
        composable("crear_perfil_egresado") { CrearPerfilEgresadoScreen(navController) }

        // ==================== RUTAS ADMINISTRADOR ====================
        composable("notificaciones") { 
            EmpresasPendientesScreen(navController = navController, viewModel = adminViewModel) 
        }
        
        composable("admin_pendientes") { 
            EmpresasPendientesScreen(navController = navController, viewModel = adminViewModel) 
        }
        
        composable("detalle_solicitud/{id}") { backStackEntry: NavBackStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
            DetalleSolicitudScreen(id = id, navController = navController, viewModel = adminViewModel)
        }

        composable("admin_empresas") { 
            EmpresasGestionScreen(navController = navController, viewModel = adminViewModel) 
        }

        composable("perfil_empresa_admin/{idEmpresa}") { backStackEntry: NavBackStackEntry ->
            val id = backStackEntry.arguments?.getString("idEmpresa")?.toLongOrNull() ?: 0L
            PerfilEmpresaAdminScreen(idEmpresa = id, navController = navController, viewModel = adminViewModel)
        }

        composable("ofertas_por_empresa/{idEmpresa}/{nombreEmpresa}") { backStackEntry: NavBackStackEntry ->
            val id = backStackEntry.arguments?.getString("idEmpresa")?.toLongOrNull() ?: 0L
            val nombre = backStackEntry.arguments?.getString("nombreEmpresa") ?: "Empresa"
            OfertasEmpresaAdminScreen(idEmpresa = id, nombreEmpresa = nombre, navController = navController, viewModel = adminViewModel)
        }

        // Nuevas rutas del menú lateral con placeholders
        composable("admin_ofertas") { 
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Gestión Global de Ofertas (Próximamente)")
            }
        }
        composable("admin_usuarios") { 
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Gestión de Usuarios (Próximamente)")
            }
        }
        composable("admin_reportes") { 
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Reportes y Estadísticas (Próximamente)")
            }
        }
        // =============================================================

        composable("detalle_oferta/{ofertaId}") { backStackEntry: NavBackStackEntry ->
            val id = backStackEntry.arguments?.getString("ofertaId")?.toLongOrNull() ?: 0L
            val empresaEntry = remember(backStackEntry) { navController.getBackStackEntry("empresa_home") }
            val viewModel: OfertasViewModel = viewModel(
                viewModelStoreOwner = empresaEntry,
                factory = OfertasViewModelFactory(OfertasRepository(RetrofitClient.ofertaLaboralApi))
            )
            DetalleOfertaEmpresaScreen(ofertaId = id, navController = navController, viewModel = viewModel)
        }

        composable("postulantes_oferta/{ofertaId}/{ofertaTitulo}") { backStackEntry: NavBackStackEntry ->
            val id = backStackEntry.arguments?.getString("ofertaId")?.toLongOrNull() ?: 0L
            val titulo = backStackEntry.arguments?.getString("ofertaTitulo") ?: "Oferta"
            PostulantesOfertaScreen(ofertaId = id, ofertaTitulo = titulo, navController = navController)
        }
        
        composable("detalle_oferta_estudiante/{ofertaId}") { backStackEntry: NavBackStackEntry ->
            val id = backStackEntry.arguments?.getString("ofertaId")?.toLongOrNull() ?: 0L
            DetalleOfertaEstudianteScreen(ofertaId = id, navController = navController)
        }

        composable("mis_postulaciones") { MisPostulacionesScreen(navController = navController) }
        composable("mi_perfil") { MiPerfilScreen(navController = navController) }
        composable("configuracion_cuenta") { ConfiguracionCuentaScreen(navController = navController) }
    }
}
