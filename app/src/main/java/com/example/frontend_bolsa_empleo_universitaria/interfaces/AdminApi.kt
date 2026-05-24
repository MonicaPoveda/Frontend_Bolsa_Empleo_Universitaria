package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.*
import retrofit2.Response
import retrofit2.http.*

interface AdminApi {

    @GET("api/empresas-pendientes/listar")
    suspend fun listarEmpresasPendientes(): Response<List<EmpresaPendiente>>

    @PUT("api/empresas-pendientes/aprobar/{id}")
    suspend fun aprobarEmpresa(
        @Path("id") id: Long,
        @Query("mensaje") mensaje: String? = null
    ): Response<EmpresaDto>

    @PUT("api/empresas-pendientes/rechazar/{id}")
    suspend fun rechazarEmpresa(
        @Path("id") id: Long,
        @Query("mensaje") mensaje: String? = null
    ): Response<Void>

    // ✅ Nuevo: Endpoint de eliminación para el 3er rechazo (según tu Swagger)
    @DELETE("api/empresas-pendientes/{id}")
    suspend fun eliminarSolicitud(@Path("id") id: Long): Response<Void>

    @GET("api/empresas/listar")
    suspend fun listarEmpresasAceptadas(): Response<List<EmpresaDto>>

    // ✅ Nuevo: Eliminar empresa aceptada del directorio
    @DELETE("api/empresas/eliminar/{id}")
    suspend fun eliminarEmpresa(@Path("id") id: Long): Response<Void>

    @GET("api/ofertas/empresa/{idEmpresa}")
    suspend fun listarOfertasPorEmpresa(@Path("idEmpresa") idEmpresa: Long): Response<List<OfertaLaboralResponse>>

    @GET("api/postulaciones/oferta/{idOferta}")
    suspend fun listarPostulacionesPorOferta(@Path("idOferta") idOferta: Long): Response<List<PostulacionDto>>

    // En tu interfaz EmpresaApi (o donde tengas los endpoints)

    @GET("api/empresas-pendientes/listar")
    suspend fun listarSolicitudesPendientes(): Response<List<SolicitudRegistroEmpresa>>

    @PUT("api/empresas-pendientes/actualizar")
    suspend fun actualizarSolicitud(@Body solicitud: SolicitudRegistroEmpresa): Response<Void>



}
