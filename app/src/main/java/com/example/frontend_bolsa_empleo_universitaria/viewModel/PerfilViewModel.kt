package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.Perfil
import com.example.frontend_bolsa_empleo_universitaria.model.Usuario
import com.example.frontend_bolsa_empleo_universitaria.repository.PerfilRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.UsuarioRepository
import kotlinx.coroutines.launch

sealed class PerfilState {
    object Idle : PerfilState()
    object Loading : PerfilState()
    object Success : PerfilState()
    data class Error(val mensaje: String) : PerfilState()
}

open class PerfilViewModel : ViewModel() {

    private val repository = PerfilRepository()
    private val usuarioRepository = UsuarioRepository()
    var uiState by mutableStateOf<PerfilState>(PerfilState.Idle)
        private set

    var perfil by mutableStateOf<Perfil?>(null)
        private set

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set
    var nombre by mutableStateOf("")
    var apellido by mutableStateOf("")
    var emailUsuario by mutableStateOf("")
    var telefonoUsuario by mutableStateOf("")
    var tipoUsuario by mutableStateOf("ESTUDIANTE")
    var isEditing by mutableStateOf(false)

    // Campos editables
    var carrera by mutableStateOf("")
    var universidad by mutableStateOf("")
    var semestre by mutableStateOf("")
    var disponibilidad by mutableStateOf("")
    var cvUrl by mutableStateOf("")
    var habilidades by mutableStateOf("")
    var listaExperiencia = mutableStateListOf<ExperienciaLaboral>()
    var tieneExperiencia by mutableStateOf(false)

    // Listas para los dropdowns
    val opcionesCarreras = listOf("Ingeniería de Sistemas", "Ingeniería Industrial", "Administración de Empresas", "Psicología", "Derecho", "Medicina", "Diseño Gráfico")
    val opcionesSemestres = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    val opcionesDisponibilidad = listOf("Tiempo Completo", "Medio Tiempo", "Remoto", "Prácticas")
    val sugerenciasHabilidades = listOf("Java", "Kotlin", "Python", "SQL", "Liderazgo", "Inglés B2", "Trabajo en Equipo", "Resolución de Problemas")

    fun cargarPerfil(idUsuario: Long) {
        viewModelScope.launch {
            uiState = PerfilState.Loading
            repository.obtenerPorUsuario(idUsuario).fold(
                onSuccess = { p ->
                    perfil = p
                    carrera = p.carrera
                    universidad = p.universidad
                    semestre = p.semestre
                    disponibilidad = p.disponibilidad ?: ""
                    cvUrl = p.cvUrl ?: ""
                    habilidades = p.habilidades
                    
                    // Parsear experiencia
                    val exp = p.experiencia ?: ""
                    if (exp.isNotBlank() && exp != "Sin experiencia") {
                        tieneExperiencia = true
                        listaExperiencia.clear()
                        exp.split(" | ").forEach { expStr ->
                            try {
                                val match = Regex("""(.*) \((.*), (.*)\)""").find(expStr)
                                if (match != null) {
                                    val (emp, car, dur) = match.destructured
                                    listaExperiencia.add(ExperienciaLaboral(emp, car, dur))
                                }
                            } catch (e: Exception) { }
                        }
                    } else {
                        tieneExperiencia = false
                        listaExperiencia.clear()
                    }
                    
                    uiState = PerfilState.Idle
                },
                onFailure = {
                    uiState = PerfilState.Error(it.message ?: "Error al cargar perfil")
                }
            )
        }
    }

    fun cargarUsuario(usuario: Usuario) {
        usuarioActual = usuario
        nombre = usuario.nombre
        apellido = usuario.apellido
        emailUsuario = usuario.email
        telefonoUsuario = usuario.telefono ?: ""
        tipoUsuario = usuario.tipoUsuario
    }

    fun cancelarEdicion() {
        isEditing = false
        usuarioActual?.let { cargarUsuario(it) }
        perfil?.let { p ->
            carrera = p.carrera
            universidad = p.universidad
            semestre = p.semestre
            disponibilidad = p.disponibilidad ?: ""
            cvUrl = p.cvUrl ?: ""
            habilidades = p.habilidades
            
            val exp = p.experiencia ?: ""
            if (exp.isNotBlank() && exp != "Sin experiencia") {
                tieneExperiencia = true
                listaExperiencia.clear()
                exp.split(" | ").forEach { expStr ->
                    try {
                        val match = Regex("""(.*) \((.*), (.*)\)""").find(expStr)
                        if (match != null) {
                            val (emp, car, dur) = match.destructured
                            listaExperiencia.add(ExperienciaLaboral(emp, car, dur))
                        }
                    } catch (e: Exception) { }
                }
            } else {
                tieneExperiencia = false
                listaExperiencia.clear()
            }
        }
    }

    fun guardarCambiosUsuario(onSuccess: (Usuario) -> Unit) {
        val actual = usuarioActual ?: return
        val id = actual.idUsuario ?: return
        
        // Validaciones
        if (nombre.isBlank() || apellido.isBlank()) {
            uiState = PerfilState.Error("Nombre y apellido son obligatorios")
            return
        }

        if (tipoUsuario == "ESTUDIANTE" && !emailUsuario.endsWith(".edu.co")) {
            uiState = PerfilState.Error("Como estudiante, debes usar tu correo institucional (.edu.co)")
            return
        }

        if (tipoUsuario == "EGRESADO" && emailUsuario.endsWith(".edu.co")) {
            uiState = PerfilState.Error("Como egresado, por favor usa un correo personal")
            return
        }

        viewModelScope.launch {
            uiState = PerfilState.Loading
            val actualizado = actual.copy(
                nombre = nombre.trim(),
                apellido = apellido.trim(),
                email = emailUsuario,
                telefono = telefonoUsuario,
                tipoUsuario = tipoUsuario
            )
            usuarioRepository.actualizar(id, actualizado).fold(
                onSuccess = { usuarioActualizado ->
                    usuarioActual = usuarioActualizado
                    guardarCambios()
                    onSuccess(usuarioActualizado)
                },
                onFailure = {
                    uiState = PerfilState.Error(it.message ?: "Error al guardar usuario")
                }
            )
        }
    }

    fun guardarCambios() {
        val perfilActual = perfil ?: return
        val id = perfilActual.idPerfil ?: return

        val expString = if (tieneExperiencia && listaExperiencia.isNotEmpty()) {
            listaExperiencia.joinToString(" | ") { "${it.empresa} (${it.cargo}, ${it.duracion})" }
        } else "Sin experiencia"

        viewModelScope.launch {
            uiState = PerfilState.Loading
            val actualizado = perfilActual.copy(
                carrera = carrera,
                universidad = universidad,
                semestre = if (tipoUsuario == "ESTUDIANTE") semestre else "Graduado",
                promedio = null,
                disponibilidad = disponibilidad,
                cvUrl = cvUrl,
                experiencia = expString,
                habilidades = habilidades
            )
            repository.actualizar(id, actualizado).fold(
                onSuccess = {
                    perfil = it
                    isEditing = false
                    uiState = PerfilState.Success
                },
                onFailure = {
                    uiState = PerfilState.Error(it.message ?: "Error al guardar perfil")
                }
            )
        }
    }

    fun resetState() { uiState = PerfilState.Idle }
}
