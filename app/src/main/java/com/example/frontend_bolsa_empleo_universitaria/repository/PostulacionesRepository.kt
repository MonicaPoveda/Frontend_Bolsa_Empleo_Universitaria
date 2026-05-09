package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.PostulacionApi
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionDto

class PostulacionRepository(
    private val api: PostulacionApi
) {

    suspend fun listarPorOferta(idOferta: Long): List<PostulacionDto> {
        return try {
            val response = api.listarPorOferta(idOferta)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                println("Error listarPorOferta: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Excepción listarPorOferta: ${e.message}")
            emptyList()
        }
    }

    suspend fun listarPorCandidato(idUsuario: Long): List<PostulacionDto> {
        return try {
            val response = api.listarPorCandidato(idUsuario)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                println("Error listarPorCandidato: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Excepción listarPorCandidato: ${e.message}")
            emptyList()
        }
    }

    suspend fun guardar(postulacion: PostulacionDto): PostulacionDto? {
        return try {
            val response = api.guardar(postulacion)
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error guardar postulación: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            println("Excepción guardar: ${e.message}")
            null
        }
    }

    suspend fun actualizar(id: Long, postulacion: PostulacionDto): PostulacionDto? {
        return try {
            val response = api.actualizar(id, postulacion)
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error actualizar postulación: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            println("Excepción actualizar: ${e.message}")
            null
        }
    }

    suspend fun eliminar(id: Long): Boolean {
        return try {
            val response = api.eliminar(id)
            response.isSuccessful
        } catch (e: Exception) {
            println("Excepción eliminar: ${e.message}")
            false
        }
    }
}