package com.example.frontend_bolsa_empleo_universitaria.interfaces



import com.example.frontend_bolsa_empleo_universitaria.model.EmpresaDto
import com.example.frontend_bolsa_empleo_universitaria.model.LoginRequest
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponse
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponseEmpresa
import com.example.frontend_bolsa_empleo_universitaria.model.RecuperarPassResponse
import com.example.frontend_bolsa_empleo_universitaria.model.RegEmpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EmpresaApi {


    @POST("api/empresas/login")
    suspend fun login(@Body request: LoginRequest): LoginResponseEmpresa

    @POST("api/empresas/guardar")
    suspend fun registrar(@Body request: RegEmpRequest): Response<EmpresaDto>

    // Si el backend tiene recuperación para empresa
    @POST("api/empresas/recuperar-password")
    suspend fun recuperarPassword(@Query("email") email: String): Response<RecuperarPassResponse>
}
