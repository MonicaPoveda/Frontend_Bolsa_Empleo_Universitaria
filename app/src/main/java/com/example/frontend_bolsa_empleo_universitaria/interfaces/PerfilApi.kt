package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.ActualizarPerfilUsuario
import com.example.frontend_bolsa_empleo_universitaria.model.Perfil
import com.example.frontend_bolsa_empleo_universitaria.model.PerfilRequest
import retrofit2.Response
import retrofit2.http.*

interface PerfilApi {

    @POST("api/perfiles/guardar")
    suspend fun crearPerfil(
        @Body request: PerfilRequest
    ): Response<Perfil>

    @GET("api/perfiles/listar")
    suspend fun listarPerfiles(): Response<List<Perfil>>

    @GET("api/perfiles/usuario/{idUsuario}")
    suspend fun obtenerPerfilPorUsuario(
        @Path("idUsuario") idUsuario: Long
    ): Response<Perfil>

    @PATCH("api/perfiles/actualizar/{id}")
    suspend fun actualizarPerfil(
        @Path("id") id: Long,
        @Body perfil: ActualizarPerfilUsuario
    ): Response<Perfil>
}
