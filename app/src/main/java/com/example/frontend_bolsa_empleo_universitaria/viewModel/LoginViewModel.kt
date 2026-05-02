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
        if (!validar(email, password)) return

        viewModelScope.launch {
            uiState = LoginState.Loading
            uiState = repo.login(email, password).fold(
                onSuccess = { LoginState.Success(it) },
                onFailure = { LoginState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    private fun validar(email: String, password: String): Boolean {
        if (email.isBlank() || !email.contains("@")) {
            uiState = LoginState.Error("Correo inválido")
            return false
        }
        if (password.length < 8) {
            uiState = LoginState.Error("Contraseña muy corta")
            return false
        }
        return true
    }

    fun resetState() { uiState = LoginState.Idle }
}

sealed class LoginState {
    object Idle    : LoginState()
    object Loading : LoginState()
    data class Success(val usuario: Usuario) : LoginState()
    data class Error(val mensaje: String)   : LoginState()
}