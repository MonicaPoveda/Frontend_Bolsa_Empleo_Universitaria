package com.example.frontend_bolsa_empleo_universitaria.ViewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.Model.OfertaLaboral
import com.example.frontend_bolsa_empleo_universitaria.Interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.Repository.OfertaRepository
import kotlinx.coroutines.launch

class OfertasViewModel : ViewModel() {

    private val repository = OfertaRepository(RetrofitClient.api)

    private val _ofertas = mutableStateOf<List<OfertaLaboral>>(emptyList())
    val ofertas: State<List<OfertaLaboral>> = _ofertas

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

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
}