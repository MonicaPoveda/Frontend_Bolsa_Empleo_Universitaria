package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.*
import com.example.frontend_bolsa_empleo_universitaria.repository.AdminRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AdminRepository) : ViewModel() {

    private val _empresasPendientes = MutableStateFlow<List<EmpresaPendiente>>(emptyList())
    val empresasPendientes: StateFlow<List<EmpresaPendiente>> = _empresasPendientes.asStateFlow()

    private val _empresasAceptadas = MutableStateFlow<List<EmpresaDto>>(emptyList())
    val empresasAceptadas: StateFlow<List<EmpresaDto>> = _empresasAceptadas.asStateFlow()

    private val _ofertasEmpresa = MutableStateFlow<List<OfertaLaboralResponse>>(emptyList())
    val ofertasEmpresa: StateFlow<List<OfertaLaboralResponse>> = _ofertasEmpresa.asStateFlow()

    private val _postulacionesOferta = MutableStateFlow<List<PostulacionDto>>(emptyList())
    val postulacionesOferta: StateFlow<List<PostulacionDto>> = _postulacionesOferta.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private var isPollingActive = false

    init {
        iniciarActualizacionAutomatica()
    }

    private fun iniciarActualizacionAutomatica() {
        if (isPollingActive) return
        isPollingActive = true
        
        viewModelScope.launch {
            while (true) {
                actualizarDatosSilenciosamente()
                delay(30000) 
            }
        }
    }

    private suspend fun actualizarDatosSilenciosamente() {
        try {
            val respPendientes = repository.listarEmpresasPendientes()
            if (respPendientes.isSuccessful) {
                _empresasPendientes.value = respPendientes.body() ?: emptyList()
            }
            val respAceptadas = repository.listarEmpresasAceptadas()
            if (respAceptadas.isSuccessful) {
                _empresasAceptadas.value = respAceptadas.body() ?: emptyList()
            }
        } catch (e: Exception) {
            // Ignorar errores en segundo plano
        }
    }

    fun listarEmpresasPendientes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.listarEmpresasPendientes()
                if (response.isSuccessful) {
                    _empresasPendientes.value = response.body() ?: emptyList()
                } else {
                    _mensaje.value = "Error al cargar empresas pendientes"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
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
                } else {
                    _mensaje.value = "Error al aprobar empresa"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun rechazarEmpresa(id: Long, comentario: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.rechazarEmpresa(id, comentario)
                if (response.isSuccessful) {
                    _mensaje.value = "Empresa rechazada"
                    listarEmpresasPendientes()
                } else {
                    _mensaje.value = "Error al rechazar empresa"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun listarEmpresasAceptadas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.listarEmpresasAceptadas()
                if (response.isSuccessful) {
                    _empresasAceptadas.value = response.body() ?: emptyList()
                } else {
                    _mensaje.value = "Error al cargar empresas aceptadas"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun listarOfertasPorEmpresa(idEmpresa: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.listarOfertasPorEmpresa(idEmpresa)
                if (response.isSuccessful) {
                    _ofertasEmpresa.value = response.body() ?: emptyList()
                } else {
                    _mensaje.value = "Error al cargar ofertas"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun listarPostulacionesPorOferta(idOferta: Long) {
        viewModelScope.launch {
            try {
                val response = repository.listarPostulacionesPorOferta(idOferta)
                if (response.isSuccessful) {
                    _postulacionesOferta.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            }
        }
    }

    fun clearMensaje() {
        _mensaje.value = null
    }
}
