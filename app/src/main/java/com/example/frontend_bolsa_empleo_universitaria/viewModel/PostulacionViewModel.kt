package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.model.SeguimientoPostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.repository.PostulacionRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.SeguimientoPostulacionRepository
import kotlinx.coroutines.launch

class PostulacionViewModel(
    private val postulacionRepository: PostulacionRepository,
    private val seguimientoRepository: SeguimientoPostulacionRepository
) : ViewModel() {

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

    // Cargar postulaciones por oferta
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

    // Cargar postulaciones por candidato (estudiante)
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

    // Actualizar estado de una postulación
    fun actualizarEstado(postulacion: PostulacionDto, nuevoEstado: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _updating.value = true
            _error.value = null
            try {
                val updatedPostulacion = postulacion.copy(estado = nuevoEstado)
                val result = postulacionRepository.actualizar(postulacion.idPostulacion, updatedPostulacion)
                if (result != null) {
                    // Actualizar la lista local
                    _postulaciones.value = _postulaciones.value.map {
                        if (it.idPostulacion == postulacion.idPostulacion) result else it
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

    // Crear nueva postulación (estudiante se postula)
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
                    idOferta = idOferta
                )
                val result = postulacionRepository.guardar(nuevaPostulacion)
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

    // Limpiar error
    fun limpiarError() {
        _error.value = null
    }

    // Limpiar postulaciones
    fun limpiarPostulaciones() {
        _postulaciones.value = emptyList()
    }

    // Limpiar historial
    fun limpiarHistorial() {
        _historial.value = emptyList()
    }

    private fun obtenerFechaActual(): String {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }
}