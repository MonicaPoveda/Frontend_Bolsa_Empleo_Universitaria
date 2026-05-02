package com.example.frontend_bolsa_empleo_universitaria.Interfaces


import com.example.frontend_bolsa_empleo_universitaria.Model.OfertaLaboral
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
interface OfertaLaboralApi {

    // 🔹 Obtener todas las ofertas
    @GET("api/ofertas/listar")
    suspend fun listarOfertas(): List<OfertaLaboral>

    // 🔹 Obtener solo activas
    @GET("api/ofertas/activas")
    suspend fun listarActivas(): List<OfertaLaboral>

    // 🔹 Buscar por área
    @GET("api/ofertas/area/{area}")
    suspend fun buscarPorArea(
        @Path("area") area: String
    ): List<OfertaLaboral>

    // 🔹 Buscar por cargo (titulo)
    @GET("api/ofertas/cargo/{cargo}")
    suspend fun buscarPorCargo(
        @Path("cargo") cargo: String
    ): List<OfertaLaboral>

    // 🔹 Guardar nueva oferta
    @POST("api/ofertas/guardar")
    suspend fun guardarOferta(
        @Body oferta: OfertaLaboral
    ): OfertaLaboral

    // 🔹 Actualizar oferta
    @PUT("api/ofertas/actualizar/{id}")
    suspend fun actualizarOferta(
        @Path("id") id: Long,
        @Body oferta: OfertaLaboral
    ): OfertaLaboral

    // 🔹 Eliminar oferta
    @DELETE("api/ofertas/eliminar/{id}")
    suspend fun eliminarOferta(
        @Path("id") id: Long
    )
}