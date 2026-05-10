package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.OfertaLaboralApi
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralRequest
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralResponse

class OfertasRepository(
    private val api: OfertaLaboralApi
) {

    // ✅ Para LISTAR - OfertaLaboralResponse (con id)
    suspend fun listarActivas(): List<OfertaLaboralResponse> {
        return try {
            val response = api.listarActivas()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                println("Error API listarActivas: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Excepción en listarActivas: ${e.message}")
            emptyList()
        }
    }

    // ✅ Para LISTAR - OfertaLaboralResponse (con id)
    suspend fun listarTodas(): List<OfertaLaboralResponse> {
        return try {
            val response = api.listar()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                println("Error API listarTodas: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Excepción en listarTodas: ${e.message}")
            emptyList()
        }
    }

    // ✅ Para GUARDAR - Envía Request, Recibe Response
    suspend fun guardarOferta(oferta: OfertaLaboralRequest): OfertaLaboralResponse? {
        return try {
            val response = api.guardar(oferta)
            if (response.isSuccessful) {
                println("✅ Oferta guardada: ${response.body()}")
                response.body()  // ← Retorna OfertaLaboralResponse
            } else {
                println("❌ Error al guardar: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            println("❌ Excepción guardar: ${e.message}")
            null
        }
    }

    // ✅ Para ACTUALIZAR - Envía Request, Recibe Response
    suspend fun actualizarOferta(id: Long, oferta: OfertaLaboralRequest): OfertaLaboralResponse? {
        return try {
            val response = api.actualizar(id, oferta)
            if (response.isSuccessful) {
                println("✅ Oferta actualizada: ${response.body()}")
                response.body()  // ← Retorna OfertaLaboralResponse
            } else {
                println("❌ Error al actualizar: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            println("❌ Excepción actualizar: ${e.message}")
            null
        }
    }
}