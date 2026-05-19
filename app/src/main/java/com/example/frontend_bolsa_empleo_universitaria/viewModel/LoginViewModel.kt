package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponse
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponseEmpresa
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
        const val PENDING_COMPANY_ERROR = "Tu solicitud de registro está en proceso de revisión por el administrador."
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val cleanEmail = email.trim()
            _uiState.value = LoginUiState.Loading

            try {
                val usuarioExiste = authRepository.existeEmail(cleanEmail)
                if (usuarioExiste.isSuccess && usuarioExiste.getOrDefault(false)) {
                    loginUsuario(cleanEmail, password)
                    return@launch
                }

                if (usuarioExiste.isFailure && isServerOrNetworkError(usuarioExiste.errorMessage())) {
                    _uiState.value = LoginUiState.Error(toLoginMessage(usuarioExiste.errorMessage()))
                    return@launch
                }

                val estadoAdministrativo = getAdministrativeCompanyStatus(cleanEmail)
                if (estadoAdministrativo != null) {
                    _uiState.value = LoginUiState.Error(estadoAdministrativo)
                    return@launch
                }

                val empresaExiste = empresaRepository.existeEmail(cleanEmail)
                if (empresaExiste.isSuccess && empresaExiste.getOrDefault(false)) {
                    loginEmpresa(cleanEmail, password)
                    return@launch
                }

                if (empresaExiste.isFailure && isServerOrNetworkError(empresaExiste.errorMessage())) {
                    _uiState.value = LoginUiState.Error(toLoginMessage(empresaExiste.errorMessage()))
                    return@launch
                }

                if (usuarioExiste.isFailure || empresaExiste.isFailure) {
                    loginConFallback(cleanEmail, password)
                } else {
                    _uiState.value = LoginUiState.Error(CREDENTIALS_ERROR)
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(CONNECTION_ERROR)
            }
        }
    }

    private suspend fun loginUsuario(email: String, password: String) {
        val userResult = authRepository.login(email, password)
        if (userResult.isSuccess) {
            userResult.getOrNull()?.let { handleUsuarioSuccess(it, email) }
            return
        }

        _uiState.value = LoginUiState.Error(toLoginMessage(userResult.errorMessage()))
    }

    private suspend fun loginEmpresa(email: String, password: String) {
        val empresaResult = empresaRepository.login(email, password)
        if (empresaResult.isSuccess) {
            empresaResult.getOrNull()?.let { handleEmpresaSuccess(it, email) }
            return
        }

        _uiState.value = LoginUiState.Error(toLoginMessage(empresaResult.errorMessage()))
    }

    private suspend fun loginConFallback(email: String, password: String) {
        val userResult = authRepository.login(email, password)
        if (userResult.isSuccess) {
            userResult.getOrNull()?.let { handleUsuarioSuccess(it, email) }
            return
        }

        val userError = userResult.errorMessage()
        if (isServerOrNetworkError(userError)) {
            _uiState.value = LoginUiState.Error(toLoginMessage(userError))
            return
        }

        val estadoAdministrativo = getAdministrativeCompanyStatus(email)
        if (estadoAdministrativo != null) {
            _uiState.value = LoginUiState.Error(estadoAdministrativo)
            return
        }

        val empresaResult = empresaRepository.login(email, password)
        if (empresaResult.isSuccess) {
            empresaResult.getOrNull()?.let { handleEmpresaSuccess(it, email) }
            return
        }

        _uiState.value = LoginUiState.Error(toLoginMessage(empresaResult.errorMessage()))
    }

    private fun handleUsuarioSuccess(response: LoginResponse, email: String) {
        val rolApp = when (response.usuario.tipoUsuario) {
            "ADMIN" -> "ADMIN"
            "EMPR" -> "EMPRESA"
            else -> "ESTUDIANTE"
        }

        tokenManager.saveToken(
            token = response.token,
            email = email,
            rol = rolApp,
            idEmpresa = 0,
            idUsuario = response.usuario.idUsuario,
            nombre = response.usuario.nombre,
            apellido = response.usuario.apellido,
            telefono = response.usuario.telefono ?: ""
        )
        stopStatusPolling()
        _uiState.value = LoginUiState.Success(response.token, rolApp, email)
    }

    private fun handleEmpresaSuccess(response: LoginResponseEmpresa, email: String) {
        tokenManager.saveToken(
            token = response.token,
            email = email,
            rol = "EMPRESA",
            idEmpresa = response.empresa.idEmpresa,
            idUsuario = response.usuario?.idUsuario ?: 0,
            nombre = response.usuario?.nombre ?: "",
            apellido = response.usuario?.apellido ?: "",
            telefono = response.usuario?.telefono ?: ""
        )
        stopStatusPolling()
        _uiState.value = LoginUiState.Success(response.token, "EMPRESA", email)
    }

    private suspend fun getAdministrativeCompanyStatus(email: String): String? {
        return try {
            val response = adminRepository.listarEmpresasPendientes()
            if (!response.isSuccessful) return null

            val solicitud = response.body()
                ?.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
                ?: return null

            when (solicitud.estado.uppercase()) {
                "PENDIENTE" -> PENDING_COMPANY_ERROR
                "RECHAZADA" -> "RECHAZADA: ${solicitud.mensaje.ifBlank { "Tu registro no ha sido aprobado por el administrador." }}"
                "BLOQUEADA" -> "RECHAZADA: ${solicitud.mensaje.ifBlank { "Tu solicitud no puede continuar. Contacta al administrador." }}"
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun toLoginMessage(error: String): String {
        return when {
            error.contains("TIMEOUT_ERROR", ignoreCase = true) -> TIMEOUT_ERROR
            error.contains("NETWORK_ERROR", ignoreCase = true) -> CONNECTION_ERROR
            error.contains("SERVER_ERROR", ignoreCase = true) -> TIMEOUT_ERROR
            error.contains("PENDIENTE", ignoreCase = true) -> PENDING_COMPANY_ERROR
            error.contains("RECHAZADA", ignoreCase = true) -> error
            else -> CREDENTIALS_ERROR
        }
    }

    private fun isServerOrNetworkError(error: String): Boolean {
        return error.contains("TIMEOUT_ERROR", ignoreCase = true) ||
            error.contains("NETWORK_ERROR", ignoreCase = true) ||
            error.contains("SERVER_ERROR", ignoreCase = true)
    }

    private fun <T> Result<T>.errorMessage(): String {
        return exceptionOrNull()?.message.orEmpty()
    }

    fun startStatusPolling(email: String, pass: String) {
        if (pollingJob?.isActive == true) return

        pollingJob = viewModelScope.launch {
            while (true) {
                delay(15000)
                val estadoAdministrativo = getAdministrativeCompanyStatus(email)
                if (estadoAdministrativo == null || estadoAdministrativo.contains("RECHAZADA", ignoreCase = true)) {
                    login(email, pass)
                    break
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
