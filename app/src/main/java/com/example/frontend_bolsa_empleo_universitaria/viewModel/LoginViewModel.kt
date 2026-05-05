package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.Usuario
import com.example.frontend_bolsa_empleo_universitaria.repository.UsuarioRepository
import kotlinx.coroutines.launch


class LoginViewModel(private val repo: UsuarioRepository) : ViewModel() {

    var uiState by mutableStateOf<LoginState>(LoginState.Idle)
        private set

    fun login(email: String, password: String) {
        // Limpiamos estado previo para agilidad y evitar bloqueos
        uiState = LoginState.Idle

        if (!validar(email, password)) return

        viewModelScope.launch {
            uiState = LoginState.Loading
            uiState = repo.login(email, password).fold(
                onSuccess = { LoginState.Success(it) },
                onFailure = {
                    // Si falla por credenciales (401) o no encontrado (404), mensaje unificado por seguridad y claridad
                    val errorMsg = if (it.message?.contains("401") == true || it.message?.contains("404") == true) {
                        "El correo o la contraseña son inválidas"
                    } else {
                        it.message ?: "Error de conexión"
                    }
                    LoginState.Error(errorMsg)
                }
            )
        }
    }

    private fun validar(email: String, password: String): Boolean {
        if (email.isBlank()) {
            uiState = LoginState.Error("El campo Correo es obligatorio")
            return false
        }
        if (!email.contains("@") || !email.contains(".")) {
            uiState = LoginState.Error("El correo debe tener un formato válido (ejemplo@dominio.com)")
            return false
        }
        if (password.isBlank()) {
            uiState = LoginState.Error("La Contraseña es obligatoria")
            return false
        }
        if (password.length < 8) {
            uiState = LoginState.Error("La Contraseña debe tener al menos 8 caracteres")
            return false
        }
        return true
    }

    fun resetState() { uiState = LoginState.Idle }

    fun setSuccessState(usuario: Usuario) {
        uiState = LoginState.Success(usuario)
    }

    fun actualizarPassword(email: String, nuevaPass: String) {
        if (nuevaPass.length < 8) {
            uiState = LoginState.Error("La nueva contraseña debe tener al menos 8 caracteres")
            return
        }
        viewModelScope.launch {
            uiState = LoginState.Loading
            repo.buscarPorEmail(email).fold(
                onSuccess = { usuario ->
                    val actualizado = usuario.copy(password = nuevaPass)
                    repo.actualizar(usuario.idUsuario!!, actualizado).fold(
                        onSuccess = { uiState = LoginState.Error("Contraseña actualizada. Inicia sesión.") },
                        onFailure = { uiState = LoginState.Error("Error al actualizar: ${it.message}") }
                    )
                },
                onFailure = { uiState = LoginState.Error("Usuario no encontrado") }
            )
        }
    }
}

sealed class LoginState {
    object Idle    : LoginState()
    object Loading : LoginState()
    data class Success(val usuario: Usuario) : LoginState()
    data class Error(val mensaje: String)   : LoginState()
}