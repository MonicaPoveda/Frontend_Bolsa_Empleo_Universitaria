package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.interfaces.UsuarioApi
import com.example.frontend_bolsa_empleo_universitaria.model.ActualizarPerfilUsuario
import com.example.frontend_bolsa_empleo_universitaria.model.ActualizarUsuario
import com.example.frontend_bolsa_empleo_universitaria.model.Perfil
import com.example.frontend_bolsa_empleo_universitaria.model.UsuarioDTO
import com.example.frontend_bolsa_empleo_universitaria.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val usuarioApi: UsuarioApi,
    private val perfilRepository: PerfilRepository
) : ViewModel() {

    private val _usuario = MutableStateFlow<UsuarioDTO?>(null)
    val usuario: StateFlow<UsuarioDTO?> = _usuario

    private val _perfil = MutableStateFlow<Perfil?>(null)
    val perfil: StateFlow<Perfil?> = _perfil

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarTodo(email: String, idUsuario: Long) {
        cargarSoloPerfil(idUsuario)
    }

    fun cargarSoloPerfil(idUsuario: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                if (idUsuario <= 0L) {
                    _error.value = "Sesión no válida"
                    _perfil.value = null
                    return@launch
                }
                _perfil.value = perfilRepository.cargarPerfilEstudiante(idUsuario)
            } catch (e: Exception) {
                _error.value = "Fallo de conexión: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun actualizarUsuario(idUsuario: Long, request: ActualizarUsuario, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = usuarioApi.actualizarUsuario(idUsuario, request)
                if (response.isSuccessful) {
                    _usuario.value = response.body()
                    onSuccess()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error ${response.code()}"
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error de red")
            } finally {
                _loading.value = false
            }
        }
    }

    fun actualizarPerfil(idPerfil: Long, request: ActualizarPerfilUsuario, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val perfilActualizado = perfilRepository.actualizarPerfil(idPerfil, request)
                if (perfilActualizado != null) {
                    _perfil.value = perfilActualizado
                    onSuccess()
                } else {
                    onError("Error al actualizar el perfil")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error de red")
            } finally {
                _loading.value = false
            }
        }
    }
}
