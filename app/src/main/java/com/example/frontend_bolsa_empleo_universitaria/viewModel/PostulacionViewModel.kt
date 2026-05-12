package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.interfaces.PostulacionApi
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionRequest
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionResponse
import com.example.frontend_bolsa_empleo_universitaria.model.SeguimientoPostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.repository.PostulacionRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.SeguimientoPostulacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class PostulacionViewModel(
    private val postulacionRepository: PostulacionRepository,
    private val seguimientoRepository: SeguimientoPostulacionRepository,
    private val api: PostulacionApi
) : ViewModel() {

    // ==================== PARA EMPRESA (Compose State) ====================

    private val _postulaciones = mutableStateOf<List<PostulacionDto>>(emptyList())
    val postulaciones: State<List<PostulacionDto>> = _postulaciones

    private val _historial = mutableStateOf<List<SeguimientoPostulacionDto>>(emptyList())
    val historial: State<List<SeguimientoPostulacionDto>> = _historial

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _updating = mutableStateOf(false)
    val updating: State<Boolean> = _updating


    // ==================== PARA ESTUDIANTE (StateFlow) ====================

    private val _postulacionesEstudiante = MutableStateFlow<List<PostulacionResponse>>(emptyList())
    val postulacionesEstudiante: StateFlow<List<PostulacionResponse>> = _postulacionesEstudiante

    private val _loadingEstudiante = MutableStateFlow(false)
    val loadingEstudiante: StateFlow<Boolean> = _loadingEstudiante

    private val _errorEstudiante = MutableStateFlow<String?>(null)
    val errorEstudiante: StateFlow<String?> = _errorEstudiante


    // ==================== FUNCIONES PARA EMPRESA ====================

    // Cargar postulaciones por oferta (para empresa)
    fun cargarPostulacionesPorOferta(idOferta: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = postulacionRepository.listarPorOferta(idOferta)
                _postulaciones.value = result
                println("✅ Postulaciones cargadas: ${result.size}")
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                println("❌ Error cargando postulaciones: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    // Cargar postulaciones por candidato (estudiante) - para empresa
    fun cargarPostulacionesPorCandidato(idUsuario: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = postulacionRepository.listarPorCandidato(idUsuario)
                _postulaciones.value = result
                println("✅ Postulaciones del candidato cargadas: ${result.size}")
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                println("❌ Error cargando postulaciones del candidato: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    // Cargar historial de una postulación
    fun cargarHistorial(idPostulacion: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = seguimientoRepository.historialPorPostulacion(idPostulacion)
                _historial.value = result
                println("✅ Historial cargado: ${result.size} registros")
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                println("❌ Error cargando historial: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    // Actualizar estado de una postulación (para empresa)
    fun actualizarEstado(postulacion: PostulacionDto, nuevoEstado: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _updating.value = true
            _error.value = null
            try {
                val postulacionActualizada = PostulacionDto(
                    idPostulacion = postulacion.idPostulacion,
                    fechaPostulacion = postulacion.fechaPostulacion,
                    estado = nuevoEstado,
                    idUsuario = postulacion.idUsuario,
                    idOferta = postulacion.idOferta,
                    nombreEstudiante = "",
                    emailEstudiante = ""
                )

                val result = postulacionRepository.actualizar(postulacion.idPostulacion, postulacionActualizada)
                if (result != null) {
                    _postulaciones.value = _postulaciones.value.map {
                        if (it.idPostulacion == postulacion.idPostulacion) {
                            it.copy(estado = nuevoEstado)
                        } else it
                    }
                    println("✅ Estado actualizado a: $nuevoEstado")
                    onComplete(true)
                } else {
                    _error.value = "Error al actualizar estado"
                    onComplete(false)
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                println("❌ Error actualizando estado: ${e.message}")
                onComplete(false)
            } finally {
                _updating.value = false
            }
        }
    }


    // ==================== FUNCIONES PARA ESTUDIANTE ====================

    fun cargarPostulacionesEstudiante(idUsuario: Long) {
        viewModelScope.launch {
            _loadingEstudiante.value = true
            _errorEstudiante.value = null
            try {
                println("📤 Cargando postulaciones para estudiante ID: $idUsuario")
                // ✅ Usar listarPorCandidato (endpoint correcto)
                val response = api.listarPorCandidato(idUsuario)
                if (response.isSuccessful) {
                    val postulaciones = response.body() ?: emptyList()
                    println("✅ Postulaciones encontradas: ${postulaciones.size}")
                    postulaciones.forEach { p ->
                        println("   - ID: ${p.idPostulacion}, Oferta: ${p.idOferta}, Estado: ${p.estado}")
                    }
                    // Convertir PostulacionDto a PostulacionResponse
                    val postulacionesResponse = postulaciones.map { dto ->
                        PostulacionResponse(
                            idPostulacion = dto.idPostulacion,
                            fechaPostulacion = dto.fechaPostulacion,
                            estado = dto.estado,
                            idUsuario = dto.idUsuario,
                            idOferta = dto.idOferta
                        )
                    }
                    _postulacionesEstudiante.value = postulacionesResponse
                } else {
                    val errorMsg = "Error ${response.code()}: ${response.message()}"
                    println("❌ $errorMsg")
                    _errorEstudiante.value = errorMsg
                }
            } catch (e: Exception) {
                println("❌ Excepción: ${e.message}")
                _errorEstudiante.value = e.message
            } finally {
                _loadingEstudiante.value = false
            }
        }
    }

    fun eliminarPostulacion(idPostulacion: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.eliminar(idPostulacion)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error al eliminar la postulación: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            }
        }
    }

    // Postularse a una oferta (estudiante)
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


    // ==================== FUNCIONES DE LIMPIEZA ====================

    fun limpiarError() {
        _error.value = null
    }

    fun limpiarPostulaciones() {
        _postulaciones.value = emptyList()
    }

    fun limpiarHistorial() {
        _historial.value = emptyList()
    }

    fun limpiarErrorEstudiante() {
        _errorEstudiante.value = null
    }


    // ==================== UTILIDADES ====================

    private fun obtenerFechaActual(): String {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }
}