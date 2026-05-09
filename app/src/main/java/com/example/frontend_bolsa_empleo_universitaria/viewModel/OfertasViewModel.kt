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

    fun cargarActivas() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val datos = repository.listarActivas()
                println("OFERTAS CARGADAS: ${datos.size}")
                _ofertas.value = datos
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun buscarPorArea(area: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _ofertas.value = repository.buscarPorArea(area)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun buscarPorCargo(cargo: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _ofertas.value = repository.buscarPorCargo(cargo)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
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
}