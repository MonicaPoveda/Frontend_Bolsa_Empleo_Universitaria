package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.interfaces.PerfilApi
import com.example.frontend_bolsa_empleo_universitaria.interfaces.UsuarioApi
import com.example.frontend_bolsa_empleo_universitaria.model.ActualizarPerfilUsuario
import com.example.frontend_bolsa_empleo_universitaria.model.ActualizarUsuario
import com.example.frontend_bolsa_empleo_universitaria.model.Perfil
import com.example.frontend_bolsa_empleo_universitaria.model.UsuarioDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val usuarioApi: UsuarioApi,
    private val perfilApi: PerfilApi
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
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                // 1. Cargar usuario (esto sí existe)
                val responseUsu = usuarioApi.buscarPorEmail(email)
                if (responseUsu.isSuccessful) {
                    _usuario.value = responseUsu.body()
                } else {
                    _error.value = "Error al obtener usuario: ${responseUsu.code()}"
                }

                // 2. Cargar perfil usando listar y filtrar
                val responsePerfil = perfilApi.listarPerfiles() // ✅ usa listar
                if (responsePerfil.isSuccessful) {
                    val perfiles = responsePerfil.body() ?: emptyList()
                    val miPerfil = perfiles.find { it.idUsuario == idUsuario }
                    _perfil.value = miPerfil
                } else {
                    _error.value = "Error al obtener perfil: ${responsePerfil.code()}"
                }
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
                val response = perfilApi.actualizarPerfil(idPerfil, request)
                if (response.isSuccessful) {
                    _perfil.value = response.body()
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
}
