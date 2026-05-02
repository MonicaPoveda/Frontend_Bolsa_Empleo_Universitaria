package com.example.frontend_bolsa_empleo_universitaria.interfaces
import com.example.frontend_bolsa_empleo_universitaria.model.SeguimientoPostulacion
import retrofit2.http.*
import retrofit2.*

interface SeguimientoPostulacionApi {

    @GET("api/seguimientos/listar")
    suspend fun listar(): Response<List<SeguimientoPostulacion>>

    @POST("api/seguimientos/guardar")
    suspend fun guardar(@Body seguimiento: SeguimientoPostulacion): Response<SeguimientoPostulacion>

    @PUT("api/seguimientos/actualizar/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body seguimiento: SeguimientoPostulacion): Response<SeguimientoPostulacion>

    @DELETE("api/seguimientos/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>

    // Consulta nativa
    @GET("api/seguimientos/historial/{idPostulacion}")
    suspend fun historialPorPostulacion(@Path("idPostulacion") idPostulacion: Long): Response<List<SeguimientoPostulacion>>
}