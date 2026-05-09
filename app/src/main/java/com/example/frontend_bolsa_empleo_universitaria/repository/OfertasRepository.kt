package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.OfertaLaboralApi
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral

class OfertasRepository(
    private val api: OfertaLaboralApi,
    private val token: com.example.frontend_bolsa_empleo_universitaria.utils.Token? = null
) {

    fun getCurrentEmpresaId(): Long = token?.getEmpresaId() ?: 0

    suspend fun listarActivas(): List<OfertaLaboral> {
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
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun listarTodas(): List<OfertaLaboral> {
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
            e.printStackTrace()
            emptyList()
        }
    }
    suspend fun guardarOferta(oferta: OfertaLaboral): OfertaLaboral? {
        return try {
            val response = api.guardar(oferta)
            if (response.isSuccessful) {
                println("✅ Oferta guardada: ${response.body()}")
                response.body()
            } else {
                println("❌ Error al guardar: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            println("❌ Excepción: ${e.message}")
            null
        }
    }
}