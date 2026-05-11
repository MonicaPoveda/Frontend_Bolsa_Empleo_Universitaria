package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.PostulacionApi
import com.example.frontend_bolsa_empleo_universitaria.interfaces.UsuarioApi
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionRequest
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionResponse
import com.example.frontend_bolsa_empleo_universitaria.model.SeguimientoPostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.model.UsuarioDTO

class PostulacionRepository(
    private val api: PostulacionApi,
    private val usuarioApi: UsuarioApi
) {

    // Cache de usuarios para evitar múltiples llamadas pesadas
    private val usuarioCache = mutableMapOf<Long, UsuarioDTO>()

    suspend fun listarPorOferta(idOferta: Long): List<PostulacionDto> {
        return try {
            println("🔍 [Admin/Empresa] Cargando postulaciones para oferta ID: $idOferta")
            val response = api.listarPorOferta(idOferta)
            if (response.isSuccessful) {
                val postulaciones = response.body() ?: emptyList()
                println("✅ Postulaciones encontradas: ${postulaciones.size}")
                
                if (postulaciones.isEmpty()) return emptyList()

                // Optimizamos: Cargamos la lista de usuarios una sola vez para esta consulta
                preCargarUsuarios()

                // Enriquecer con los nombres de la caché
                postulaciones.map { postulacion ->
                    val usuario = usuarioCache[postulacion.idUsuario]
                    postulacion.copy(
                        nombreEstudiante = if (usuario != null) "${usuario.nombre} ${usuario.apellido}" else "Estudiante #${postulacion.idUsuario}",
                        emailEstudiante = usuario?.email ?: "Email no disponible"
                    )
                }
            } else {
                println("❌ Error listarPorOferta: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("⚠️ Excepción listarPorOferta: ${e.message}")
            emptyList()
        }
    }

    private suspend fun preCargarUsuarios() {
        try {
            // Solo cargamos si la caché está vacía
            if (usuarioCache.isNotEmpty()) return

            println("👤 Obteniendo lista de usuarios para enriquecer postulaciones...")
            val response = usuarioApi.listar()
            if (response.isSuccessful) {
                response.body()?.forEach { usuario ->
                    usuarioCache[usuario.idUsuario] = usuario
                }
                println("✅ Caché de usuarios sincronizada (${usuarioCache.size} registros)")
            }
        } catch (e: Exception) {
            println("⚠️ No se pudo pre-cargar usuarios: ${e.message}")
        }
    }

    suspend fun listarPorCandidato(idUsuario: Long): List<PostulacionDto> {
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

    suspend fun crearPostulacion(idUsuario: Long, idOferta: Long): PostulacionResponse? {
        return try {
            val request = PostulacionRequest(idUsuario, idOferta)
            val response = api.postularse(request)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun actualizar(id: Long, postulacion: PostulacionDto): PostulacionDto? {
        return try {
            val response = api.actualizar(id, postulacion)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
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
