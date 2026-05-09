package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.frontend_bolsa_empleo_universitaria.repository.AuthRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
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
    private val empresaRepository: EmpresaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            println("🔐 Login iniciado para: $email")

            try {
                // Primero intentar login como estudiante
                val estudianteResult = authRepository.login(email, password)

                if (estudianteResult.isSuccess) {
                    val response = estudianteResult.getOrNull()
                    response?.let {
                        println("✅ Login exitoso como ESTUDIANTE")
                        tokenManager.saveToken(
                            token = it.token,
                            email = email,
                            rol = "ESTUDIANTE",
                            idEmpresa = 0,
                            idUsuario = it.usuario.idUsuario,
                            nombre = it.usuario.nombre,
                            apellido = it.usuario.apellido
                        )
                        _uiState.value = LoginUiState.Success(it.token, "ESTUDIANTE", email)
                        return@launch
                    }
                } else {
                    println("❌ Falló login como estudiante: ${estudianteResult.exceptionOrNull()?.message}")
                }

                // Si falla como estudiante, intentar como empresa
                val empresaResult = empresaRepository.login(email, password)

                if (empresaResult.isSuccess) {
                    val response = empresaResult.getOrNull()
                    response?.let {
                        println("✅ Login exitoso como EMPRESA")
                        println("   ID Empresa: ${it.empresa.idEmpresa}")
                        tokenManager.saveToken(
                            token = it.token,
                            email = email,
                            rol = "EMPRESA",
                            idEmpresa = it.empresa.idEmpresa,
                            idUsuario = it.usuario?.idUsuario ?: -1,
                            nombre = it.usuario?.nombre ?: it.empresa.nombre,
                            apellido = it.usuario?.apellido ?: ""
                        )
                        _uiState.value = LoginUiState.Success(it.token, "EMPRESA", email)
                        return@launch
                    }
                } else {
                    println("❌ Falló login como empresa: ${empresaResult.exceptionOrNull()?.message}")
                }

                // Si ambos fallan, mostrar error
                val error = when {
                    empresaResult.exceptionOrNull()?.message?.contains("PENDIENTE") == true ->
                        "❌ Tu solicitud está PENDIENTE. Espera la aprobación del administrador."
                    empresaResult.exceptionOrNull()?.message?.contains("no encontrada") == true ||
                            estudianteResult.exceptionOrNull()?.message?.contains("no encontrada") == true ->
                        "❌ Credenciales incorrectas. Verifica tu email y contraseña."
                    empresaResult.exceptionOrNull()?.message?.contains("403") == true ||
                            estudianteResult.exceptionOrNull()?.message?.contains("403") == true ->
                        "❌ Acceso denegado. Verifica tus credenciales."
                    else -> "❌ Error al iniciar sesión. Verifica tus credenciales."
                }
                _uiState.value = LoginUiState.Error(error)

            } catch (e: Exception) {
                println("💥 Error en login: ${e.message}")
                _uiState.value = LoginUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

}