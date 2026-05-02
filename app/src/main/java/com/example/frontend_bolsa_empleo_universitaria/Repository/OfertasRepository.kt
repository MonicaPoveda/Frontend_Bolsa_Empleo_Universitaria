package com.example.frontend_bolsa_empleo_universitaria.Repository

import com.example.frontend_bolsa_empleo_universitaria.Interfaces.OfertaLaboralApi
import com.example.frontend_bolsa_empleo_universitaria.Model.OfertaLaboral

class OfertaRepository(
    private val api: OfertaLaboralApi
) {
    suspend fun listarOfertas(): List<OfertaLaboral> {
        return api.listarOfertas()
    }

    suspend fun listarActivas(): List<OfertaLaboral> {
        return api.listarActivas()
    }

    suspend fun buscarPorArea(area: String): List<OfertaLaboral> {
        return api.buscarPorArea(area)
    }

    suspend fun buscarPorCargo(cargo: String): List<OfertaLaboral> {
        return api.buscarPorCargo(cargo)
    }

    suspend fun guardarOferta(oferta: OfertaLaboral): OfertaLaboral {
        return api.guardarOferta(oferta)
    }

    suspend fun actualizarOferta(id: Long, oferta: OfertaLaboral): OfertaLaboral {
        return api.actualizarOferta(id, oferta)
    }

    suspend fun eliminarOferta(id: Long) {
        api.eliminarOferta(id)
    }
}