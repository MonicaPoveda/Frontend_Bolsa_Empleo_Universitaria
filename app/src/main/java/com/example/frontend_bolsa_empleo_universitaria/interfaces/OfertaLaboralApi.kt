package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboral
import retrofit2.Response
import retrofit2.http.*

interface OfertaLaboralApi {
    @GET("api/ofertas/listar")
    suspend fun listar(): Response<List<OfertaLaboral>>

    @GET("api/ofertas/activas")
    suspend fun listarActivas(): Response<List<OfertaLaboral>>

    @POST("api/ofertas/guardar")
    suspend fun guardar(@Body oferta: OfertaLaboral): Response<OfertaLaboral>

    @PUT("api/ofertas/actualizar/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body oferta: OfertaLaboral): Response<OfertaLaboral>

    @DELETE("api/ofertas/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>
}