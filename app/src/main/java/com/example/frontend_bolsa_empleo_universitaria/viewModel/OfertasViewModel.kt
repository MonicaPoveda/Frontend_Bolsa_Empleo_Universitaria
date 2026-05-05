package com.example.frontend_bolsa_empleo_universitaria.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.Empresa
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral
import kotlinx.coroutines.launch

class OfertasViewModel : ViewModel() {

    private val repository = OfertasRepository(RetrofitClient.ofertaLaboralApi)
    private val empresaRepository = EmpresaRepository(RetrofitClient.empresaApi)

    private val _ofertas = mutableStateOf<List<OfertaLaboral>>(emptyList())
    val ofertas: State<List<OfertaLaboral>> = _ofertas

    private val _empresaSeleccionada = mutableStateOf<Empresa?>(null)
    val empresaSeleccionada: State<Empresa?> = _empresaSeleccionada

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _loadingEmpresa = mutableStateOf(false)
    val loadingEmpresa: State<Boolean> = _loadingEmpresa

    private val _errorEmpresa = mutableStateOf<String?>(null)
    val errorEmpresa: State<String?> = _errorEmpresa

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
        if (idEmpresa <= 0) {
            Log.e("OfertasViewModel", "Error: Intento de cargar empresa con ID inválido: $idEmpresa")
            _empresaNombre.value = "Empresa no disponible (ID 0)"
            _empresaSeleccionada.value = null
            _errorEmpresa.value = "ID de empresa inválido"
            return
        }

        viewModelScope.launch {
            _loadingEmpresa.value = true
            _errorEmpresa.value = null
            Log.d("OfertasViewModel", "Cargando empresa ID: $idEmpresa")
            try {
                val empresa = empresaRepository.getEmpresa(idEmpresa)
                if (empresa != null) {
                    Log.d("OfertasViewModel", "Empresa cargada exitosamente: ${empresa.nombre}")
                    _empresaSeleccionada.value = empresa
                    _empresaNombre.value = empresa.nombre
                } else {
                    Log.w("OfertasViewModel", "No se encontró la empresa con ID: $idEmpresa")
                    _empresaNombre.value = "Empresa no disponible"
                    _empresaSeleccionada.value = null
                    _errorEmpresa.value = "No se pudo encontrar la información de la empresa"
                }
            } catch (e: Exception) {
                Log.e("OfertasViewModel", "Error al cargar empresa $idEmpresa", e)
                _empresaNombre.value = "Error al cargar"
                _empresaSeleccionada.value = null
                _errorEmpresa.value = "Error de conexión al cargar la empresa"
            } finally {
                _loadingEmpresa.value = false
            }
        }
    }

    fun String.normalize(): String {
        return java.text.Normalizer
            .normalize(this, java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }
}