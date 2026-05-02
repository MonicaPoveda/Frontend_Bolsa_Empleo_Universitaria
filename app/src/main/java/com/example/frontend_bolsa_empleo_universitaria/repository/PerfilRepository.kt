package com.example.frontend_bolsa_empleo_universitaria.Repository

import com.example.frontend_bolsa_empleo_universitaria.Interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.Model.Perfil

class PerfilRepository {
    private val api = RetrofitClient.perfilApi

    suspend fun guardar(perfil: Perfil): Result<Perfil> = try {
        val r = api.guardar(perfil)
        if (r.isSuccessful) Result.success(r.body()!!)
        else Result.failure(Exception("Error al guardar perfil"))
    } catch (e: Exception) { Result.failure(Exception("Sin conexión")) }
}
