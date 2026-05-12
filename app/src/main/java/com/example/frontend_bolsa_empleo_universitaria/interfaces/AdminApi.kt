package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.*
import retrofit2.Response
import retrofit2.http.*

interface AdminApi {

    // ==================== GESTIÓN DE EMPRESAS PENDIENTES ====================

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


    // ==================== GESTIÓN DE EMPRESAS ACEPTADAS ====================

    @GET("api/empresas/listar")
    suspend fun listarEmpresasAceptadas(): Response<List<EmpresaDto>>


    // ==================== GESTIÓN DE OFERTAS Y POSTULACIONES ====================

    @GET("api/ofertas/empresa/{idEmpresa}")
    suspend fun listarOfertasPorEmpresa(@Path("idEmpresa") idEmpresa: Long): Response<List<OfertaLaboralResponse>>

    @GET("api/postulaciones/oferta/{idOferta}")
    suspend fun listarPostulacionesPorOferta(@Path("idOferta") idOferta: Long): Response<List<PostulacionDto>>
}
