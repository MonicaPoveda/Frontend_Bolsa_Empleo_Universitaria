package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral
import kotlinx.coroutines.launch

class OfertasViewModel : ViewModel() {

    private val repository = OfertasRepository(RetrofitClient.ofertaLaboralApi)
    private val empresaRepository = EmpresaRepository(RetrofitClient.empresaApi)

    private val _ofertas = mutableStateOf<List<OfertaLaboral>>(emptyList())
    val ofertas: State<List<OfertaLaboral>> = _ofertas

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _empresaNombre = mutableStateOf("Cargando...")
    val empresaNombre: State<String> = _empresaNombre

    private val _todasLasOfertas = mutableStateOf<List<OfertaLaboral>>(emptyList())

    private val sinonimos = mapOf(
        "Diseño" to listOf("diseño", "disenador", "disenar", "design", "grafico", "grafica", "ux/ui", "figma", "prototipado", "interfaces"),
        "Desarrollo" to listOf("desarrollador", "developer", "programacion", "programador", "software", "backend", "frontend", "fullstack", "kotlin", "java", "python", "android"),
        "Marketing" to listOf("marketing", "publicidad", "digital", "community", "branding", "pauta"),
        "Ventas" to listOf("ventas", "vendedor", "comercial", "asesor comercial", "negocios", "comisiones"),
        "TI" to listOf("tecnologia", "sistemas", "infraestructura", "soporte tecnico", "redes", "informatica", "data", "analista de datos", "power bi", "tableau", "sql")
    )

    fun cargarActivas() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val datos = repository.listarActivas()
                _todasLasOfertas.value = datos
                _ofertas.value = datos.filter { it.estado }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun filtrarPorCategoria(categoria: String) {
        println("FILTRANDO POR: $categoria")
        println("TOTAL EN MEMORIA: ${_todasLasOfertas.value.size}")

        if (categoria == "Todas") {
            _ofertas.value = _todasLasOfertas.value.filter { it.estado }
            return
        }

        val palabrasClave = sinonimos[categoria] ?: listOf(categoria.lowercase())
        println("PALABRAS CLAVE: $palabrasClave")

        val resultado = _todasLasOfertas.value.filter { oferta ->
            oferta.estado &&
                    run {
                        val textoOferta = "${oferta.titulo} ${oferta.area} ${oferta.descripcion}".lowercase().normalize()
                        val coincide = palabrasClave.any { palabra -> textoOferta.contains(palabra.normalize()) }
                        println("  '${oferta.titulo}' → texto='$textoOferta' coincide=$coincide")
                        coincide
                    }
        }
        println("RESULTADO: ${resultado.size} ofertas")
        _ofertas.value = resultado
    }

    fun buscarGeneral(query: String) {
        if (query.isBlank()) {
            _ofertas.value = _todasLasOfertas.value.filter { it.estado }
            return
        }
        val q = query.trim().lowercase().normalize()
        _ofertas.value = _todasLasOfertas.value.filter { oferta ->
            oferta.estado &&
                    (oferta.titulo.lowercase().normalize().contains(q) ||
                            oferta.area.lowercase().normalize().contains(q) ||
                            oferta.descripcion.lowercase().normalize().contains(q) ||
                            oferta.modalidad.lowercase().normalize().contains(q))
        }
    }

    fun cargarEmpresa(idEmpresa: Long) {
        viewModelScope.launch {
            _empresaNombre.value = "Cargando..."
            try {
                _empresaNombre.value = empresaRepository.getNombreEmpresa(idEmpresa)
            } catch (e: Exception) {
                _empresaNombre.value = "Empresa no disponible"
            }
        }
    }

    fun String.normalize(): String {
        return java.text.Normalizer
            .normalize(this, java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }
}