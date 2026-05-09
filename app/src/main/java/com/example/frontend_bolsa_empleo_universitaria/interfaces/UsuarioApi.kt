package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.ActualizarUsuario
import com.example.frontend_bolsa_empleo_universitaria.model.LoginRequest
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponse
import com.example.frontend_bolsa_empleo_universitaria.model.RecuperarPassResponse
import com.example.frontend_bolsa_empleo_universitaria.model.RegUsuRequest
import com.example.frontend_bolsa_empleo_universitaria.model.UsuarioDTO
import retrofit2.Response
import retrofit2.http.*

// interfaces/UsuarioApi.kt
interface UsuarioApi {

    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/usuarios/recuperar-password")
    suspend fun recuperarPassword(@Query("email") email: String): Response<RecuperarPassResponse>

    @POST("api/usuarios/guardar")
    suspend fun registrar(@Body request: RegUsuRequest): Response<UsuarioDTO>

    // ✅ CORREGIDO: El backend usa @PatchMapping y el path es api/usuarios/actualizar/{id}
    @PATCH("api/usuarios/actualizar/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body request: ActualizarUsuario
    ): Response<UsuarioDTO>

    // ✅ NUEVO: Endpoint para buscar usuario por email (existe en tu UsuarioController)
    @GET("api/usuarios/buscar-email")
    suspend fun buscarPorEmail(@Query("email") email: String): Response<UsuarioDTO>
}
