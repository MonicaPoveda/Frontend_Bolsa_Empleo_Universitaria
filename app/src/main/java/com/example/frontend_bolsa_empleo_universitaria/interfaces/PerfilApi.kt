package com.example.frontend_bolsa_empleo_universitaria.interfaces

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
}