package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.PerfilApi
import com.example.frontend_bolsa_empleo_universitaria.model.Perfil

class PerfilRepository(private val api: PerfilApi) {

    // Ya no necesitamos el token aquí, el interceptor lo añade solo
    suspend fun obtenerPerfilPorUsuario(userId: Long): Perfil? {
        return try {
            val response = api.listarPerfiles()
            if (response.isSuccessful) {
                val perfiles = response.body()
                // Buscar el perfil que pertenece a este usuario
                perfiles?.find { it.idUsuario == userId }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}