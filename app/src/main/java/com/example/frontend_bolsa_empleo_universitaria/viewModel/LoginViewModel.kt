package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.frontend_bolsa_empleo_universitaria.repository.AuthRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.AdminRepository
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
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
    private val empresaRepository: EmpresaRepository,
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

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
                        _uiState.value = LoginUiState.Success(it.token, rolApp, email)
                        return@launch
                    }
                }

                val userError = userResult.exceptionOrNull()?.message ?: ""

                // PASO PREVIO: Verificar si es una empresa pendiente ANTES de intentar el login de empresa
                if (userError.contains("HTTP_ERROR_401") || userError.contains("HTTP_ERROR_403")) {
                    try {
                        val pendientesResponse = adminRepository.listarEmpresasPendientes()
                        if (pendientesResponse.isSuccessful) {
                            val listaPendientes = pendientesResponse.body()
                            val esPendiente = listaPendientes?.any { it.email.equals(email, ignoreCase = true) } == true
                            
                            if (esPendiente) {
                                _uiState.value = LoginUiState.Error("⏳ Su solicitud de registro está en proceso de revisión por el administrador. Por favor, espere a ser aprobado.")
                                return@launch
                            }
                        }
                    } catch (e: Exception) {
                        // Fallback silencioso si falla la red al listar pendientes
                    }

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
                            _uiState.value = LoginUiState.Success(it.token, "EMPRESA", email)
                            return@launch
                        }
                    }

                    val empresaError = empresaResult.exceptionOrNull()?.message ?: ""
                    val finalError = when {
                        empresaError.contains("TIMEOUT_ERROR") -> "❌ Tiempo de espera agotado."
                        else -> "❌ Usuario o contraseña incorrectos. Por favor, verifique sus datos."
                    }
                    _uiState.value = LoginUiState.Error(finalError)

                } else {
                    // Otros errores de usuario (Red, Timeout)
                    val finalError = when {
                        userError.contains("TIMEOUT_ERROR") -> "❌ El servidor tardó demasiado en responder."
                        userError.contains("NETWORK_ERROR") -> "❌ Error de conexión. Revisa tu internet."
                        else -> "❌ Usuario o contraseña incorrectos. Por favor, verifique sus datos."
                    }
                    _uiState.value = LoginUiState.Error(finalError)
                }

            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Error inesperado: ${e.message}")
            }
        }
    }
}
