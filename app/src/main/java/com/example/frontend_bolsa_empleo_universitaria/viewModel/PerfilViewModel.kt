package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

class PerfilViewModel : ViewModel() {

    private val repository = PerfilRepository()
    private val usuarioRepository = UsuarioRepository()
    var uiState by mutableStateOf<PerfilState>(PerfilState.Idle)
        private set

    var perfil by mutableStateOf<Perfil?>(null)
        private set

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set
    var nombreUsuario by mutableStateOf("")
    var emailUsuario by mutableStateOf("")
    var tipoUsuario by mutableStateOf("ESTUDIANTE")

    // Campos editables
    var carrera by mutableStateOf("")
    var universidad by mutableStateOf("")
    var semestre by mutableStateOf("")
    var promedio by mutableStateOf("")
    var disponibilidad by mutableStateOf("")
    var cvUrl by mutableStateOf("")
    var experiencia by mutableStateOf("")
    var habilidades by mutableStateOf("")

    fun cargarPerfil(idUsuario: Long) {
        viewModelScope.launch {
            uiState = PerfilState.Loading
            repository.obtenerPorUsuario(idUsuario).fold(
                onSuccess = { p ->
                    perfil = p
                    carrera = p.carrera
                    universidad = p.universidad
                    semestre = p.semestre
                    promedio = p.promedio?.toString() ?: ""
                    disponibilidad = p.disponibilidad ?: ""
                    cvUrl = p.cvUrl ?: ""
                    experiencia = p.experiencia ?: ""
                    habilidades = p.habilidades
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
        nombreUsuario = usuario.nombre
        emailUsuario = usuario.email
        tipoUsuario = usuario.tipoUsuario
    }

    // 👈 Nuevo: guardar cambios del usuario
    fun guardarCambiosUsuario(onSuccess: (Usuario) -> Unit) {
        val actual = usuarioActual ?: return
        val id = actual.idUsuario ?: return
        viewModelScope.launch {
            uiState = PerfilState.Loading
            val actualizado = actual.copy(
                nombre = nombreUsuario,
                email = emailUsuario,
                tipoUsuario = tipoUsuario
            )
            usuarioRepository.actualizar(id, actualizado).fold(
                onSuccess = { usuarioActualizado ->
                    usuarioActual = usuarioActualizado
                    uiState = PerfilState.Success
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

        viewModelScope.launch {
            uiState = PerfilState.Loading
            val actualizado = perfilActual.copy(
                carrera = carrera,
                universidad = universidad,
                semestre = semestre,
                promedio = promedio.toDoubleOrNull(),
                disponibilidad = disponibilidad,
                cvUrl = cvUrl,
                experiencia = experiencia,
                habilidades = habilidades
            )
            repository.actualizar(id, actualizado).fold(
                onSuccess = {
                    perfil = it
                    uiState = PerfilState.Success
                },
                onFailure = {
                    uiState = PerfilState.Error(it.message ?: "Error al guardar")
                }
            )
        }
    }

    fun resetState() { uiState = PerfilState.Idle }
}