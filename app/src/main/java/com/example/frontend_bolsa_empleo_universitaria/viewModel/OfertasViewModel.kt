package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralRequest
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralResponse
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaSearchFilters
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class OfertasViewModel(
    private val repository: OfertasRepository,
    private val empresaRepository: EmpresaRepository? = null
) : ViewModel() {

    private val _ofertas = mutableStateOf<List<OfertaLaboralResponse>>(emptyList())
    val ofertas: State<List<OfertaLaboralResponse>> = _ofertas

    private val _ofertasEmpresa = mutableStateOf<List<OfertaLaboralResponse>>(emptyList())
    val ofertasEmpresa: State<List<OfertaLaboralResponse>> = _ofertasEmpresa

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _todasLasOfertas = mutableStateOf<List<OfertaLaboralResponse>>(emptyList())

    private val _empresaNombrePorId = mutableStateOf<Map<Long, String>>(emptyMap())
    val empresaNombrePorId: State<Map<Long, String>> = _empresaNombrePorId

    private val _searchFilters = mutableStateOf(OfertaSearchFilters())
    val searchFilters: State<OfertaSearchFilters> = _searchFilters

    private val sinonimos = mapOf(
        "Diseño" to listOf("diseño", "disenador", "disenar", "design", "grafico", "ux/ui", "figma", "prototipado"),
        "Desarrollo" to listOf("desarrollador", "developer", "programacion", "programador", "software", "backend", "frontend", "fullstack", "kotlin", "java", "python", "android"),
        "Marketing" to listOf("marketing", "publicidad", "digital", "community", "branding"),
        "Ventas" to listOf("ventas", "vendedor", "comercial", "negocios"),
        "TI" to listOf("tecnologia", "sistemas", "infraestructura", "soporte", "redes", "informatica", "data", "analista")
    )

    fun setSearchFilters(filters: OfertaSearchFilters) {
        _searchFilters.value = filters
        aplicarFiltrosInternos()
    }

    fun updateSearchFilters(transform: (OfertaSearchFilters) -> OfertaSearchFilters) {
        _searchFilters.value = transform(_searchFilters.value)
        aplicarFiltrosInternos()
    }

    private suspend fun cargarMapaEmpresas() {
        val repo = empresaRepository ?: return
        try {
            val empresas = repo.listarEmpresas()
            _empresaNombrePorId.value = empresas.associate { it.idEmpresa to (it.nombre ?: "").trim() }
        } catch (_: Exception) {
            _empresaNombrePorId.value = emptyMap()
        }
    }

    private fun aplicarFiltrosInternos() {
        val filters = _searchFilters.value
        var list = _todasLasOfertas.value
        val empMap = _empresaNombrePorId.value

        if (filters.categoria != "Todas") {
            val palabras = sinonimos[filters.categoria] ?: listOf(filters.categoria.lowercase())
            list = list.filter { oferta ->
                val texto = "${oferta.titulo} ${oferta.area} ${oferta.descripcion}".lowercase()
                palabras.any { texto.contains(it.lowercase()) }
            }
        }

        if (filters.textoLibre.isNotBlank()) {
            val q = filters.textoLibre.trim().lowercase()
            list = list.filter { oferta ->
                val empresa = empMap[oferta.idEmpresa]?.lowercase().orEmpty()
                oferta.titulo.lowercase().contains(q) ||
                    oferta.area.lowercase().contains(q) ||
                    oferta.descripcion.lowercase().contains(q) ||
                    oferta.modalidad.lowercase().contains(q) ||
                    empresa.contains(q)
            }
        }

        if (filters.nombreEmpresa.isNotBlank()) {
            val q = filters.nombreEmpresa.trim().lowercase()
            list = list.filter { oferta ->
                empMap[oferta.idEmpresa]?.lowercase()?.contains(q) == true
            }
        }

        if (filters.cargo.isNotBlank()) {
            val q = filters.cargo.trim().lowercase()
            list = list.filter { it.titulo.lowercase().contains(q) }
        }

        if (filters.carrera.isNotBlank()) {
            val q = filters.carrera.trim().lowercase()
            list = list.filter {
                it.area.lowercase().contains(q) || it.descripcion.lowercase().contains(q)
            }
        }

        if (filters.oficio.isNotBlank()) {
            val q = filters.oficio.trim().lowercase()
            list = list.filter {
                it.titulo.lowercase().contains(q) ||
                    it.descripcion.lowercase().contains(q) ||
                    it.area.lowercase().contains(q)
            }
        }

        filters.salarioMin?.let { min ->
            list = list.filter { it.salario >= min }
        }
        filters.salarioMax?.let { max ->
            list = list.filter { it.salario <= max }
        }

        val mod = filters.modalidad
        if (!mod.isNullOrBlank() && mod != "Todas") {
            list = list.filter { it.modalidad.contains(mod, ignoreCase = true) }
        }

        _ofertas.value = list
    }

    fun cargarActivas() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val datos = repository.listarActivas()
                _todasLasOfertas.value = datos
                cargarMapaEmpresas()
                aplicarFiltrosInternos()
            } catch (e: HttpException) {
                _error.value = "Error de conexión: ${e.code()}"
            } catch (e: IOException) {
                _error.value = "Error de red: verifica tu conexión"
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
            } catch (e: HttpException) {
                _error.value = "Error de conexión: ${e.code()}"
            } catch (e: IOException) {
                _error.value = "Error de red: verifica tu conexión"
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun cargarOfertasPorEmpresaSilent(idEmpresa: Long) {
        viewModelScope.launch {
            try {
                val todas = repository.listarTodas()
                val filtradas = todas.filter { it.idEmpresa == idEmpresa }
                _ofertasEmpresa.value = filtradas
                _ofertas.value = _ofertas.value.map { oferta ->
                    filtradas.find { it.idOferta == oferta.idOferta } ?: oferta
                }
                _todasLasOfertas.value = _todasLasOfertas.value.map { oferta ->
                    filtradas.find { it.idOferta == oferta.idOferta } ?: oferta
                }
            } catch (_: Exception) { }
        }
    }

    fun listarTodas() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val datos = repository.listarTodas()
                _todasLasOfertas.value = datos
                cargarMapaEmpresas()
                aplicarFiltrosInternos()
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    /** Compatibilidad: solo actualiza categoría y reaplica el resto de filtros. */
    fun filtrarPorCategoria(categoria: String) {
        updateSearchFilters { it.copy(categoria = categoria) }
    }

    /** Compatibilidad: búsqueda libre principal. */
    fun buscarGeneral(query: String) {
        updateSearchFilters { it.copy(textoLibre = query) }
    }

    fun obtenerOfertaPorId(idOferta: Long): OfertaLaboralResponse? {
        return _ofertasEmpresa.value.find { it.idOferta == idOferta }
            ?: _todasLasOfertas.value.find { it.idOferta == idOferta }
            ?: _ofertas.value.find { it.idOferta == idOferta }
    }

    fun actualizarEstadoOferta(idOferta: Long, nuevoEstado: Boolean) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val ofertaActual = _ofertasEmpresa.value.find { it.idOferta == idOferta }
                    ?: run {
                        _error.value = "Oferta no encontrada"
                        return@launch
                    }

                val ofertaRequest = OfertaLaboralRequest(
                    titulo = ofertaActual.titulo,
                    descripcion = ofertaActual.descripcion,
                    area = ofertaActual.area,
                    salario = ofertaActual.salario,
                    modalidad = ofertaActual.modalidad,
                    fechaPublicacion = ofertaActual.fechaPublicacion,
                    fechaCierre = ofertaActual.fechaCierre,
                    estado = nuevoEstado,
                    idEmpresa = ofertaActual.idEmpresa
                )

                val resultado = repository.actualizarOferta(idOferta, ofertaRequest)

                if (resultado != null) {
                    _ofertasEmpresa.value = _ofertasEmpresa.value.map {
                        if (it.idOferta == idOferta) resultado else it
                    }
                    _ofertas.value = _ofertas.value.map {
                        if (it.idOferta == idOferta) resultado else it
                    }
                    _todasLasOfertas.value = _todasLasOfertas.value.map {
                        if (it.idOferta == idOferta) resultado else it
                    }
                    aplicarFiltrosInternos()
                } else {
                    _error.value = "Error al actualizar la oferta"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun eliminarOferta(idOferta: Long, idEmpresa: Long, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val resultado = repository.eliminarOferta(idOferta)
                if (resultado) {
                    _ofertasEmpresa.value = _ofertasEmpresa.value.filter { it.idOferta != idOferta }
                    _ofertas.value = _ofertas.value.filter { it.idOferta != idOferta }
                    _todasLasOfertas.value = _todasLasOfertas.value.filter { it.idOferta != idOferta }
                    aplicarFiltrosInternos()
                    onComplete(true)
                } else {
                    _error.value = "Error al eliminar la oferta"
                    onComplete(false)
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                onComplete(false)
            } finally {
                _loading.value = false
            }
        }
    }
}
