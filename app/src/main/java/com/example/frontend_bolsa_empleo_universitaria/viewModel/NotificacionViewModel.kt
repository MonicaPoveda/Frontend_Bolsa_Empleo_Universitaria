package com.example.frontend_bolsa_empleo_universitaria.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.repository.PostulacionesRepository
import com.example.frontend_bolsa_empleo_universitaria.screens.Notificacion
import com.example.frontend_bolsa_empleo_universitaria.screens.TipoNotificacion
import kotlinx.coroutines.launch

class NotificacionViewModel : ViewModel() {
    private val repository = PostulacionesRepository()
    private val ofertaApi = RetrofitClient.ofertaLaboralApi

    private val _notificaciones = mutableStateOf<List<Notificacion>>(emptyList())
    val notificaciones: State<List<Notificacion>> = _notificaciones

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    fun cargarNotificaciones(idUsuario: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val postulaciones = repository.listarPorCandidato(idUsuario)
                val ofertas = ofertaApi.listar().body() ?: emptyList()
                val empresas = try { RetrofitClient.empresaApi.listarEmpresas() } catch(e: Exception) { emptyList() }

                _notificaciones.value = postulaciones.map { p ->
                    val oferta = ofertas.find { it.idOferta == p.idOferta }
                    val nombreOferta = oferta?.titulo ?: "Oferta #${p.idOferta}"
                    val empresa = empresas.find { it.idEmpresa == oferta?.idEmpresa }

                    val lugar = if (empresa != null) "$nombreOferta en ${empresa.nombre}" else nombreOferta

                    Notificacion(
                        id = p.idPostulacion?.toInt() ?: 0,
                        categoria = "POSTULACIONES",
                        titulo = when (p.estado.uppercase()) {
                            "ACEPTADO", "ACEPTADA" -> "¡Felicitaciones! Fuiste aceptado en: $lugar"
                            "RECHAZADO", "RECHAZADA" -> "Tu postulación a $lugar fue rechazada"
                            else -> "Tu postulación a $lugar está en revisión"
                        },
                        // Mostramos el estado actual de forma limpia
                        descripcion = "Estado: ${p.estado}",
                        tiempo = p.fechaPostulacion,
                        tipo = TipoNotificacion.POSTULACION,
                        estado = p.estado
                    )
                }
            } catch (e: Exception) {
                _notificaciones.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
}