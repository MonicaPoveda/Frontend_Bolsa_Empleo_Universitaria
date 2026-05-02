package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.OfertaLaboralApi
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient

class OfertasRepository(
    private val api: OfertaLaboralApi = RetrofitClient.ofertaLaboralApi
) {
    suspend fun listarOfertas(): List<OfertaLaboral> {
        val response = api.listar()
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    suspend fun listarActivas(): List<OfertaLaboral> {
        val response = api.listarActivas()
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    suspend fun buscarPorArea(area: String): List<OfertaLaboral> {
        val response = api.buscarPorArea(area)
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    suspend fun buscarPorCargo(cargo: String): List<OfertaLaboral> {
        val response = api.buscarPorCargo(cargo)
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    suspend fun guardarOferta(oferta: OfertaLaboral): OfertaLaboral? {
        val response = api.guardar(oferta)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun actualizarOferta(id: Long, oferta: OfertaLaboral): OfertaLaboral? {
        val response = api.actualizar(id, oferta)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun eliminarOferta(id: Long) {
        api.eliminar(id)
    }
}