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
}
