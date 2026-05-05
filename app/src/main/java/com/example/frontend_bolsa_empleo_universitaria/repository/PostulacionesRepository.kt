package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.Postulacion
import retrofit2.Response

class PostulacionesRepository {
    private val api = RetrofitClient.postulacionApi

    suspend fun listarPorCandidato(idUsuario: Long): List<Postulacion> {
        return try {
            val response = api.listarPorCandidato(idUsuario)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun guardar(postulacion: Postulacion): Boolean {
        return try {
            val response = api.guardar(postulacion)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminar(id: Long): Boolean {
        return try {
            val response = api.eliminar(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}