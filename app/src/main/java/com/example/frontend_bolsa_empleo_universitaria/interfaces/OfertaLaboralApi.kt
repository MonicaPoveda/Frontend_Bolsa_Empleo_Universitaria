package com.example.frontend_bolsa_empleo_universitaria.Interfaces


import com.example.frontend_bolsa_empleo_universitaria.Model.OfertaLaboral
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// interfaces/OfertaLaboralApi.kt
interface OfertaLaboralApi {

    @GET("api/ofertas/listar")
    suspend fun listar(): Response<List<OfertaLaboral>>

    @POST("api/ofertas/guardar")
    suspend fun guardar(@Body oferta: OfertaLaboral): Response<OfertaLaboral>

    @PUT("api/ofertas/actualizar/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body oferta: OfertaLaboral): Response<OfertaLaboral>

    @DELETE("api/ofertas/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>

    @GET("api/ofertas/area/{area}")
    suspend fun buscarPorArea(@Path("area") area: String): Response<List<OfertaLaboral>>

    @GET("api/ofertas/cargo/{cargo}")
    suspend fun buscarPorCargo(@Path("cargo") cargo: String): Response<List<OfertaLaboral>>

    @GET("api/ofertas/activas")
    suspend fun listarActivas(): Response<List<OfertaLaboral>>
}