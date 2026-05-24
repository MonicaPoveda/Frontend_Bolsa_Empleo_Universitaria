package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.PerfilApi
import com.example.frontend_bolsa_empleo_universitaria.model.ActualizarPerfilUsuario
import com.example.frontend_bolsa_empleo_universitaria.model.Perfil
import com.example.frontend_bolsa_empleo_universitaria.model.PerfilRequest
import com.example.frontend_bolsa_empleo_universitaria.utils.Token

class PerfilRepository(private val api: PerfilApi) {

    /**
     * Carga el perfil del estudiante autenticado.
     * 1) GET /api/perfiles/mi-perfil (ruta oficial del backend)
     * 2) GET /api/perfiles/listar filtrado por idUsuario (respaldo)
     */
    suspend fun cargarPerfilEstudiante(userId: Long): Perfil? {
        if (userId <= 0L) return null

        try {
            val miPerfil = api.obtenerMiPerfil()
            if (miPerfil.isSuccessful) {
                miPerfil.body()?.let { return it }
            }
        } catch (_: Exception) {
            // Continúa con el fallback.
        }

        return buscarEnListado(userId)
    }

    suspend fun obtenerMiPerfil(): Perfil? {
        val response = try {
            api.obtenerMiPerfil()
        } catch (_: Exception) {
            return null
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun obtenerPerfilPorUsuario(userId: Long): Perfil? =
        cargarPerfilEstudiante(userId)

    suspend fun crearPerfil(request: PerfilRequest): Perfil? {
        return try {
            val response = api.crearPerfil(request)
            if (response.isSuccessful) response.body() else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun actualizarPerfil(id: Long, perfil: ActualizarPerfilUsuario): Perfil? {
        return try {
            val response = api.actualizarPerfil(id, perfil)
            if (response.isSuccessful) response.body() else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun listarPerfiles(): List<Perfil>? {
        return try {
            val response = api.listarPerfiles()
            if (response.isSuccessful) response.body() else null
        } catch (_: Exception) {
            null
        }
    }

    fun sincronizarEstadoLocal(token: Token, perfil: Perfil?) {
        if (perfil == null) {
            token.setProfileCreated(false)
            return
        }
        token.setProfileCreated(true)
        val semestre = perfil.semestre?.uppercase().orEmpty()
        when {
            semestre.contains("GRADUAD") || semestre.contains("EGRESAD") ->
                token.setUserType("EGRESADO")
            token.getUserType().isNullOrBlank() ->
                token.setUserType("ESTUDIANTE")
        }
    }

    private suspend fun buscarEnListado(userId: Long): Perfil? {
        return try {
            val response = api.listarPerfiles()
            if (response.isSuccessful) {
                response.body()?.find { it.idUsuario == userId }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
