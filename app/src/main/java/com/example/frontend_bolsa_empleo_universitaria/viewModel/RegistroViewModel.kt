package com.example.frontend_bolsa_empleo_universitaria.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.Usuario
import com.example.frontend_bolsa_empleo_universitaria.repository.UsuarioRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.frontend_bolsa_empleo_universitaria.model.Perfil
import com.example.frontend_bolsa_empleo_universitaria.repository.PerfilRepository

class RegistroViewModel(
    private val repoUsuario: UsuarioRepository,
    private val repoPerfil: PerfilRepository
) : ViewModel() {

    var uiState by mutableStateOf<RegistroState>(RegistroState.Idle)
        private set

    var pasoActual by mutableIntStateOf(1)
        private set

    // Estado para persistir datos entre pasos
    var datosUsuario by mutableStateOf<Usuario?>(null)

    // Datos Paso 1
    var nombre by mutableStateOf("")
    var identificacion by mutableStateOf("")
    var email by mutableStateOf("")
    var telefono by mutableStateOf("")
    var password by mutableStateOf("")
    var tipoUsuario by mutableStateOf("ESTUDIANTE")

    // Datos Paso 2
    var carrera by mutableStateOf("")
    var universidad by mutableStateOf("")
    var semestre by mutableStateOf("")
    var promedio by mutableStateOf("")
    val areasInteres = mutableStateListOf<String>()
    var cvUrl by mutableStateOf("")
    var disponibilidad by mutableStateOf("")
    var tieneExperiencia by mutableStateOf(false)
    val listaExperiencia = mutableStateListOf<ExperienciaLaboral>()

    // Listas de opciones
    val opcionesCarreras = listOf(
        "Ingeniería de Sistemas", "Ingeniería Industrial", "Administración de Empresas",
        "Psicología", "Derecho", "Contaduría Pública", "Comunicación Social", "Diseño Gráfico"
    )
    val opcionesSemestres = (1..10).map { "$it° Semestre" } + "Graduado"
    val opcionesDisponibilidad = listOf("Tiempo Completo", "Medio Tiempo", "Remoto", "Híbrido", "Prácticas")
    val sugerenciasHabilidades = listOf("Java", "Kotlin", "Python", "SQL", "React", "Node.js", "Scrum", "Inglés B2")

    fun registrarPaso1() {
        Log.d("RegistroViewModel", "Intentando Paso 1: $nombre, $email, $tipoUsuario")
        
        // Reset state to clear previous errors
        uiState = RegistroState.Idle

        if (nombre.isBlank()) {
            uiState = RegistroState.Error("El campo Nombre no puede estar vacío")
            return
        }

        val partesNombre = nombre.trim().split("\\s+".toRegex())
        if (partesNombre.size < 2) {
            uiState = RegistroState.Error("El campo Nombre debe incluir al menos un apellido (Nombre + Apellido)")
            return
        }

        if (identificacion.isBlank()) {
            uiState = RegistroState.Error("El campo Identificación es obligatorio")
            return
        }

        if (telefono.length < 7) {
            uiState = RegistroState.Error("El campo Teléfono debe tener al menos 7 dígitos")
            return
        }

        // Validación de formato de correo
        if (!email.contains("@") || !email.contains(".")) {
            uiState = RegistroState.Error("El campo Correo debe tener un formato válido (ejemplo@dominio.com)")
            return
        }

        // Validación de dominio según tipo de usuario
        if (tipoUsuario == "ESTUDIANTE" && !email.endsWith(".edu.co")) {
            uiState = RegistroState.Error("Para Estudiantes, el correo debe ser institucional y terminar en .edu.co")
            return
        }

        if (tipoUsuario == "EGRESADO" && email.endsWith(".edu.co")) {
            uiState = RegistroState.Error("Para Egresados, debe usar un correo personal. No se permite el dominio .edu.co")
            return
        }

        if (password.length < 8) {
            uiState = RegistroState.Error("La Contraseña debe tener al menos 8 caracteres para ser segura")
            return
        }

        val partes = nombre.trim().split("\\s+".toRegex(), limit = 2)
        val nombreUser = partes.getOrNull(0) ?: ""
        val apellidoUser = partes.getOrNull(1) ?: ""

        datosUsuario = Usuario(
            idUsuario = null,
            nombre = nombreUser,
            apellido = apellidoUser,
            identificacion = identificacion,
            email = email,
            telefono = telefono,
            tipoUsuario = tipoUsuario,
            fechaRegistro = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            estado = true,
            password = password
        )
        Log.d("RegistroViewModel", "Paso 1 exitoso, datosUsuario: $datosUsuario")
        pasoActual = 2
        uiState = RegistroState.Idle // Limpiar errores previos al pasar de nivel
    }

    fun finalizarRegistro() {
        val usuario = datosUsuario ?: return
        val esEstudiante = usuario.tipoUsuario == "ESTUDIANTE"

        // Limpiar error previo para evitar bloqueos visuales
        uiState = RegistroState.Idle

        if (carrera.isBlank()) {
            uiState = RegistroState.Error("El campo Carrera es obligatorio. Selecciona o escribe una.")
            return
        }

        if (universidad.isBlank()) {
            uiState = RegistroState.Error("El campo Universidad es obligatorio.")
            return
        }

        if (esEstudiante) {
            if (semestre.isBlank()) {
                uiState = RegistroState.Error("El campo Semestre es obligatorio para estudiantes.")
                return
            }
            if (promedio.isBlank()) {
                uiState = RegistroState.Error("El campo Promedio es obligatorio.")
                return
            }
            val promedioNum = promedio.replace(",", ".").toDoubleOrNull()
            if (promedioNum == null || promedioNum < 0.0 || promedioNum > 5.0) {
                uiState = RegistroState.Error("El campo Promedio debe ser un número válido entre 0.0 y 5.0")
                return
            }
        }

        if (disponibilidad.isBlank()) {
            uiState = RegistroState.Error("Debes seleccionar una opción en el campo Disponibilidad.")
            return
        }

        viewModelScope.launch {
            uiState = RegistroState.Loading
            
            // Formatear experiencia como String para el backend
            val experienciaStr = if (tieneExperiencia) {
                listaExperiencia.joinToString("\n") { 
                    "${it.empresa} - ${it.cargo} (${it.duracion})"
                }
            } else "Sin experiencia"

            // 1. Guardar Usuario
            val resultUsuario = repoUsuario.guardar(usuario)
            
            resultUsuario.fold(
                onSuccess = { usuarioGuardado ->
                    // 2. Guardar Perfil asociado al usuario
                    val nuevoPerfil = Perfil(
                        idPerfil = null,
                        carrera = carrera,
                        universidad = universidad,
                        semestre = if (esEstudiante) semestre else "Graduado",
                        habilidades = areasInteres.joinToString(", "),
                        promedio = if (esEstudiante) promedio.toDoubleOrNull() else null,
                        experiencia = experienciaStr,
                        cvUrl = cvUrl,
                        disponibilidad = disponibilidad,
                        idUsuario = usuarioGuardado.idUsuario
                    )
                    
                    Log.d("RegistroViewModel", "Intentando guardar perfil: $nuevoPerfil")
                    val resultPerfil = repoPerfil.guardar(nuevoPerfil)
                    resultPerfil.fold(
                        onSuccess = { 
                            Log.d("RegistroViewModel", "Perfil guardado exitosamente")
                            uiState = RegistroState.Success(usuarioGuardado)
                        },
                        onFailure = { 
                            Log.e("RegistroViewModel", "Error al guardar perfil", it)
                            uiState = RegistroState.Error("Usuario creado, pero hubo error en el perfil: ${it.message}") 
                        }
                    )
                },
                onFailure = { 
                    Log.e("RegistroViewModel", "Error al guardar usuario: ${it.message}")
                    uiState = RegistroState.Error(it.message ?: "Error al registrar usuario")
                }
            )
        }
    }

    fun volverAlPaso1() {
        pasoActual = 1
        uiState = RegistroState.Idle
    }

    fun resetState() {
        uiState = RegistroState.Idle
        pasoActual = 1
        datosUsuario = null
        nombre = ""
        identificacion = ""
        email = ""
        telefono = ""
        password = ""
        tipoUsuario = "ESTUDIANTE"
        carrera = ""
        universidad = ""
        semestre = ""
        promedio = ""
        areasInteres.clear()
        cvUrl = ""
        disponibilidad = ""
        tieneExperiencia = false
        listaExperiencia.clear()
    }
}

data class ExperienciaLaboral(
    var empresa: String = "",
    var cargo: String = "",
    var duracion: String = ""
)

sealed class RegistroState {
    object Idle : RegistroState()
    object Paso2 : RegistroState()
    object Loading : RegistroState()
    data class Success(val usuario: com.example.frontend_bolsa_empleo_universitaria.model.Usuario) : RegistroState()
    data class Error(val mensaje: String) : RegistroState()
}
