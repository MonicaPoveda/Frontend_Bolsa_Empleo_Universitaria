package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.example.frontend_bolsa_empleo_universitaria.model.*
import retrofit2.Response
import retrofit2.http.*

// interfaces/UsuarioApi.kt
interface UsuarioApi {

    // ==================== AUTENTICACIÓN ====================

    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/usuarios/recuperar-password")
    suspend fun recuperarPassword(@Query("email") email: String): Response<RecuperarPassResponse>


    // ==================== REGISTRO ====================

    @POST("api/usuarios/guardar")
    suspend fun registrar(@Body request: RegUsuRequest): Response<UsuarioDTO>


    // ==================== LISTAR ====================

    @GET("api/usuarios/listar")
    suspend fun listar(): Response<List<UsuarioDTO>>


    // ==================== CRUD ====================

    @PATCH("api/usuarios/actualizar/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body request: ActualizarUsuario
    ): Response<UsuarioDTO>

    @DELETE("api/usuarios/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>


    // ==================== BÚSQUEDAS ====================

    @GET("api/usuarios/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): Response<UsuarioDTO>

    @GET("api/usuarios/buscar-email")
    suspend fun buscarPorEmail(@Query("email") email: String): Response<UsuarioDTO>
}