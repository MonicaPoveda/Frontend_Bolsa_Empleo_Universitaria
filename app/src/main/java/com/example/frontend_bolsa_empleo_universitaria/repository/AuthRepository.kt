package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.UsuarioApi
import com.example.frontend_bolsa_empleo_universitaria.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class AuthRepository(
    private val api: UsuarioApi
) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(email, password))
                Result.success(response)
            } catch (e: SocketTimeoutException) {
                Result.failure(Exception("TIMEOUT_ERROR: El servidor tardó demasiado en responder."))
            } catch (e: IOException) {
                Result.failure(Exception("NETWORK_ERROR: ${e.message}"))
            } catch (e: HttpException) {
                // Devolvemos el código de error en el mensaje para que el ViewModel lo procese
                Result.failure(Exception("HTTP_ERROR_${e.code()}: ${e.message()}"))
            } catch (e: Exception) {
                Result.failure(Exception("UNKNOWN_ERROR: ${e.message}"))
            }
        }
    }

    suspend fun registrarEstudiante(request: RegUsuRequest): Result<UsuarioDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.registrar(request)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Respuesta vacía del servidor"))
                } else {
                    Result.failure(Exception("Error al registrar: ${response.code()}"))
                }
            } catch (e: IOException) {
                Result.failure(Exception("Error de red: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(Exception("Error inesperado: ${e.message}"))
            }
        }
    }

    suspend fun recuperarPassword(email: String): Result<RecuperarPassResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.recuperarPassword(email)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Respuesta vacía del servidor"))
                } else {
                    Result.failure(Exception("Error al recuperar contraseña: ${response.code()}"))
                }
            } catch (e: IOException) {
                Result.failure(Exception("Error de red: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(Exception("Error inesperado: ${e.message}"))
            }
        }
    }
}