package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.interfaces.PostulacionApi
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionRequest
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
                    val lista = response.body() ?: emptyList()
                    _postulaciones.value = lista
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    _error.value = when (response.code()) {
                        403 -> "Error de Autorización (403): El servidor no reconoce tu identidad correctamente. Contacta al soporte técnico."
                        404 -> "No se encontraron postulaciones."
                        else -> "Error del servidor: ${response.code()}"
                    }
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
                    val errorMsg = response.errorBody()?.string() ?: "Error ${response.code()}"
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error de red")
            }
        }
    }
}