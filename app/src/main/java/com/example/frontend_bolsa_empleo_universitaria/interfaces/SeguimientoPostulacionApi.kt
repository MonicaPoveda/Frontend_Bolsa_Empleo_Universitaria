package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.SeguimientoPostulacionDto
import retrofit2.Response
import retrofit2.http.*

interface SeguimientoPostulacionApi {

    // Listar todos los seguimientos
    @GET("api/seguimientos/listar")
    suspend fun listar(): Response<List<SeguimientoPostulacionDto>>

    // Guardar nuevo seguimiento
    @POST("api/seguimientos/guardar")
    suspend fun guardar(@Body seguimiento: SeguimientoPostulacionDto): Response<SeguimientoPostulacionDto>

    // Actualizar seguimiento
    @PUT("api/seguimientos/actualizar/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body seguimiento: SeguimientoPostulacionDto
    ): Response<SeguimientoPostulacionDto>

    // Eliminar seguimiento
    @DELETE("api/seguimientos/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>

    // Obtener historial por postulación
    @GET("api/seguimientos/historial/{idPostulacion}")
    suspend fun historialPorPostulacion(@Path("idPostulacion") idPostulacion: Long): Response<List<SeguimientoPostulacionDto>>
}