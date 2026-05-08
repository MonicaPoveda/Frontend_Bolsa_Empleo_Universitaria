package com.example.frontend_bolsa_empleo_universitaria.repository

import android.util.Log
import com.example.frontend_bolsa_empleo_universitaria.interfaces.EmpresaApi
import com.example.frontend_bolsa_empleo_universitaria.model.LoginRequest
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponseEmpresa
import retrofit2.HttpException
import java.io.IOException

class EmpresaRepository (private val empresaApi: EmpresaApi) {

    suspend fun login(email: String, password: String): Result<LoginResponseEmpresa> {
        return try {
            val response = empresaApi.login(LoginRequest(email, password))
            Log.d("EmpresaRepo", "Login success: ${response.token}")
            Result.success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("EmpresaRepo", "HTTP error: $errorBody")
            Result.failure(Exception(errorBody ?: "Error en el servidor"))
        } catch (e: IOException) {
            Log.e("EmpresaRepo", "Network error: ${e.message}")
            Result.failure(Exception("Error de red: Verifica tu conexión"))
        } catch (e: Exception) {
            Log.e("EmpresaRepo", "Unexpected error: ${e.message}")
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }



}