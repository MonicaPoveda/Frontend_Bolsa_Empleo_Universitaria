package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.EmpresaDto
import com.example.frontend_bolsa_empleo_universitaria.model.LoginRequest
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponseEmpresa
import retrofit2.Response
import retrofit2.http.*

interface EmpresaApi {

    // Login de empresa
    @POST("api/empresas/login")
    suspend fun login(@Body request: LoginRequest): LoginResponseEmpresa

    // Listar todas las empresas
    @GET("api/empresas/listar")
    suspend fun listar(): Response<List<EmpresaDto>>

    // Guardar nueva empresa
    @POST("api/empresas/guardar")
    suspend fun guardar(@Body empresa: EmpresaDto): Response<EmpresaDto>

    // Actualizar empresa existente
    @PUT("api/empresas/actualizar/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body empresa: EmpresaDto
    ): Response<EmpresaDto>

    // Eliminar empresa
    @DELETE("api/empresas/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>

    // Listar empresas con más ofertas (top)
    @GET("api/empresas/top")
    suspend fun listarTopEmpresas(): Response<List<EmpresaDto>>

    // Obtener empresa por ID (si necesitas este endpoint - agregar en backend si no existe)
    @GET("api/empresas/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): Response<EmpresaDto>
}