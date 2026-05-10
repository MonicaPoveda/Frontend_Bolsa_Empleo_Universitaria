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
import com.example.frontend_bolsa_empleo_universitaria.repository.PostulacionesRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.SeguimientoPostulacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class PostulacionViewModel(
    // Para estudiante (usando API directa)
    private val api: PostulacionApi? = null,
    // Para empresa (usando repositorios)
    private val postulacionesRepository: PostulacionesRepository? = null,
    private val seguimientoRepository: SeguimientoPostulacionRepository? = null
) : ViewModel() {

    // Estados para estudiante
    private val _postulaciones = MutableStateFlow<List<PostulacionResponse>>(emptyList())
    val postulaciones: StateFlow<List<PostulacionResponse>> = _postulaciones

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Estados para empresa
    private val _postulacionesEmpresa = mutableStateOf<List<PostulacionDto>>(emptyList())
    val postulacionesEmpresa: State<List<PostulacionDto>> = _postulacionesEmpresa

    private val _historial = mutableStateOf<List<SeguimientoPostulacionDto>>(emptyList())
    val historial: State<List<SeguimientoPostulacionDto>> = _historial

    private val _updating = mutableStateOf(false)
    val updating: State<Boolean> = _updating

    // ================== FUNCIONES PARA ESTUDIANTE ==================
    fun cargarPostulaciones(idUsuario: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = api?.listarPorEstudiante(idUsuario)
                if (response?.isSuccessful == true) {
                    _postulaciones.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error ${response?.code() ?: "desconocido"}"
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
                val response = api?.postularse(request)
                if (response?.isSuccessful == true) {
                    onSuccess()
                } else {
                    val errorBody = response?.errorBody()?.string()
                    val errorMsg = try {
                        JSONObject(errorBody).optString("message", "")
                    } catch (e: Exception) {
                        errorBody ?: ""
                    }
                    val finalMessage = when {
                        response?.code() == 403 || response?.code() == 409 || response?.code() == 400 ||
                                errorMsg.contains("ya existe", ignoreCase = true) ||
                                errorMsg.contains("duplicate", ignoreCase = true) ||
                                errorMsg.contains("postulado", ignoreCase = true) ||
                                errorMsg.contains("403") -> {
                            "Ya estás postulado a esta oferta, no puedes volver a postularte."
                        }
                        errorMsg.isBlank() -> "Error ${response?.code() ?: ""}: No se pudo completar la postulación"
                        else -> errorMsg
                    }
                    onError(finalMessage)
                }
            } catch (e: Exception) {
                onError("Error de conexión: No se pudo procesar la postulación")
            }
        }
    }

    // ================== FUNCIONES PARA EMPRESA ==================
    fun cargarPostulacionesPorOferta(idOferta: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = postulacionesRepository?.listarPorOferta(idOferta) ?: emptyList()
                _postulacionesEmpresa.value = result
                println("✅ Postulaciones cargadas: ${result.size}")
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                println("❌ Error cargando postulaciones: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun cargarPostulacionesPorCandidato(idUsuario: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = postulacionesRepository?.listarPorCandidato(idUsuario) ?: emptyList()
                _postulacionesEmpresa.value = result
                println("✅ Postulaciones del candidato cargadas: ${result.size}")
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                println("❌ Error cargando postulaciones del candidato: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun cargarHistorial(idPostulacion: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = seguimientoRepository?.historialPorPostulacion(idPostulacion) ?: emptyList()
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
                val result = postulacionesRepository?.actualizar(postulacion.idPostulacion, postulacionActualizada)
                if (result != null) {
                    _postulacionesEmpresa.value = _postulacionesEmpresa.value.map {
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

    fun crearPostulacion(idOferta: Long, idUsuario: Long, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val nuevaPostulacion = PostulacionDto(
                    idPostulacion = 0,
                    fechaPostulacion = obtenerFechaActual(),
                    estado = "PENDIENTE",
                    idUsuario = idUsuario,
                    idOferta = idOferta,
                    nombreEstudiante = "",
                    emailEstudiante = ""
                )
                val result = postulacionesRepository?.guardar(nuevaPostulacion)
                if (result != null) {
                    println("✅ Postulación creada exitosamente")
                    onComplete(true)
                } else {
                    _error.value = "Error al crear postulación"
                    onComplete(false)
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                println("❌ Error creando postulación: ${e.message}")
                onComplete(false)
            } finally {
                _loading.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun limpiarPostulaciones() {
        _postulacionesEmpresa.value = emptyList()
    }

    fun limpiarHistorial() {
        _historial.value = emptyList()
    }

    private fun obtenerFechaActual(): String {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }
}