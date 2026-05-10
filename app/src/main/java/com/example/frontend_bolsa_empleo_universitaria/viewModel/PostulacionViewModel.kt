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
    private val api: PostulacionApi,
    private val postulacionRepository: PostulacionRepository? = null,
    private val seguimientoRepository: SeguimientoPostulacionRepository? = null
) : ViewModel() {

    // ==================== ESTADOS PARA EMPRESA (Compose State) ====================
    private val _postulacionesEmpresa = mutableStateOf<List<PostulacionDto>>(emptyList())
    val postulacionesEmpresa: State<List<PostulacionDto>> = _postulacionesEmpresa

    private val _historial = mutableStateOf<List<SeguimientoPostulacionDto>>(emptyList())
    val historial: State<List<SeguimientoPostulacionDto>> = _historial

    private val _loadingEmpresa = mutableStateOf(false)
    val loadingEmpresa: State<Boolean> = _loadingEmpresa

    private val _errorEmpresa = mutableStateOf<String?>(null)
    val errorEmpresa: State<String?> = _errorEmpresa

    private val _updating = mutableStateOf(false)
    val updating: State<Boolean> = _updating

    // ==================== ESTADOS PARA ESTUDIANTE (StateFlow) ====================
    private val _postulacionesEstudiante = MutableStateFlow<List<PostulacionResponse>>(emptyList())
    val postulacionesEstudiante: StateFlow<List<PostulacionResponse>> = _postulacionesEstudiante

    private val _loadingEstudiante = MutableStateFlow(false)
    val loadingEstudiante: StateFlow<Boolean> = _loadingEstudiante

    private val _errorEstudiante = MutableStateFlow<String?>(null)
    val errorEstudiante: StateFlow<String?> = _errorEstudiante

    // ==================== FUNCIONES PARA EMPRESA ====================
    fun cargarPostulacionesPorOferta(idOferta: Long) {
        viewModelScope.launch {
            _loadingEmpresa.value = true
            _errorEmpresa.value = null
            try {
                val result = postulacionRepository?.listarPorOferta(idOferta) ?: emptyList()
                _postulacionesEmpresa.value = result
                println("✅ Postulaciones cargadas: ${result.size}")
            } catch (e: Exception) {
                _errorEmpresa.value = "Error: ${e.message}"
                println("❌ Error cargando postulaciones: ${e.message}")
            } finally {
                _loadingEmpresa.value = false
            }
        }
    }

    fun cargarPostulacionesPorCandidato(idUsuario: Long) {
        viewModelScope.launch {
            _loadingEmpresa.value = true
            _errorEmpresa.value = null
            try {
                val result = postulacionRepository?.listarPorCandidato(idUsuario) ?: emptyList()
                _postulacionesEmpresa.value = result
                println("✅ Postulaciones del candidato cargadas: ${result.size}")
            } catch (e: Exception) {
                _errorEmpresa.value = "Error: ${e.message}"
                println("❌ Error cargando postulaciones del candidato: ${e.message}")
            } finally {
                _loadingEmpresa.value = false
            }
        }
    }

    fun cargarHistorial(idPostulacion: Long) {
        viewModelScope.launch {
            _loadingEmpresa.value = true
            _errorEmpresa.value = null
            try {
                val result = seguimientoRepository?.historialPorPostulacion(idPostulacion) ?: emptyList()
                _historial.value = result
                println("✅ Historial cargado: ${result.size} registros")
            } catch (e: Exception) {
                _errorEmpresa.value = "Error: ${e.message}"
                println("❌ Error cargando historial: ${e.message}")
            } finally {
                _loadingEmpresa.value = false
            }
        }
    }

    fun actualizarEstado(postulacion: PostulacionDto, nuevoEstado: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _updating.value = true
            _errorEmpresa.value = null
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
                val result = postulacionRepository?.actualizar(postulacion.idPostulacion, postulacionActualizada)
                if (result != null) {
                    _postulacionesEmpresa.value = _postulacionesEmpresa.value.map {
                        if (it.idPostulacion == postulacion.idPostulacion) it.copy(estado = nuevoEstado) else it
                    }
                    println("✅ Estado actualizado a: $nuevoEstado")
                    onComplete(true)
                } else {
                    _errorEmpresa.value = "Error al actualizar estado"
                    onComplete(false)
                }
            } catch (e: Exception) {
                _errorEmpresa.value = "Error: ${e.message}"
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
            try {
                val response = api.listarPorEstudiante(idUsuario)
                if (response.isSuccessful) {
                    _postulacionesEstudiante.value = response.body() ?: emptyList()
                } else {
                    _errorEstudiante.value = "Error ${response.code()}"
                }
            } catch (e: Exception) {
                _errorEstudiante.value = e.message
            } finally {
                _loadingEstudiante.value = false
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
    fun limpiarErrorEmpresa() {
        _errorEmpresa.value = null
    }

    fun limpiarPostulacionesEmpresa() {
        _postulacionesEmpresa.value = emptyList()
    }

    fun limpiarHistorial() {
        _historial.value = emptyList()
    }

    fun limpiarErrorEstudiante() {
        _errorEstudiante.value = null
    }
}