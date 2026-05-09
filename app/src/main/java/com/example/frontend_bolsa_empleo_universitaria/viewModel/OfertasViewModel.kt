package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class OfertasViewModel(
    private val repository: OfertasRepository
) : ViewModel() {

    private val _ofertas = MutableStateFlow<List<OfertaLaboral>>(emptyList())
    val ofertas: StateFlow<List<OfertaLaboral>> = _ofertas

    private val _ofertasEmpresa = MutableStateFlow<List<OfertaLaboral>>(emptyList())
    val ofertasEmpresa: StateFlow<List<OfertaLaboral>> = _ofertasEmpresa

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _todasLasOfertas = MutableStateFlow<List<OfertaLaboral>>(emptyList())

    private val sinonimos = mapOf(
        "Diseño" to listOf("diseño", "disenador", "disenar", "design", "grafico", "ux/ui", "figma", "prototipado"),
        "Desarrollo" to listOf("desarrollador", "developer", "programacion", "programador", "software", "backend", "frontend", "fullstack", "kotlin", "java", "python", "android"),
        "Tecnología" to listOf("tecnologia", "sistemas", "infraestructura", "soporte", "redes", "informatica", "data", "analista"),
        "Marketing" to listOf("marketing", "publicidad", "digital", "community", "branding"),
        "Ventas" to listOf("ventas", "vendedor", "comercial", "negocios"),
        "TI" to listOf("tecnologia", "sistemas", "infraestructura", "soporte", "redes", "informatica", "data", "analista")
    )

    fun cargarActivas() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val datos = repository.listarActivas()
                _todasLasOfertas.value = datos
                _ofertas.value = datos
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun cargarOfertasPorEmpresa(idEmpresa: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val todas = repository.listarTodas()
                val filtradas = todas.filter { it.idEmpresa == idEmpresa }
                _ofertasEmpresa.value = filtradas
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun listarTodas() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val datos = repository.listarTodas()
                _todasLasOfertas.value = datos
                _ofertas.value = datos
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun buscarPorTexto(query: String, esEmpresa: Boolean = false) {
        val baseList = if (esEmpresa) _todasLasOfertas.value.filter { it.idEmpresa == repository.getCurrentEmpresaId() } else _todasLasOfertas.value
        
        if (query.isBlank()) {
            if (esEmpresa) _ofertasEmpresa.value = baseList else _ofertas.value = baseList
            return
        }
        
        val q = query.trim().lowercase()
        val resultado = baseList.filter { oferta ->
            oferta.titulo.lowercase().contains(q) ||
            oferta.area.lowercase().contains(q) ||
            oferta.descripcion.lowercase().contains(q) ||
            (oferta.modalidad ?: "").lowercase().contains(q)
        }
        
        if (esEmpresa) _ofertasEmpresa.value = resultado else _ofertas.value = resultado
    }

    fun filtrarPorArea(categoria: String, esEmpresa: Boolean = false) {
        val baseList = if (esEmpresa) _todasLasOfertas.value.filter { it.idEmpresa == repository.getCurrentEmpresaId() } else _todasLasOfertas.value

        if (categoria == "Todas") {
            if (esEmpresa) _ofertasEmpresa.value = baseList else _ofertas.value = baseList
            return
        }

        val palabrasClave = sinonimos[categoria] ?: listOf(categoria.lowercase())
        
        val resultado = baseList.filter { oferta ->
            val textoOferta = "${oferta.titulo} ${oferta.area} ${oferta.descripcion}".lowercase()
            palabrasClave.any { palabra -> textoOferta.contains(palabra.lowercase()) }
        }
        
        if (esEmpresa) _ofertasEmpresa.value = resultado else _ofertas.value = resultado
    }

    fun limpiarFiltros() {
        _ofertas.value = _todasLasOfertas.value
    }
}
