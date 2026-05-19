package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.UsuarioApi
import com.example.frontend_bolsa_empleo_universitaria.model.LoginRequest
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponse
import com.example.frontend_bolsa_empleo_universitaria.model.RecuperarPassResponse
import com.example.frontend_bolsa_empleo_universitaria.model.RegUsuRequest
import com.example.frontend_bolsa_empleo_universitaria.model.UsuarioDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class AuthRepository(
    private val api: UsuarioApi
) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(email.trim(), password))
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("EMPTY_RESPONSE"))
                } else {
                    Result.failure(Exception(parseHttpError(response)))
                }
            } catch (e: SocketTimeoutException) {
                Result.failure(Exception("TIMEOUT_ERROR"))
            } catch (e: IOException) {
                Result.failure(Exception("NETWORK_ERROR: ${e.message}"))
            } catch (e: HttpException) {
                Result.failure(Exception(httpExceptionCode(e)))
            } catch (e: Exception) {
                Result.failure(Exception("UNKNOWN_ERROR: ${e.message}"))
            }
        }
    }

    suspend fun existeEmail(email: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.buscarPorEmail(email.trim())
                when {
                    response.isSuccessful -> Result.success(response.body() != null)
                    response.code() == 404 -> Result.success(false)
                    response.code() == 401 || response.code() == 403 -> Result.failure(Exception("LOOKUP_UNAVAILABLE_${response.code()}"))
                    response.code() in 500..599 -> Result.failure(Exception("SERVER_ERROR_${response.code()}"))
                    else -> Result.success(false)
                }
            } catch (e: SocketTimeoutException) {
                Result.failure(Exception("TIMEOUT_ERROR"))
            } catch (e: IOException) {
                Result.failure(Exception("NETWORK_ERROR: ${e.message}"))
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

    private fun parseHttpError(response: Response<*>): String {
        val code = response.code()
        val fallback = "HTTP_ERROR_$code"
        val message = try {
            val errorBody = response.errorBody()?.string()
            val json = JSONObject(errorBody ?: "{}")
            json.optString("message", fallback).ifBlank { fallback }
        } catch (ex: Exception) {
            fallback
        }

        return when (code) {
            401, 403, 404 -> "INVALID_CREDENTIALS_$code"
            in 500..599 -> "SERVER_ERROR_$code: $message"
            else -> "$fallback: $message"
        }
    }

    private fun httpExceptionCode(e: HttpException): String {
        return when (e.code()) {
            401, 403, 404 -> "INVALID_CREDENTIALS_${e.code()}"
            in 500..599 -> "SERVER_ERROR_${e.code()}"
            else -> "HTTP_ERROR_${e.code()}"
        }
    }
}
