package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.interfaces.UsuarioApi
import com.example.frontend_bolsa_empleo_universitaria.model.LoginRequest
import com.example.frontend_bolsa_empleo_universitaria.model.Usuario

// repository/UsuarioRepository.kt
// repository/UsuarioRepository.kt
class UsuarioRepository {
    private val api = RetrofitClient.usuarioApi

    suspend fun listar(): Result<List<Usuario>> = try {
        val r = api.listar()
        if (r.isSuccessful) Result.success(r.body()!!)
        else Result.failure(Exception("Error al listar usuarios"))
    } catch (e: Exception) { Result.failure(Exception("Sin conexión")) }

    suspend fun guardar(usuario: Usuario): Result<Usuario> = try {
        val r = api.guardar(usuario)
        if (r.isSuccessful) Result.success(r.body()!!)
        else {
            val errorMsg = r.errorBody()?.string() ?: "Error al guardar usuario"
            Result.failure(Exception(errorMsg))
        }
    } catch (e: Exception) { Result.failure(Exception("Sin conexión: ${e.message}")) }

    suspend fun actualizar(id: Long, usuario: Usuario): Result<Usuario> = try {
        val r = api.actualizar(id, usuario)
        if (r.isSuccessful) Result.success(r.body()!!)
        else Result.failure(Exception("Error al actualizar usuario"))
    } catch (e: Exception) { Result.failure(Exception("Sin conexión")) }

    suspend fun login(email: String, password: String): Result<Usuario> = try {
        val r = api.login(LoginRequest(email, password))
        if (r.isSuccessful) Result.success(r.body()!!)
        else {
            val errorMsg = r.errorBody()?.string() ?: "Correo o contraseña incorrectos"
            Result.failure(Exception(errorMsg))
        }
    } catch (e: Exception) { Result.failure(Exception("Sin conexión: ${e.message}")) }

    suspend fun eliminar(id: Long): Result<Unit> = try {
        val r = api.eliminar(id)
        if (r.isSuccessful) Result.success(Unit)
        else Result.failure(Exception("Error al eliminar usuario"))
    } catch (e: Exception) { Result.failure(Exception("Sin conexión")) }

    suspend fun buscarPorEmail(email: String): Result<Usuario> = try {
        val r = api.buscarPorEmail(email)
        if (r.isSuccessful) Result.success(r.body()!!)
        else Result.failure(Exception("Usuario no encontrado"))
    } catch (e: Exception) { Result.failure(Exception("Sin conexión")) }
}