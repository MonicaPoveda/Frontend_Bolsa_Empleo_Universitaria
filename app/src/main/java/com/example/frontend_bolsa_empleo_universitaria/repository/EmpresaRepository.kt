package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.EmpresaApi
import com.example.frontend_bolsa_empleo_universitaria.model.EmpresaDto
import com.example.frontend_bolsa_empleo_universitaria.model.LoginRequest
import com.example.frontend_bolsa_empleo_universitaria.model.LoginResponseEmpresa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class EmpresaRepository(
    private val empresaApi: EmpresaApi
) {

    suspend fun login(email: String, password: String): Result<LoginResponseEmpresa> {
        return login(LoginRequest(email.trim(), password))
    }

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponseEmpresa> {
        return withContext(Dispatchers.IO) {
            try {
                val response = empresaApi.login(loginRequest)
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
                val response = empresaApi.listar()
                when {
                    response.isSuccessful -> {
                        val exists = response.body()
                            ?.any { it.email.equals(email.trim(), ignoreCase = true) }
                            ?: false
                        Result.success(exists)
                    }
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

    suspend fun listarEmpresas(): List<EmpresaDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = empresaApi.listar()
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    println("Error al listar empresas: ${response.code()}")
                    emptyList()
                }
            } catch (e: Exception) {
                println("Excepción al listar empresas: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun guardarEmpresa(empresa: EmpresaDto): EmpresaDto? {
        return withContext(Dispatchers.IO) {
            try {
                val response = empresaApi.guardar(empresa)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    println("Error al guardar empresa: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                println("Excepción al guardar empresa: ${e.message}")
                null
            }
        }
    }

    suspend fun actualizarEmpresa(id: Long, empresa: EmpresaDto): EmpresaDto? {
        return withContext(Dispatchers.IO) {
            try {
                val response = empresaApi.actualizar(id, empresa)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    println("Error al actualizar empresa: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                println("Excepción al actualizar empresa: ${e.message}")
                null
            }
        }
    }

    suspend fun eliminarEmpresa(id: Long): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = empresaApi.eliminar(id)
                response.isSuccessful
            } catch (e: Exception) {
                println("Excepción al eliminar empresa: ${e.message}")
                false
            }
        }
    }

    suspend fun listarTopEmpresas(): List<EmpresaDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = empresaApi.listarTopEmpresas()
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    println("Error al listar top empresas: ${response.code()}")
                    emptyList()
                }
            } catch (e: Exception) {
                println("Excepción al listar top empresas: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun getEmpresaByEmail(email: String): EmpresaDto? {
        val empresas = listarEmpresas()
        return empresas.find { it.email.equals(email, ignoreCase = true) }
    }

    suspend fun getEmpresaById(id: Long): EmpresaDto? {
        val empresas = listarEmpresas()
        return empresas.find { it.idEmpresa == id }
    }

    suspend fun getNombreEmpresa(idEmpresa: Long): String {
        return getEmpresaById(idEmpresa)?.nombre ?: "Empresa no disponible"
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
