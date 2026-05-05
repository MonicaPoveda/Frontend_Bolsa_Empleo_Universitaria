package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.Perfil

class PerfilRepository {
    private val api = RetrofitClient.perfilApi

    suspend fun guardar(perfil: Perfil): Result<Perfil> = try {
        val r = api.guardar(perfil)
        if (r.isSuccessful) Result.success(r.body()!!)
        else Result.failure(Exception("Error al guardar perfil"))
    } catch (e: Exception) { Result.failure(Exception("Sin conexión")) }


    suspend fun obtenerPorUsuario(idUsuario: Long): Result<Perfil> = try {
        val r = api.listar()
        if (r.isSuccessful) {
            val perfil = r.body()?.find { it.idUsuario == idUsuario }
            if (perfil != null) Result.success(perfil)
            else Result.failure(Exception("Perfil no encontrado"))
        } else Result.failure(Exception("Error al obtener perfil"))
    } catch (e: Exception) { Result.failure(Exception("Sin conexión")) }


    suspend fun actualizar(id: Long, perfil: Perfil): Result<Perfil> = try {
        val r = api.actualizar(id, perfil)
        if (r.isSuccessful) Result.success(r.body()!!)
        else {
            val errorMsg = r.errorBody()?.string() ?: "Error al actualizar perfil"
            Result.failure(Exception(errorMsg))
        }
    } catch (e: Exception) { Result.failure(Exception("Sin conexión: ${e.message}")) }
}