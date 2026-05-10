package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralRequest
import com.example.frontend_bolsa_empleo_universitaria.model.OfertaLaboralResponse
import retrofit2.Response
import retrofit2.http.*

interface OfertaLaboralApi {
    @GET("api/ofertas/listar")
    suspend fun listar(): Response<List<OfertaLaboralResponse>>

    @GET("api/ofertas/activas")
    suspend fun listarActivas(): Response<List<OfertaLaboralResponse>>

    @POST("api/ofertas/guardar")
    suspend fun guardar(@Body request: OfertaLaboralRequest): Response<OfertaLaboralResponse>  // ✅ CORRECTO

    @PUT("api/ofertas/actualizar/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body oferta: OfertaLaboralRequest): Response<OfertaLaboralResponse>  // ✅ CORRECTO

    @DELETE("api/ofertas/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>
}