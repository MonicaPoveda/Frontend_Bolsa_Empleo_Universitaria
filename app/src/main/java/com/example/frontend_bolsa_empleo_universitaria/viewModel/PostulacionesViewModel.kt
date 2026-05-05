package com.example.frontend_bolsa_empleo_universitaria.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral
import com.example.frontend_bolsa_empleo_universitaria.model.Postulacion
import com.example.frontend_bolsa_empleo_universitaria.model.Usuario
import com.example.frontend_bolsa_empleo_universitaria.repository.EmpresaRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.OfertasRepository
import com.example.frontend_bolsa_empleo_universitaria.repository.PostulacionesRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostulacionesViewModel : ViewModel() {
    private val repository = PostulacionesRepository()
    private val ofertasRepository = OfertasRepository(RetrofitClient.ofertaLaboralApi)
    private val empresaApi = RetrofitClient.empresaApi

    private val _postulaciones = mutableStateOf<List<Postulacion>>(emptyList())
    val postulaciones: State<List<Postulacion>> = _postulaciones

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    fun cargarPostulaciones(idUsuario: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val listaBase = repository.listarPorCandidato(idUsuario)
                val todasLasOfertas = ofertasRepository.listarOfertas()
                val todasLasEmpresas = try { empresaApi.listarEmpresas() } catch (e: Exception) { emptyList() }

                listaBase.forEach { postu ->
                    val ofertaDetalle = todasLasOfertas.find { it.idOferta == postu.idOferta }
                    postu.oferta = ofertaDetalle

                    if (ofertaDetalle != null) {
                        val empresaDetalle = todasLasEmpresas.find { it.idEmpresa == ofertaDetalle.idEmpresa }
                        postu.empresa = empresaDetalle
                        postu.nombreEmpresa = empresaDetalle?.nombre ?: "Empresa no encontrada"
                    } else {
                        postu.nombreEmpresa = "Oferta no disponible"
                    }
                }
                _postulaciones.value = ArrayList(listaBase)
            } catch (e: Exception) {
                Log.e("PostulacionesVM", "Error hidratando: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun aplicarAOferta(usuario: Usuario, oferta: OfertaLaboral, onResult: (Boolean) -> Unit) {
        val idUsr = usuario.idUsuario ?: return onResult(false)
        viewModelScope.launch {
            try {
                val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val nuevaPostulacion = Postulacion(
                    fechaPostulacion = fechaActual,
                    estado = "PENDIENTE",
                    idUsuario = idUsr,
                    idOferta = oferta.idOferta
                )
                val success = repository.guardar(nuevaPostulacion)
                if (success) {
                    cargarPostulaciones(idUsr)
                }
                onResult(success)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun desistirDePostulacion(idPostulacion: Long, idUsuario: Long) {
        viewModelScope.launch {
            val success = repository.eliminar(idPostulacion)
            if (success) {
                cargarPostulaciones(idUsuario)
            }
        }
    }
}