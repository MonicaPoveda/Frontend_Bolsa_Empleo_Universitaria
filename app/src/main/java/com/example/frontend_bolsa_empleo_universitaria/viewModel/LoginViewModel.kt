package com.example.frontend_bolsa_empleo_universitaria.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.Model.Usuario
import com.example.frontend_bolsa_empleo_universitaria.Repository.UsuarioRepository
import kotlinx.coroutines.launch


import androidx.lifecycle.ViewModelProvider

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

class LoginViewModelFactory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class LoginState {
    object Idle    : LoginState()
    object Loading : LoginState()
    data class Success(val usuario: Usuario) : LoginState()
    data class Error(val mensaje: String)   : LoginState()
}