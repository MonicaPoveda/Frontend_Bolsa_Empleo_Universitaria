package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.*
import com.example.frontend_bolsa_empleo_universitaria.repository.AdminRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AdminRepository) : ViewModel() {

    private val _empresasPendientesRaw = MutableStateFlow<List<EmpresaPendiente>>(emptyList())
    
    val empresasPendientes: StateFlow<List<EmpresaPendiente>> = _empresasPendientesRaw
        .map { listaRaw ->
            listaRaw.groupBy { it.email.lowercase().trim().replace(Regex("\\s+"), "") }.map { (email, solicitudes) ->
                if (solicitudes.any { it.estado.equals("APROBADA", ignoreCase = true) }) return@map null
                val masReciente = solicitudes.maxByOrNull { it.idEmpresaPendiente } ?: solicitudes.first()
                val rechazosPorFilas = solicitudes.count { it.estado.equals("RECHAZADA", ignoreCase = true) }
                val rechazosPorCampo = solicitudes.maxOfOrNull { it.rechazos } ?: 0
                val totalRechazos = maxOf(rechazosPorFilas, rechazosPorCampo)
                val solicitudActiva = solicitudes.find { it.estado.equals("PENDIENTE", ignoreCase = true) }
                val representativa = solicitudActiva ?: masReciente

                representativa.copy(
                    rechazos = totalRechazos,
                    actualizada = solicitudActiva != null && totalRechazos > 0
                )
            }.filterNotNull().filter { it.rechazos < 3 }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _empresasAceptadasRaw = MutableStateFlow<List<EmpresaDto>>(emptyList())
    val empresasAceptadas: StateFlow<List<EmpresaDto>> = _empresasAceptadasRaw.combine(empresasPendientes) { aceptadas, pendientes ->
        val emailsEnTramite = pendientes.map { it.email.lowercase().trim().replace(Regex("\\s+"), "") }.toSet()
        aceptadas.filter { it.email?.lowercase()?.trim()?.replace(Regex("\\s+"), "") !in emailsEnTramite }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ✅ Restauradas: Propiedades de Ofertas y Postulaciones
    private val _ofertasEmpresa = MutableStateFlow<List<OfertaLaboralResponse>>(emptyList())
    val ofertasEmpresa: StateFlow<List<OfertaLaboralResponse>> = _ofertasEmpresa.asStateFlow()

    private val _postulacionesOferta = MutableStateFlow<List<PostulacionDto>>(emptyList())
    val postulacionesOferta: StateFlow<List<PostulacionDto>> = _postulacionesOferta.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    init {
        listarEmpresasPendientes()
        listarEmpresasAceptadas()
    }

    fun listarEmpresasPendientes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.listarEmpresasPendientes()
                if (response.isSuccessful) _empresasPendientesRaw.value = response.body() ?: emptyList()
            } catch (e: Exception) { _mensaje.value = "Error: ${e.message}" } finally { _isLoading.value = false }
        }
    }

    fun listarEmpresasAceptadas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.listarEmpresasAceptadas()
                if (response.isSuccessful) _empresasAceptadasRaw.value = response.body() ?: emptyList()
            } catch (e: Exception) { _mensaje.value = "Error: ${e.message}" } finally { _isLoading.value = false }
        }
    }

    // ✅ Nuevo: Eliminar empresa aceptada
    fun eliminarEmpresa(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.eliminarEmpresa(id)
                if (response.isSuccessful) {
                    _mensaje.value = "Empresa eliminada correctamente"
                    listarEmpresasAceptadas()
                } else {
                    _mensaje.value = "No se pudo eliminar la empresa"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ Restaurados: Métodos de Ofertas
    fun listarOfertasPorEmpresa(idEmpresa: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.listarOfertasPorEmpresa(idEmpresa)
                if (response.isSuccessful) _ofertasEmpresa.value = response.body() ?: emptyList()
            } catch (e: Exception) { _mensaje.value = "Error: ${e.message}" } finally { _isLoading.value = false }
        }
    }

    fun listarPostulacionesPorOferta(idOferta: Long) {
        viewModelScope.launch {
            try {
                val response = repository.listarPostulacionesPorOferta(idOferta)
                if (response.isSuccessful) _postulacionesOferta.value = response.body() ?: emptyList()
            } catch (e: Exception) { _mensaje.value = "Error: ${e.message}" }
        }
    }

    fun aprobarEmpresa(id: Long, comentario: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.aprobarEmpresa(id, comentario)
                if (response.isSuccessful) {
                    _mensaje.value = "Empresa aprobada con éxito"
                    listarEmpresasPendientes()
                    listarEmpresasAceptadas()
                }
            } catch (e: Exception) { _mensaje.value = "Error: ${e.message}" } finally { _isLoading.value = false }
        }
    }

    fun rechazarEmpresa(empresa: EmpresaPendiente, comentario: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (empresa.rechazos >= 2) {
                    val response = repository.eliminarSolicitud(empresa.idEmpresaPendiente)
                    if (response.isSuccessful) {
                        _mensaje.value = "Límite alcanzado: Empresa eliminada"
                        listarEmpresasPendientes()
                    }
                } else {
                    val response = repository.rechazarEmpresa(empresa.idEmpresaPendiente, comentario)
                    if (response.isSuccessful) {
                        _mensaje.value = "Rechazo procesado (${empresa.rechazos + 1}/3)"
                        listarEmpresasPendientes()
                    }
                }
            } catch (e: Exception) { _mensaje.value = "Error de red" } finally { _isLoading.value = false }
        }
    }

    fun clearMensaje() { _mensaje.value = null }
}
