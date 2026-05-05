package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.Perfil
import retrofit2.Response
import retrofit2.http.*

interface PerfilApi {

    @GET("api/perfiles/listar")
    suspend fun listar(): Response<List<Perfil>>

    @POST("api/perfiles/guardar")
    suspend fun guardar(@Body perfil: Perfil): Response<Perfil>

    @PATCH("api/perfiles/actualizar/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body perfil: Perfil): Response<Perfil>

    @DELETE("api/perfiles/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>

    @GET("api/perfiles/carrera/{carrera}")
    suspend fun buscarPorCarrera(@Path("carrera") carrera: String): Response<List<Perfil>>

    @GET("api/perfiles/habilidad/{habilidad}")
    suspend fun buscarPorHabilidad(@Path("habilidad") habilidad: String): Response<List<Perfil>>
}