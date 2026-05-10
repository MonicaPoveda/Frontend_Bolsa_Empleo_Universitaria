package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.interfaces.PostulacionApi
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionRequest
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class PostulacionViewModel(
    private val api: PostulacionApi
) : ViewModel() {

    private val _postulaciones = MutableStateFlow<List<PostulacionResponse>>(emptyList())
    val postulaciones: StateFlow<List<PostulacionResponse>> = _postulaciones

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarPostulaciones(idUsuario: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = api.listarPorEstudiante(idUsuario)
                if (response.isSuccessful) {
                    _postulaciones.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }



    fun postularse(idUsuario: Long, idOferta: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val request = PostulacionRequest(idUsuario, idOferta)
                val response = api.postularse(request)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        JSONObject(errorBody).optString("message", "")
                    } catch (e: Exception) {
                        errorBody ?: ""
                    }

                    // Personalizar el mensaje según el requerimiento: 403, 409 o 400 suelen indicar duplicidad
                    val finalMessage = when {
                        response.code() == 403 || response.code() == 409 || response.code() == 400 ||
                        errorMsg.contains("ya existe", ignoreCase = true) || 
                        errorMsg.contains("duplicate", ignoreCase = true) ||
                        errorMsg.contains("postulado", ignoreCase = true) ||
                        errorMsg.contains("403") -> {
                            "Ya estás postulado a esta oferta, no puedes volver a postularte."
                        }
                        errorMsg.isBlank() -> "Error ${response.code()}: No se pudo completar la postulación"
                        else -> errorMsg
                    }
                    
                    onError(finalMessage)
                }
            } catch (e: Exception) {
                onError("Error de conexión: No se pudo procesar la postulación")
            }
        }
    }
}