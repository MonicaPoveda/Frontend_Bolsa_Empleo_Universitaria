package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.PostulacionApi
import com.example.frontend_bolsa_empleo_universitaria.interfaces.UsuarioApi
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.model.SeguimientoPostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.model.UsuarioDTO

class PostulacionRepository(
    private val api: PostulacionApi,
    private val usuarioApi: UsuarioApi
) {

    // Cache de usuarios
    private val usuarioCache = mutableMapOf<Long, UsuarioDTO>()

    suspend fun listarPorOferta(idOferta: Long): List<PostulacionDto> {
        return try {
            val response = api.listarPorOferta(idOferta)
            if (response.isSuccessful) {
                val postulaciones = response.body() ?: emptyList()
                // Enriquecer cada postulación con datos del usuario
                postulaciones.map { postulacion ->
                    val usuario = obtenerUsuarioPorId(postulacion.idUsuario)
                    postulacion.copy(
                        nombreEstudiante = if (usuario != null) "${usuario.nombre} ${usuario.apellido}" else "Estudiante #${postulacion.idUsuario}",
                        emailEstudiante = usuario?.email ?: "Email no disponible"
                    )
                }
            } else {
                println("Error listarPorOferta: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Excepción listarPorOferta: ${e.message}")
            emptyList()
        }
    }

    private suspend fun obtenerUsuarioPorId(idUsuario: Long): UsuarioDTO? {
        // Verificar caché
        usuarioCache[idUsuario]?.let { return it }

        return try {
            // Usar listar y filtrar (aunque solo ADMIN puede, esto dará error para empresas)
            val response = usuarioApi.listar()
            if (response.isSuccessful) {
                val usuarios = response.body() ?: emptyList()
                val usuario = usuarios.find { it.idUsuario == idUsuario }
                if (usuario != null) {
                    usuarioCache[idUsuario] = usuario
                }
                usuario
            } else {
                println("Error al obtener usuario $idUsuario: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            println("Excepción al obtener usuario: ${e.message}")
            null
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