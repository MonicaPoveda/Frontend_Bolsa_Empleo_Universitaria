package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.Empresa
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// interfaces/EmpresaApi.kt
interface EmpresaApi {

    @GET("api/empresas/listar")
    suspend fun listar(): Response<List<Empresa>>

    @POST("api/empresas/guardar")
    suspend fun guardar(@Body empresa: Empresa): Response<Empresa>

    @PUT("api/empresas/actualizar/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body empresa: Empresa): Response<Empresa>

    @DELETE("api/empresas/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>

    @GET("api/empresas/top")
    suspend fun listarEmpresasConMasOfertas(): Response<List<Empresa>>
}