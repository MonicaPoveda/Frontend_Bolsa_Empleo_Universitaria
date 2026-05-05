package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.Empresa
import retrofit2.http.GET
import retrofit2.http.Path

interface EmpresaApi {
    @GET("api/empresas/listar")
    suspend fun listarEmpresas(): List<Empresa>

    @GET("api/empresas/{id}")
    suspend fun obtenerEmpresaPorId(@Path("id") id: Long): Empresa
}