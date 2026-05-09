package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.SeguimientoPostulacionApi
import com.example.frontend_bolsa_empleo_universitaria.model.SeguimientoPostulacionDto

class SeguimientoPostulacionRepository(
    private val api: SeguimientoPostulacionApi
) {

    suspend fun historialPorPostulacion(idPostulacion: Long): List<SeguimientoPostulacionDto> {
        return try {
            val response = api.historialPorPostulacion(idPostulacion)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                println("Error historialPorPostulacion: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Excepción historialPorPostulacion: ${e.message}")
            emptyList()
        }
    }

    suspend fun listar(): List<SeguimientoPostulacionDto> {
        return try {
            val response = api.listar()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                println("Error listar seguimientos: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Excepción listar: ${e.message}")
            emptyList()
        }
    }

    suspend fun guardar(seguimiento: SeguimientoPostulacionDto): SeguimientoPostulacionDto? {
        return try {
            val response = api.guardar(seguimiento)
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error guardar seguimiento: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            println("Excepción guardar: ${e.message}")
            null
        }
    }
}