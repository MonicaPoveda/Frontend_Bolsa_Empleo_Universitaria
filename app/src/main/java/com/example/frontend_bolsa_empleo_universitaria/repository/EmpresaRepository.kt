package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.EmpresaApi
import com.example.frontend_bolsa_empleo_universitaria.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class EmpresaRepository(
    private val empresaApi: EmpresaApi
) {

    suspend fun login(email: String, password: String): Result<LoginResponseEmpresa> {
        return login(LoginRequest(email, password))
    }

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponseEmpresa> {
        return withContext(Dispatchers.IO) {
            try {
                val response = empresaApi.login(loginRequest)
                Result.success(response)
            } catch (e: IOException) {
                Result.failure(Exception("Error de red: ${e.message}"))
            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    401 -> "Credenciales incorrectas"
                    404 -> "Servicio no disponible"
                    else -> "Error del servidor: ${e.code()}"
                }
                Result.failure(Exception(errorMessage))
            } catch (e: Exception) {
                Result.failure(Exception("Error inesperado: ${e.message}"))
            }
        }
    }

    suspend fun registrarEmpresa(request: RegEmpRequest): Result<EmpresaDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = empresaApi.registrar(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: IOException) {
                Result.failure(Exception("Error de red: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(Exception("Error inesperado: ${e.message}"))
            }
        }
    }
}
