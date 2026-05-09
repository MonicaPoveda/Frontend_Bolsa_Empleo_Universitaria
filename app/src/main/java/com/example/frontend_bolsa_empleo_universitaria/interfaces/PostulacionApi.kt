package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionRequest
import com.example.frontend_bolsa_empleo_universitaria.model.PostulacionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PostulacionApi {
    @POST("api/postulaciones/guardar")
    suspend fun postularse(
        @Body request: PostulacionRequest
    ): Response<PostulacionResponse>

    @GET("api/postulaciones/candidato/{idUsuario}")
    suspend fun listarPorEstudiante(
        @Path("idUsuario") idUsuario: Long
    ): Response<List<PostulacionResponse>>
}
