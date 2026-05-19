package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.*
import retrofit2.Response
import retrofit2.http.*

interface EmpresaApi {

    // ==================== AUTENTICACIÓN ====================

    @POST("api/empresas/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponseEmpresa>


    // ==================== REGISTRO ====================

    // Registro directo (sin aprobación)
    @POST("api/empresas/guardar")
    suspend fun registrar(@Body request: RegEmpRequest): Response<EmpresaDto>

    // Solicitar registro de empresa (con aprobación)
    @POST("api/empresas-pendientes/enviar")
    suspend fun enviarSolicitudEmpresa(
        @Body request: SolicitudRegistroEmpresa
    ): Response<EmpresaPendiente>


    // ==================== LISTAR ====================

    @GET("api/empresas/listar")
    suspend fun listar(): Response<List<EmpresaDto>>


    // ==================== CRUD ====================

    @POST("api/empresas/guardar")
    suspend fun guardar(@Body empresa: EmpresaDto): Response<EmpresaDto>

    @PUT("api/empresas/actualizar/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body empresa: EmpresaDto
    ): Response<EmpresaDto>

    @DELETE("api/empresas/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>


    // ==================== EXTRAS ====================

    @GET("api/empresas/top")
    suspend fun listarTopEmpresas(): Response<List<EmpresaDto>>

    @POST("api/empresas/recuperar-password")
    suspend fun recuperarPassword(@Query("email") email: String): Response<RecuperarPassResponse>
}
