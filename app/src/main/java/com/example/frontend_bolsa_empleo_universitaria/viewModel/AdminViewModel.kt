package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.*
import com.example.frontend_bolsa_empleo_universitaria.repository.AdminRepository
import com.example.frontend_bolsa_empleo_universitaria.ui.components.AdminMessageState
import com.example.frontend_bolsa_empleo_universitaria.ui.components.AdminMessageType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AdminRepository) : ViewModel() {

    private val _empresasPendientesRaw = MutableStateFlow<List<EmpresaPendiente>>(emptyList())
    private val _empresasAceptadasRaw = MutableStateFlow<List<EmpresaDto>>(emptyList())
    private val _ofertasEmpresaRaw = MutableStateFlow<List<OfertaLaboralResponse>>(emptyList())
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _adminMessage = MutableStateFlow(AdminMessageState())
    val adminMessage: StateFlow<AdminMessageState> = _adminMessage.asStateFlow()

    fun showAdminMessage(message: String, type: AdminMessageType = AdminMessageType.INFO) {
        _adminMessage.value = AdminMessageState(message, type, true)
    }

    fun dismissAdminMessage() {
        _adminMessage.value = _adminMessage.value.copy(visible = false)
    }

    val empresasPendientes: StateFlow<List<EmpresaPendiente>> = _empresasPendientesRaw
        .combine(_empresasAceptadasRaw) { pendientesRaw, aceptadasRaw ->
            if (pendientesRaw.isEmpty()) return@combine emptyList()
            pendientesRaw.groupBy { it.email.lowercase().trim() }
                .mapNotNull { (email, solicitudes) ->
                    val solicitudActiva = solicitudes.find { it.estado.equals("PENDIENTE", ignoreCase = true) }
                    val masReciente = solicitudes.maxByOrNull { it.idEmpresaPendiente } ?: solicitudes.first()
                    if (solicitudActiva == null && masReciente.estado.equals("APROBADA", ignoreCase = true)) return@mapNotNull null
                    val totalRechazos = maxOf(solicitudes.count { it.estado.equals("RECHAZADA", ignoreCase = true) }, solicitudes.maxOfOrNull { it.rechazos } ?: 0)
                    if (totalRechazos >= 3 && solicitudActiva == null) return@mapNotNull null
                    val base = solicitudActiva ?: masReciente
                    val matchingAceptada = aceptadasRaw.find { it.email?.lowercase()?.trim() == email }
                    base.copy(
                        rechazos = totalRechazos,
                        actualizada = solicitudActiva != null && totalRechazos > 0,
                        sector = base.sector?.takeIf { it.isNotBlank() } ?: matchingAceptada?.sector,
                        telefono = base.telefono?.takeIf { it.isNotBlank() } ?: matchingAceptada?.telefono,
                        ciudad = base.ciudad?.takeIf { it.isNotBlank() } ?: matchingAceptada?.ciudad,
                        descripcion = base.descripcion?.takeIf { it.isNotBlank() } ?: matchingAceptada?.descripcion
                    )
                }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val empresasAceptadas: StateFlow<List<EmpresaDto>> = _empresasAceptadasRaw.asStateFlow()
    val ofertasEmpresa: StateFlow<List<OfertaLaboralResponse>> = _ofertasEmpresaRaw.asStateFlow()

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
            } catch (e: Exception) { } finally { _isLoading.value = false }
        }
    }

    fun listarEmpresasAceptadas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.listarEmpresasAceptadas()
                if (response.isSuccessful) _empresasAceptadasRaw.value = response.body() ?: emptyList()
            } catch (e: Exception) { } finally { _isLoading.value = false }
        }
    }

    fun aprobarEmpresa(id: Long, comentario: String? = null) {
        viewModelScope.launch {
            // NOTIFICACIÓN INMEDIATA
            showAdminMessage("Aprobando empresa...", AdminMessageType.INFO)
            _isLoading.value = true
            try {
                val response = repository.aprobarEmpresa(id, comentario)
                if (response.isSuccessful) {
                    showAdminMessage("Empresa aprobada con éxito", AdminMessageType.SUCCESS)
                    launch { listarEmpresasPendientes(); listarEmpresasAceptadas() }
                } else {
                    showAdminMessage("Error al aprobar", AdminMessageType.ERROR)
                }
            } catch (e: Exception) {
                showAdminMessage("Fallo de conexión", AdminMessageType.ERROR)
            } finally { _isLoading.value = false }
        }
    }

    fun rechazarEmpresa(empresa: EmpresaPendiente, comentario: String?) {
        if (comentario.isNullOrBlank()) {
            showAdminMessage("Motivo de rechazo obligatorio", AdminMessageType.WARNING)
            return
        }
        viewModelScope.launch {
            showAdminMessage("Procesando rechazo...", AdminMessageType.INFO)
            _isLoading.value = true
            try {
                val response = if (empresa.rechazos >= 2) repository.eliminarSolicitud(empresa.idEmpresaPendiente)
                               else repository.rechazarEmpresa(empresa.idEmpresaPendiente, comentario)

                if (response.isSuccessful) {
                    showAdminMessage("Solicitud rechazada correctamente", AdminMessageType.SUCCESS)
                    launch { listarEmpresasPendientes() }
                } else {
                    showAdminMessage("No se pudo rechazar", AdminMessageType.ERROR)
                }
            } catch (e: Exception) {
                showAdminMessage("Error de red", AdminMessageType.ERROR)
            } finally { _isLoading.value = false }
        }
    }

    fun eliminarEmpresa(id: Long) {
        viewModelScope.launch {
            showAdminMessage("Eliminando empresa...", AdminMessageType.INFO)
            _isLoading.value = true
            try {
                val response = repository.eliminarEmpresa(id)
                if (response.isSuccessful) {
                    showAdminMessage("Empresa eliminada del directorio", AdminMessageType.SUCCESS)
                    launch { listarEmpresasAceptadas() }
                } else {
                    showAdminMessage("Error al eliminar empresa", AdminMessageType.ERROR)
                }
            } catch (e: Exception) {
                showAdminMessage("Fallo de conexión", AdminMessageType.ERROR)
            } finally { _isLoading.value = false }
        }
    }

    fun listarOfertasPorEmpresa(idEmpresa: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.listarOfertasPorEmpresa(idEmpresa)
                if (response.isSuccessful) {
                    _ofertasEmpresaRaw.value = response.body() ?: emptyList()
                } else {
                    _ofertasEmpresaRaw.value = emptyList()
                }
            } catch (e: Exception) {
                _ofertasEmpresaRaw.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
