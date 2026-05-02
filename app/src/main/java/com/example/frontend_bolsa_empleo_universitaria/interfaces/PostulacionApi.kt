package com.example.frontend_bolsa_empleo_universitaria.Interfaces

import com.example.frontend_bolsa_empleo_universitaria.Model.Postulacion
import retrofit2.http.*
import retrofit2.*

// interfaces/PostulacionApi.kt
interface PostulacionApi {

    @GET("api/postulaciones/listar")
    suspend fun listar(): Response<List<Postulacion>>

    @POST("api/postulaciones/guardar")
    suspend fun guardar(@Body postulacion: Postulacion): Response<Postulacion>

    @PUT("api/postulaciones/actualizar/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body postulacion: Postulacion): Response<Postulacion>

    @DELETE("api/postulaciones/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>

    // Consultas nativas
    @GET("api/postulaciones/candidato/{idUsuario}")
    suspend fun listarPorCandidato(@Path("idUsuario") idUsuario: Long): Response<List<Postulacion>>

    @GET("api/postulaciones/oferta/{idOferta}")
    suspend fun listarCandidatosPorOferta(@Path("idOferta") idOferta: Long): Response<List<Postulacion>>
}