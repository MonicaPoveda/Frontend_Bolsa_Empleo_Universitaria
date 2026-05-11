package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.OfertaLaboralApi
import com.example.frontend_bolsa_empleo_universitaria.interfaces.PostulacionApi
import com.example.frontend_bolsa_empleo_universitaria.interfaces.UsuarioApi
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralResponse
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionRequest
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionResponse
import com.example.frontend_bolsa_empleo_universitaria.model.SeguimientoPostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.model.UsuarioDTO

class PostulacionRepository(
    private val api: PostulacionApi,
    private val usuarioApi: UsuarioApi,
    private val ofertaApi: OfertaLaboralApi
) {

    // Cache de usuarios y ofertas
    private val usuarioCache = mutableMapOf<Long, UsuarioDTO>()
    private val ofertaCache = mutableMapOf<Long, OfertaLaboralResponse>()

    suspend fun listarPorOferta(idOferta: Long): List<PostulacionDto> {
        return try {
            println("🔍 Cargando postulaciones para oferta: $idOferta")
            val response = api.listarPorOferta(idOferta)
            if (response.isSuccessful) {
                val postulaciones = response.body() ?: emptyList()
                println("✅ Postulaciones obtenidas: ${postulaciones.size}")
                
                if (postulaciones.isEmpty()) return emptyList()

                // Pre-cargar usuarios una sola vez para evitar múltiples llamadas a la API
                preCargarUsuarios()

                // Enriquecer cada postulación con datos del usuario desde la caché
                postulaciones.map { postulacion ->
                    val usuario = usuarioCache[postulacion.idUsuario]
                    postulacion.copy(
                        nombreEstudiante = if (usuario != null) "${usuario.nombre} ${usuario.apellido}" else "Estudiante #${postulacion.idUsuario}",
                        emailEstudiante = usuario?.email ?: "Email no disponible"
                    )
                }
            } else {
                println("❌ Error listarPorOferta: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("⚠️ Excepción listarPorOferta: ${e.message}")
            emptyList()
        }
    }

    private suspend fun preCargarUsuarios() {
        try {
            // Solo cargamos si la caché está vacía o para refrescar
            if (usuarioCache.isNotEmpty()) return

            println("👤 Pre-cargando lista de usuarios para nombres...")
            val response = usuarioApi.listar()
            if (response.isSuccessful) {
                val usuarios = response.body() ?: emptyList()
                usuarios.forEach { usuario ->
                    usuarioCache[usuario.idUsuario] = usuario
                }
                println("✅ Caché de usuarios lista: ${usuarioCache.size} usuarios")
            } else {
                println("⚠️ No se pudo listar usuarios (${response.code()}). Los nombres se mostrarán como IDs.")
            }
        } catch (e: Exception) {
            println("⚠️ Error al pre-cargar usuarios: ${e.message}")
        }
    }

    private suspend fun obtenerUsuarioPorId(idUsuario: Long): UsuarioDTO? {
        // Esta función ahora solo se usa como fallback o si se requiere un usuario específico
        usuarioCache[idUsuario]?.let { return it }
        return null // Ya que preCargarUsuarios debería haber llenado la caché
    }

    private suspend fun preCargarOfertas() {
        try {
            if (ofertaCache.isNotEmpty()) return

            println("💼 Pre-cargando lista de ofertas para títulos...")
            val response = ofertaApi.listar()
            if (response.isSuccessful) {
                val ofertas = response.body() ?: emptyList()
                ofertas.forEach { oferta ->
                    ofertaCache[oferta.idOferta] = oferta
                }
                println("✅ Caché de ofertas lista: ${ofertaCache.size} ofertas")
            }
        } catch (e: Exception) {
            println("⚠️ Error al pre-cargar ofertas: ${e.message}")
        }
    }

    suspend fun listarPorCandidato(idUsuario: Long): List<PostulacionDto> {
        return try {
            val response = api.listarPorCandidato(idUsuario)
            if (response.isSuccessful) {
                val postulaciones = response.body() ?: emptyList()
                
                if (postulaciones.isEmpty()) return emptyList()

                // Pre-cargar ofertas para obtener el título
                preCargarOfertas()

                postulaciones.map { postulacion ->
                    val oferta = ofertaCache[postulacion.idOferta]
                    postulacion.copy(
                        tituloOferta = oferta?.titulo ?: "Oferta #${postulacion.idOferta}"
                    )
                }
            } else {
                println("Error listarPorCandidato: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Excepción listarPorCandidato: ${e.message}")
            emptyList()
        }
    }

    // ✅ CORREGIDO: Usar postularse con PostulacionRequest
    suspend fun crearPostulacion(idUsuario: Long, idOferta: Long): PostulacionResponse? {
        return try {
            val request = PostulacionRequest(idUsuario, idOferta)
            val response = api.postularse(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error crear postulación: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            println("Excepción crear postulación: ${e.message}")
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