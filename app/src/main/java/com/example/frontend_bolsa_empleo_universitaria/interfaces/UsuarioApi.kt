package com.example.frontend_bolsa_empleo_universitaria.interfaces
import com.example.frontend_bolsa_empleo_universitaria.model.LoginRequest
import com.example.frontend_bolsa_empleo_universitaria.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// interfaces/UsuarioApi.kt
interface UsuarioApi {

    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<Usuario>

    @GET("api/usuarios/listar")
    suspend fun listar(): Response<List<Usuario>>

    @POST("api/usuarios/guardar")
    suspend fun guardar(@Body usuario: Usuario): Response<Usuario>

    @PUT("api/usuarios/actualizar/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body usuario: Usuario): Response<Usuario>

    @DELETE("api/usuarios/eliminar/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Void>

    @GET("api/usuarios/buscar-email")
    suspend fun buscarPorEmail(@Query("email") email: String): Response<Usuario>

    @POST("api/usuarios/recuperar-password")
    suspend fun recuperarPassword(@Query("email") email: String): Response<Void>
}