package com.example.frontend_bolsa_empleo_universitaria.interfaces



import com.example.frontend_bolsa_empleo_universitaria.model.EmpresaDto
import com.example.frontend_bolsa_empleo_universitaria.model.LoginRequest
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponse
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponseEmpresa
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EmpresaApi {


    @POST("api/empresas/login")
    suspend fun login(@Body request: LoginRequest): LoginResponseEmpresa
}
