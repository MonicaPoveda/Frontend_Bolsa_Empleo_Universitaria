package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.PerfilApi
import com.example.frontend_bolsa_empleo_universitaria.model.Perfil

// PerfilRepository.kt
class PerfilRepository(private val api: PerfilApi) {
    suspend fun obtenerPerfilPorUsuario(userId: Long): Perfil? {
        return try {
            val response = api.obtenerPerfilPorUsuario(userId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}