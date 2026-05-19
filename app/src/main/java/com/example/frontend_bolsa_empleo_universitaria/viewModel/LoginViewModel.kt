package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.repository.AdminRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.AuthRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    private var pollingJob: Job? = null

    private companion object {
        const val CREDENTIALS_ERROR = "Usuario o contraseña incorrecta. Por favor verifique sus datos."
        const val CONNECTION_ERROR = "No se pudo conectar con el servidor. Intenta nuevamente en unos minutos."
        const val TIMEOUT_ERROR = "El servidor tardó demasiado en responder. Intenta nuevamente."
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val userResult = authRepository.login(email, password)

                if (userResult.isSuccess) {
                    val response = userResult.getOrNull()
                    response?.let {
                        val rolBackend = it.usuario.tipoUsuario
                        val rolApp = when (rolBackend) {
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

                if (
                    userError.contains("401") ||
                    userError.contains("403") ||
                    userError.contains("PENDIENTE") ||
                    userError.contains("RECHAZADA")
                ) {
                    try {
                        val pendientesResponse = adminRepository.listarEmpresasPendientes()
                        if (pendientesResponse.isSuccessful) {
                            val listaPendientes = pendientesResponse.body()
                            val esPendiente = listaPendientes?.any { it.email.equals(email, ignoreCase = true) } == true

                            if (esPendiente) {
                                _uiState.value = LoginUiState.Error("PENDIENTE: Su solicitud de registro está en proceso de revisión por el administrador. Por favor, espere a ser aprobado.")
                                return@launch
                            }
                        }
                    } catch (e: Exception) {
                        // Si no se puede consultar el estado administrativo, continúa con el login de empresa.
                    }

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

                    val empresaError = empresaResult.exceptionOrNull()?.message ?: ""
                    _uiState.value = LoginUiState.Error(loginErrorMessage(empresaError))
                } else {
                    _uiState.value = LoginUiState.Error(loginErrorMessage(userError))
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(CONNECTION_ERROR)
            }
        }
    }

    private fun loginErrorMessage(error: String): String {
        return when {
            error.contains("TIMEOUT_ERROR", ignoreCase = true) -> TIMEOUT_ERROR
            error.contains("NETWORK_ERROR", ignoreCase = true) -> CONNECTION_ERROR
            error.contains("SERVER_ERROR", ignoreCase = true) || error.contains("HTTP_ERROR_5", ignoreCase = true) -> TIMEOUT_ERROR
            error.contains("PENDIENTE", ignoreCase = true) || error.contains("RECHAZADA", ignoreCase = true) -> error
            else -> CREDENTIALS_ERROR
        }
    }

    fun startStatusPolling(email: String, pass: String) {
        if (pollingJob?.isActive == true) return

        pollingJob = viewModelScope.launch {
            while (true) {
                delay(5000)
                val userResult = authRepository.login(email, pass)
                if (userResult.isSuccess) {
                    login(email, pass)
                    break
                } else {
                    val error = userResult.exceptionOrNull()?.message ?: ""
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
