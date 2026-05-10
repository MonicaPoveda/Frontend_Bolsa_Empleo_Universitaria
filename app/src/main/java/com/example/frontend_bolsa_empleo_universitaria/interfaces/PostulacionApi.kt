package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionDto
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionRequest
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionResponse
import retrofit2.Response
import retrofit2.http.*

interface PostulacionApi {

    // Listar todas las postulaciones (solo ADMIN)
    @GET("api/postulaciones/listar")
    suspend fun listar(): Response<List<PostulacionDto>>

    @POST("api/postulaciones/guardar")
    suspend fun postularse(
        @Body request: PostulacionRequest
    ): Response<PostulacionResponse>
    // Listar postulaciones por candidato (usuario)
    @GET("api/postulaciones/candidato/{idUsuario}")
    suspend fun listarPorCandidato(@Path("idUsuario") idUsuario: Long): Response<List<PostulacionDto>>

    // Listar postulaciones por candidato (usuario)
    @GET("/api/postulaciones/candidato/{idUsuario}")
    suspend fun listarPorEstudiante(
        @Path("idUsuario") idUsuario: Long
    ): Response<List<PostulacionResponse>>

    // Actualizar estado de postulación (ADMIN o ESTUDIANTE)
    @PUT("api/postulaciones/actualizar/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body postulacion: PostulacionDto
    ): Response<PostulacionDto>

    // Eliminar postulación (solo ADMIN)
    @DELETE("api/postulaciones/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>

    @POST("api/postulaciones/guardar")
    suspend fun guardar(@Body postulacion: PostulacionDto): Response<PostulacionDto>

    // Listar postulaciones por oferta (solo ADMIN)
    @GET("api/postulaciones/oferta/{idOferta}")
    suspend fun listarPorOferta(@Path("idOferta") idOferta: Long): Response<List<PostulacionDto>>
}