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

    // Login de empresa
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
                    403 -> "Acceso denegado. Empresa no aprobada."
                    404 -> "Servicio no disponible"
                    else -> "Error del servidor: ${e.code()}"
                }
                Result.failure(Exception(errorMessage))
            } catch (e: Exception) {
                Result.failure(Exception("Error inesperado: ${e.message}"))
            }
        }
    }

    // Listar todas las empresas
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

    // Guardar nueva empresa (registro directo)
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

    // Actualizar empresa
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

    // Eliminar empresa
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

    // Listar top empresas (con más ofertas)
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

    // Obtener empresa por email (filtro local)
    suspend fun getEmpresaByEmail(email: String): EmpresaDto? {
        val empresas = listarEmpresas()
        return empresas.find { it.email == email }
    }

    // Obtener empresa por ID (filtro local)
    suspend fun getEmpresaById(id: Long): EmpresaDto? {
        val empresas = listarEmpresas()
        return empresas.find { it.idEmpresa == id }
    }

    // Obtener nombre de empresa por ID
    suspend fun getNombreEmpresa(idEmpresa: Long): String {
        return getEmpresaById(idEmpresa)?.nombre ?: "Empresa no disponible"
    }
}