package com.example.frontend_bolsa_empleo_universitaria.repository

import android.util.Log
import com.example.frontend_bolsa_empleo_universitaria.model.LoginRequest
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponse
import com.example.frontend_bolsa_empleo_universitaria.interfaces.UsuarioApi
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(private val usuarioApi: UsuarioApi) {

//    suspend fun login(email: String, password: String): Result<LoginResponse> {
//
//        return try {
//            val response = usuarioApi.login(LoginRequest(email, password))
//            Result.success(response)
//        } catch (e: HttpException) {
//            val errorBody = e.response()?.errorBody()?.string()
//            Result.failure(Exception(errorBody ?: "Error en el servidor"))
//        } catch (e: IOException) {
//            Result.failure(Exception("Error de red: Verifica tu conexión"))
//        } catch (e: Exception) {
//            Result.failure(Exception("Error inesperado: ${e.message}"))
//        }
//    }
suspend fun login(email: String, password: String): Result<LoginResponse> {
    return try {
        val response = usuarioApi.login(LoginRequest(email, password))
        Log.d("AuthRepo", "Login success: ${response.token}")
        Result.success(response)
    } catch (e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        Log.e("AuthRepo", "HTTP error: $errorBody")
        Result.failure(Exception(errorBody ?: "Error en el servidor"))
    } catch (e: IOException) {
        Log.e("AuthRepo", "Network error: ${e.message}")
        Result.failure(Exception("Error de red: Verifica tu conexión"))
    } catch (e: Exception) {
        Log.e("AuthRepo", "Unexpected error: ${e.message}")
        Result.failure(Exception("Error inesperado: ${e.message}"))
    }
}
}