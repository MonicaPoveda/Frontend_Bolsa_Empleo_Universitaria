// LoginViewModel.kt
package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.repository.AuthRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val token: String, val rol: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: Token,
    private val empresaRepository: EmpresaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            try {
                // Primero intentar login como estudiante
                val estudianteResult = authRepository.login(email, password)

                if (estudianteResult.isSuccess) {
                    val response = estudianteResult.getOrNull()
                    response?.let {
                        tokenManager.saveToken(it.token, email, "ESTUDIANTE")
                        _uiState.value = LoginUiState.Success(it.token, "ESTUDIANTE")
                        return@launch
                    }
                }

                // Si falla como estudiante, intentar como empresa
                val empresaResult = empresaRepository.login(email, password)

                if (empresaResult.isSuccess) {
                    val response = empresaResult.getOrNull()
                    response?.let {
                        tokenManager.saveToken(it.token, email, "EMPRESA")
                        _uiState.value = LoginUiState.Success(it.token, "EMPRESA")
                        return@launch
                    }
                }

                // Si ambos fallan, mostrar error
                val error = when {
                    empresaResult.exceptionOrNull()?.message?.contains("PENDIENTE") == true ->
                        "❌ Tu solicitud está PENDIENTE. Espera la aprobación del administrador."
                    empresaResult.exceptionOrNull()?.message?.contains("no encontrada") == true ->
                        "❌ Credenciales incorrectas. Verifica tu email y contraseña."
                    else -> "❌ Error al iniciar sesión. Verifica tus credenciales."
                }
                _uiState.value = LoginUiState.Error(error)

            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}