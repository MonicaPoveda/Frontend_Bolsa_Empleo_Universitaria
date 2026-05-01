package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.Usuario
import com.example.frontend_bolsa_empleo_universitaria.repository.UsuarioRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroViewModel(private val repo: UsuarioRepository) : ViewModel() {

    var uiState by mutableStateOf<RegistroState>(RegistroState.Idle)
        private set

    fun registrar(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String,
        tipoUsuario: String,
        password: String
    ) {
        if (!validar(nombre, apellido, email, password)) return

        viewModelScope.launch {
            uiState = RegistroState.Loading
            
            val nuevoUsuario = Usuario(
                idUsuario = null, // Cambiado de 0 a null para evitar conflictos en el backend
                nombre = nombre,
                apellido = apellido,
                email = email,
                telefono = if (telefono.isBlank()) null else telefono,
                tipoUsuario = tipoUsuario,
                fechaRegistro = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                estado = true,
                password = password
            )

            uiState = repo.guardar(nuevoUsuario).fold(
                onSuccess = { RegistroState.Success },
                onFailure = { RegistroState.Error(it.message ?: "Error al registrar") }
            )
        }
    }

    private fun validar(nombre: String, apellido: String, email: String, pass: String): Boolean {
        if (nombre.isBlank() || apellido.isBlank()) {
            uiState = RegistroState.Error("Nombre y apellido son requeridos")
            return false
        }
        if (!email.contains("@")) {
            uiState = RegistroState.Error("Correo electrónico inválido")
            return false
        }
        if (pass.length < 8) {
            uiState = RegistroState.Error("La contraseña debe tener al menos 8 caracteres")
            return false
        }
        return true
    }

    fun resetState() { uiState = RegistroState.Idle }
}

sealed class RegistroState {
    object Idle : RegistroState()
    object Loading : RegistroState()
    object Success : RegistroState()
    data class Error(val mensaje: String) : RegistroState()
}
