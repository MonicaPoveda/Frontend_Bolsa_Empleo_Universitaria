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
import kotlinx.coroutines.TimeoutCancellationException

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val rol: String, val email: String, val token: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val token: Token,
    private val empresaRepository: EmpresaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            println("🔐 Login iniciado para: $email")

            try {
                _uiState.value = LoginUiState.Loading
                println("📡 Estado: Loading")

                // Intentar login como estudiante/admin
                val userResult = authRepository.login(email, password)

                userResult.fold(
                    onSuccess = { response ->
                        println("✅ Login exitoso como usuario")
                        val rol = response.usuario.tipoUsuario.uppercase()
                        println("🎭 Rol obtenido: $rol")

                        token.saveToken(response.token, response.usuario.email, rol)
                        println("💾 Token guardado")

                        _uiState.value = LoginUiState.Success(
                            rol = rol,
                            email = response.usuario.email,
                            token = response.token
                        )
                        println("📤 State actualizado a Success")
                    },
                    onFailure = { error ->
                        println("❌ Falló login usuario: ${error.message}")
                        
                        // Si el error es de red, no intentamos con empresa porque fallará igual
                        if (error.message?.contains("red") == true || error.message?.contains("conexión") == true) {
                            _uiState.value = LoginUiState.Error("Error de red: Verifica tu conexión a internet.")
                            return@fold
                        }

                        // Intentar como empresa
                        try {
                            println("🏢 Intentando login como empresa...")
                            val empresaResult = empresaRepository.login(email, password)

                            empresaResult.fold(
                                onSuccess = { response ->
                                    println("✅ Login exitoso como empresa")
                                    token.saveToken(response.token, response.empresa.email, "EMPRESA")
                                    _uiState.value = LoginUiState.Success(
                                        rol = "EMPRESA",
                                        email = response.empresa.email,
                                        token = response.token
                                    )
                                    println("📤 State actualizado a Success (Empresa)")
                                },
                                onFailure = { empresaError ->
                                    println("❌ Falló también como empresa: ${empresaError.message}")
                                    
                                    val finalErrorMessage = when {
                                        empresaError.message?.contains("401") == true || 
                                        empresaError.message?.contains("403") == true -> 
                                            "Email o contraseña incorrectos."
                                        empresaError.message?.contains("500") == true ->
                                            "Error interno del servidor. Intenta más tarde."
                                        else -> "Credenciales incorrectas o usuario no encontrado."
                                    }
                                    
                                    _uiState.value = LoginUiState.Error(finalErrorMessage)
                                }
                            )
                        } catch (e: Exception) {
                            println("💥 Excepción en login empresa: ${e.message}")
                            _uiState.value = LoginUiState.Error("Error al procesar la solicitud.")
                        }
                    }
                )
            } catch (e: TimeoutCancellationException) {
                println("⏰ Timeout - El servidor tardó demasiado en responder")
                _uiState.value = LoginUiState.Error(
                    "Tiempo de espera agotado. Intenta nuevamente."
                )
            } catch (e: Exception) {
                println("💥 Error general: ${e.message}")
                _uiState.value = LoginUiState.Error(
                    "Error de conexión: ${e.message}"
                )
            }
        }
    }
}
