package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class OfertasViewModel(
    private val repository: OfertasRepository
) : ViewModel() {

    private val _ofertas = mutableStateOf<List<OfertaLaboral>>(emptyList())
    val ofertas: State<List<OfertaLaboral>> = _ofertas

    private val _ofertasEmpresa = mutableStateOf<List<OfertaLaboral>>(emptyList())
    val ofertasEmpresa: State<List<OfertaLaboral>> = _ofertasEmpresa

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _todasLasOfertas = mutableStateOf<List<OfertaLaboral>>(emptyList())

    // Sinónimos para filtrado por área
    private val sinonimos = mapOf(
        "Diseño" to listOf("diseño", "disenador", "disenar", "design", "grafico", "ux/ui", "figma", "prototipado"),
        "Desarrollo" to listOf("desarrollador", "developer", "programacion", "programador", "software", "backend", "frontend", "fullstack", "kotlin", "java", "python", "android"),
        "Marketing" to listOf("marketing", "publicidad", "digital", "community", "branding"),
        "Ventas" to listOf("ventas", "vendedor", "comercial", "negocios"),
        "TI" to listOf("tecnologia", "sistemas", "infraestructura", "soporte", "redes", "informatica", "data", "analista")
    )

    fun cargarActivas() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                println("Cargando ofertas activas...")
                val datos = repository.listarActivas()
                println("Ofertas recibidas: ${datos.size}")
                datos.forEach { oferta ->
                    println("Oferta: ${oferta.titulo} - ${oferta.area}")
                }
                _todasLasOfertas.value = datos
                _ofertas.value = datos
            } catch (e: HttpException) {
                println("Error HTTP: ${e.code()} - ${e.message()}")
                _error.value = "Error de conexión: ${e.code()}"
            } catch (e: IOException) {
                println("Error de red: ${e.message}")
                _error.value = "Error de red: Verifica tu conexión"
            } catch (e: Exception) {
                println("Error inesperado: ${e.message}")
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // NUEVO: Cargar ofertas de una empresa específica (filtrado en frontend)
    fun cargarOfertasPorEmpresa(idEmpresa: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                println("Cargando ofertas para empresa ID: $idEmpresa")
                val todas = repository.listarTodas()
                println("Total de ofertas en backend: ${todas.size}")

                // Filtrar solo las ofertas de esta empresa
                val filtradas = todas.filter { it.idEmpresa == idEmpresa }
                println("Ofertas filtradas para empresa $idEmpresa: ${filtradas.size}")

                filtradas.forEach { oferta ->
                    println("  - ${oferta.titulo} (ID: ${oferta.idOferta})")
                }

                _ofertasEmpresa.value = filtradas
            } catch (e: HttpException) {
                println("Error HTTP: ${e.code()} - ${e.message()}")
                _error.value = "Error de conexión: ${e.code()}"
            } catch (e: IOException) {
                println("Error de red: ${e.message}")
                _error.value = "Error de red: Verifica tu conexión"
            } catch (e: Exception) {
                println("Error inesperado: ${e.message}")
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
                println("Error: ${e.message}")
                _error.value = "Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun filtrarPorCategoria(categoria: String) {
        println("Filtrando por: $categoria")
        if (categoria == "Todas") {
            _ofertas.value = _todasLasOfertas.value
            return
        }

        val palabrasClave = sinonimos[categoria] ?: listOf(categoria.lowercase())
        println("Palabras clave: $palabrasClave")

        val resultado = _todasLasOfertas.value.filter { oferta ->
            val textoOferta = "${oferta.titulo} ${oferta.area} ${oferta.descripcion}".lowercase()
            val coincide = palabrasClave.any { palabra ->
                textoOferta.contains(palabra.lowercase())
            }
            if (coincide) println("Coincide: ${oferta.titulo}")
            coincide
        }
        println("Resultados: ${resultado.size}")
        _ofertas.value = resultado
    }

    fun buscarGeneral(query: String) {
        println("Buscando: $query")
        if (query.isBlank()) {
            _ofertas.value = _todasLasOfertas.value
            return
        }
        val q = query.trim().lowercase()
        val resultado = _todasLasOfertas.value.filter { oferta ->
            oferta.titulo.lowercase().contains(q) ||
                    oferta.area.lowercase().contains(q) ||
                    oferta.descripcion.lowercase().contains(q) ||
                    oferta.modalidad.lowercase().contains(q)
        }
        println("Resultados búsqueda: ${resultado.size}")
        _ofertas.value = resultado
    }
    // Buscar una oferta específica por ID de la lista ya cargada
    fun obtenerOfertaPorId(idOferta: Long): OfertaLaboral? {
        return _ofertasEmpresa.value.find { it.idOferta == idOferta }
    }

    // Actualizar estado de una oferta (activar/cerrar)
    fun actualizarEstadoOferta(idOferta: Long, nuevoEstado: Boolean) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val ofertaActual = _ofertasEmpresa.value.find { it.idOferta == idOferta }
                    ?: return@launch

                val ofertaActualizada = ofertaActual.copy(estado = nuevoEstado)
                val resultado = repository.actualizarOferta(idOferta, ofertaActualizada)

                if (resultado != null) {
                    // Actualizar la lista local sin recargar todo
                    _ofertasEmpresa.value = _ofertasEmpresa.value.map {
                        if (it.idOferta == idOferta) resultado else it
                    }
                    println("✅ Oferta $idOferta actualizada a estado: $nuevoEstado")
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
}