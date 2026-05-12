package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralRequest
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralResponse
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class OfertasViewModel(
    private val repository: OfertasRepository
) : ViewModel() {

    // ✅ Cambiar a OfertaLaboralResponse (tiene idOferta)
    private val _ofertas = mutableStateOf<List<OfertaLaboralResponse>>(emptyList())
    val ofertas: State<List<OfertaLaboralResponse>> = _ofertas

    // ✅ Cambiar a OfertaLaboralResponse (tiene idOferta)
    private val _ofertasEmpresa = mutableStateOf<List<OfertaLaboralResponse>>(emptyList())
    val ofertasEmpresa: State<List<OfertaLaboralResponse>> = _ofertasEmpresa

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // ✅ Cambiar a OfertaLaboralResponse
    private val _todasLasOfertas = mutableStateOf<List<OfertaLaboralResponse>>(emptyList())

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
                    println("Oferta: ${oferta.titulo} - ${oferta.area} - ID: ${oferta.idOferta}")
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

    // Cargar ofertas de una empresa específica
    fun cargarOfertasPorEmpresa(idEmpresa: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                println("Cargando ofertas para empresa ID: $idEmpresa")
                val todas = repository.listarTodas()
                println("Total de ofertas en backend: ${todas.size}")

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

    // ✅ NUEVO MÉTODO: Carga silenciosa (sin indicador de carga)
    fun cargarOfertasPorEmpresaSilent(idEmpresa: Long) {
        viewModelScope.launch {
            // No modificamos _loading.value para no mostrar indicador de carga
            try {
                println("🔄 Recarga silenciosa de ofertas para empresa ID: $idEmpresa")
                val todas = repository.listarTodas()
                val filtradas = todas.filter { it.idEmpresa == idEmpresa }

                _ofertasEmpresa.value = filtradas

                // También actualizar _ofertas y _todasLasOfertas si contienen ofertas de esta empresa
                _ofertas.value = _ofertas.value.map { oferta ->
                    filtradas.find { it.idOferta == oferta.idOferta } ?: oferta
                }
                _todasLasOfertas.value = _todasLasOfertas.value.map { oferta ->
                    filtradas.find { it.idOferta == oferta.idOferta } ?: oferta
                }

                println("✅ Recarga silenciosa completada: ${filtradas.size} ofertas")
            } catch (e: HttpException) {
                println("⚠️ Error en recarga silenciosa (HTTP): ${e.code()} - ${e.message()}")
            } catch (e: IOException) {
                println("⚠️ Error en recarga silenciosa (Red): ${e.message}")
            } catch (e: Exception) {
                println("⚠️ Error en recarga silenciosa: ${e.message}")
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

    // ✅ Buscar una oferta específica por ID
    fun obtenerOfertaPorId(idOferta: Long): OfertaLaboralResponse? {
        // Buscar primero en ofertasEmpresa, luego en ofertas
        return _ofertasEmpresa.value.find { it.idOferta == idOferta }
            ?: _ofertas.value.find { it.idOferta == idOferta }
    }

    // ✅ Actualizar estado de una oferta
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

                // Crear Request para actualizar (sin idOferta)
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
                    // Actualizar la lista local
                    _ofertasEmpresa.value = _ofertasEmpresa.value.map {
                        if (it.idOferta == idOferta) resultado else it
                    }
                    // También actualizar en _ofertas si existe
                    _ofertas.value = _ofertas.value.map {
                        if (it.idOferta == idOferta) resultado else it
                    }
                    _todasLasOfertas.value = _todasLasOfertas.value.map {
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
    // Agrega esta función en OfertasViewModel.kt
    fun eliminarOferta(idOferta: Long, idEmpresa: Long, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val resultado = repository.eliminarOferta(idOferta)
                if (resultado) {
                    // Eliminar de la lista local
                    _ofertasEmpresa.value = _ofertasEmpresa.value.filter { it.idOferta != idOferta }
                    _ofertas.value = _ofertas.value.filter { it.idOferta != idOferta }
                    _todasLasOfertas.value = _todasLasOfertas.value.filter { it.idOferta != idOferta }
                    println("✅ Oferta $idOferta eliminada correctamente")
                    onComplete(true)
                } else {
                    _error.value = "Error al eliminar la oferta"
                    onComplete(false)
                }
            } catch (e: Exception) {
                println("❌ Error al eliminar oferta: ${e.message}")
                _error.value = "Error: ${e.message}"
                onComplete(false)
            } finally {
                _loading.value = false
            }
        }
    }

}