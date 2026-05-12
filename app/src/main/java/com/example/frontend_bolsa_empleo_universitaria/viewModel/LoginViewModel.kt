package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.frontend_bolsa_empleo_universitaria.repository.AuthRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val token: String, val rol: String, val email: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: Token,
    private val empresaRepository: EmpresaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                // 1. Intento como Usuario (Estudiantes y Admins)
                val userResult = authRepository.login(email, password)
                
                if (userResult.isSuccess) {
                    val response = userResult.getOrNull()
                    response?.let {
                        val rolBackend = it.usuario.tipoUsuario
                        val rolApp = when(rolBackend) {
                            "ADMIN" -> "ADMIN"
                            "EMPR" -> "EMPRESA"
                            else -> "ESTUDIANTE"
                        }
                        
                        tokenManager.saveToken(
                            token = it.token,
                            email = email,
                            rol = rolApp,
                            idEmpresa = 0,
                            idUsuario = it.usuario.idUsuario,
                            nombre = it.usuario.nombre,
                            apellido = it.usuario.apellido,
                            telefono = it.usuario.telefono ?: ""
                        )
                        stopStatusPolling()
                        _uiState.value = LoginUiState.Success(it.token, rolApp, email)
                        return@launch
                    }
                }

                val userError = userResult.exceptionOrNull()?.message ?: ""

                // Si es un error de credenciales o de estado (como PENDIENTE), probamos con empresa
                if (userError.contains("401") || userError.contains("403") || userError.contains("PENDIENTE") || userError.contains("RECHAZADA")) {
                    
                    // 2. Intento como Empresa
                    val empresaResult = empresaRepository.login(email, password)
                    if (empresaResult.isSuccess) {
                        val response = empresaResult.getOrNull()
                        response?.let {
                            tokenManager.saveToken(
                                token = it.token,
                                email = email,
                                rol = "EMPRESA",
                                idEmpresa = it.empresa.idEmpresa,
                                idUsuario = it.usuario?.idUsuario ?: 0,
                                nombre = it.usuario?.nombre ?: "",
                                apellido = it.usuario?.apellido ?: "",
                                telefono = it.usuario?.telefono ?: ""
                            )
                            stopStatusPolling()
                            _uiState.value = LoginUiState.Success(it.token, "EMPRESA", email)
                            return@launch
                        }
                    }

                    // Manejo de errores de Empresa
                    val empresaError = empresaResult.exceptionOrNull()?.message ?: ""
                    val finalError = when {
                        empresaError.contains("TIMEOUT_ERROR") -> "❌ Tiempo de espera agotado. El servidor no responde."
                        empresaError.contains("NETWORK_ERROR") -> "❌ Error de conexión. Revisa tu internet."
                        empresaError.contains("PENDIENTE") || empresaError.contains("RECHAZADA") -> empresaError
                        empresaError.isNotBlank() && !empresaError.contains("HTTP_ERROR") -> empresaError
                        else -> "❌ Usuario o contraseña incorrectos. Por favor, verifique sus datos."
                    }
                    _uiState.value = LoginUiState.Error(finalError)
                    
                } else {
                    // Manejo de errores de Usuario
                    val finalError = when {
                        userError.contains("TIMEOUT_ERROR") -> "❌ El servidor tardó demasiado en responder."
                        userError.contains("NETWORK_ERROR") -> "❌ Error de conexión. No se pudo contactar con el servidor."
                        userError.contains("PENDIENTE") || userError.contains("RECHAZADA") -> userError
                        userError.isNotBlank() && !userError.contains("HTTP_ERROR") -> userError
                        else -> "❌ Usuario o contraseña incorrectos. Por favor, verifique sus datos."
                    }
                    _uiState.value = LoginUiState.Error(finalError)
                }

            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Error inesperado: ${e.message}")
            }
        }
    }

    fun startStatusPolling(email: String, pass: String) {
        if (pollingJob?.isActive == true) return
        
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(5000) // Revisa cada 5 segundos (más corto como pediste)
                val userResult = authRepository.login(email, pass)
                if (userResult.isSuccess) {
                    login(email, pass) // Si ya no da error, hace el login completo
                    break
                } else {
                    val error = userResult.exceptionOrNull()?.message ?: ""
                    // Si el error ya no es PENDIENTE (ej: ahora es RECHAZADA o SUCCESS), actualizamos
                    if (!error.contains("PENDIENTE", ignoreCase = true)) {
                        login(email, pass)
                        break
                    }
                }
            }
        }
    }

    fun stopStatusPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopStatusPolling()
    }
}
