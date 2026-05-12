package com.example.frontend_bolsa_empleo_universitaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.repository.AuthRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.AdminRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.example.frontend_bolsa_empleo_universitaria.screens.Administrador.*
import com.example.frontend_bolsa_empleo_universitaria.screens.Empresa.*
import com.example.frontend_bolsa_empleo_universitaria.screens.Estudiante.*
import com.example.frontend_bolsa_empleo_universitaria.screens.Login.LoginScreen
import com.example.frontend_bolsa_empleo_universitaria.screens.Login.SplashScreen
import com.example.frontend_bolsa_empleo_universitaria.ui.theme.BolsaTokens
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
private fun AdminPlaceholderScreen(titulo: String, descripcion: String) {
    Box(
        Modifier
            .fillMaxSize()
            .background(BolsaTokens.Palette.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Outlined.Construction,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = BolsaTokens.Palette.Primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineSmall,
                color = BolsaTokens.Palette.TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = BolsaTokens.Palette.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val adminViewModel: AdminViewModel = viewModel(
        factory = AdminViewModelFactory(context)
    )
    val adminRepo = remember { AdminRepository(context) }
    val authRepo = remember { AuthRepository(RetrofitClient.usuarioApi) }
    val empresaRepo = remember { EmpresaRepository(RetrofitClient.empresaApi) }
    val token = remember { Token(context) }

    NavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = {
            fadeIn(animationSpec = tween(280)) + slideInHorizontally(animationSpec = tween(320)) { it / 26 }
        },
        exitTransition = {
            fadeOut(animationSpec = tween(220))
        },
        popEnterTransition = {
            fadeIn(tween(280)) + slideInHorizontally(tween(320)) { -it / 26 }
        },
        popExitTransition = {
            fadeOut(tween(220)) + slideOutHorizontally(tween(280)) { it / 26 }
        }
    ) {
        composable("splash") { SplashScreen(navController = navController) }
        composable("login") { LoginScreen(navController = navController) }
        composable("estudiante_home") { EstudianteHomeScreen(navController = navController) }
        composable("empresa_home") { EmpresaHomeScreen(navController = navController) }

        composable("admin_home") {
            AdministradorHomeScreen(navController = navController, adminViewModel = adminViewModel)
        }

        composable("registro_estudiante") { RegistroEstudianteScreen(navController = navController) }
        composable("registro_empresa") { RegistroEmpresaScreen(navController = navController) }
        composable("editar_perfil_empresa") { EditarPerfilEmpresaScreen(navController = navController) }
        composable("mensaje_alerta_crear_perfil") { MensajeAlertaCrearPerfilScreen(navController) }
        composable("crear_perfil_estudiante") { CrearPerfilEstudianteScreen(navController) }
        composable("crear_perfil_egresado") { CrearPerfilEgresadoScreen(navController) }

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

        class FakePerfilEmpresaAdminScreen // Placeholder for potentially missing imports or symbols if any, though imports seem fine

        composable("perfil_empresa_admin/{idEmpresa}") { backStackEntry: NavBackStackEntry ->
            val id = backStackEntry.arguments?.getString("idEmpresa")?.toLongOrNull() ?: 0L
            PerfilEmpresaAdminScreen(idEmpresa = id, navController = navController, viewModel = adminViewModel)
        }

        composable("ofertas_por_empresa/{idEmpresa}/{nombreEmpresa}") { backStackEntry: NavBackStackEntry ->
            val id = backStackEntry.arguments?.getString("idEmpresa")?.toLongOrNull() ?: 0L
            val nombre = backStackEntry.arguments?.getString("nombreEmpresa") ?: "Empresa"
            OfertasEmpresaAdminScreen(idEmpresa = id, nombreEmpresa = nombre, navController = navController, viewModel = adminViewModel)
        }

        composable("admin_ofertas") {
            AdminPlaceholderScreen(
                titulo = "Gestión global de ofertas",
                descripcion = "Estamos preparando esta sección. Mientras tanto, usa el listado de empresas y ofertas desde el panel principal."
            )
        }
        composable("admin_usuarios") {
            AdminPlaceholderScreen(
                titulo = "Gestión de usuarios",
                descripcion = "Próximamente podrás administrar cuentas desde aquí con la misma línea visual del resto de la app."
            )
        }
        composable("admin_reportes") {
            AdminPlaceholderScreen(
                titulo = "Reportes y estadísticas",
                descripcion = "Esta área mostrará indicadores clave cuando el backend esté disponible."
            )
        }

        composable("detalle_oferta/{ofertaId}") { backStackEntry: NavBackStackEntry ->
            val id = backStackEntry.arguments?.getString("ofertaId")?.toLongOrNull() ?: 0L
            val empresaEntry = remember(backStackEntry) { navController.getBackStackEntry("empresa_home") }
            val viewModel: OfertasViewModel = viewModel(
                viewModelStoreOwner = empresaEntry,
                factory = OfertasViewModelFactory(
                    OfertasRepository(RetrofitClient.ofertaLaboralApi),
                    EmpresaRepository(RetrofitClient.empresaApi)
                )
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

        composable("perfil_estudiante_empresa/{idUsuario}") { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toLongOrNull() ?: 0L
            PerfilEstudianteEmpresaScreen(
                idUsuario = idUsuario,
                navController = navController
            )
        }
        composable("mis_postulaciones") { MisPostulacionesScreen(navController = navController) }
        composable("mi_perfil") { MiPerfilScreen(navController = navController) }
        composable("configuracion_cuenta") { ConfiguracionCuentaScreen(navController = navController) }
    }
}
