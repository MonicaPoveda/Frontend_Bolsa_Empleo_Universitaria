package com.example.frontend_bolsa_empleo_universitaria.interfaces



import com.example.frontend_bolsa_empleo_universitaria.model.EmpresaDto
import com.example.frontend_bolsa_empleo_universitaria.model.EmpresaPendiente
import com.example.frontend_bolsa_empleo_universitaria.model.LoginRequest
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponse
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponseEmpresa
import com.example.frontend_bolsa_empleo_universitaria.model.RecuperarPassResponse
import com.example.frontend_bolsa_empleo_universitaria.model.RegEmpRequest
import com.example.frontend_bolsa_empleo_universitaria.model.SolicitudRegistroEmpresa
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface EmpresaApi {


    @POST("api/empresas/login")
    suspend fun login(@Body request: LoginRequest): LoginResponseEmpresa

            // Solicitar registro de empresa (sin autenticación)
        @POST("api/empresas-pendientes/enviar")
        suspend fun enviarSolicitudEmpresa(
            @Body request: SolicitudRegistroEmpresa
        ): Response<EmpresaPendiente>

        // Listar todas las empresas
        @GET("api/empresas/listar")
        suspend fun listar(): Response<List<EmpresaDto>>

        // Guardar nueva empresa
        @POST("api/empresas/guardar")
        suspend fun guardar(@Body empresa: EmpresaDto): Response<EmpresaDto>

        // Actualizar empresa por ID
        @PUT("api/empresas/actualizar/{id}")
        suspend fun actualizar(
            @Path("id") id: Long,
            @Body empresa: EmpresaDto
        ): Response<EmpresaDto>

        // Eliminar empresa por ID
        @DELETE("api/empresas/eliminar/{id}")
        suspend fun eliminar(@Path("id") id: Long): Response<Void>

        // Listar empresas con más ofertas (top)
        @GET("api/empresas/top")
        suspend fun listarTopEmpresas(): Response<List<EmpresaDto>>
    }




