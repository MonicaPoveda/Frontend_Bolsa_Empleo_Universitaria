package com.example.frontend_bolsa_empleo_universitaria.repository

import android.content.Context
import com.example.frontend_bolsa_empleo_universitaria.interfaces.AdminApi
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.*
import retrofit2.Response

class AdminRepository(private val context: Context) {

    init {
        // Aseguramos que el cliente global esté inicializado con el contexto
        RetrofitClient.init(context)
    }

    private val api: AdminApi = RetrofitClient.adminApi

    suspend fun listarEmpresasPendientes(): Response<List<EmpresaPendiente>> = api.listarEmpresasPendientes()
    
    suspend fun aprobarEmpresa(id: Long, mensaje: String? = null): Response<EmpresaDto> = api.aprobarEmpresa(id, mensaje)
    
    suspend fun rechazarEmpresa(id: Long, mensaje: String? = null): Response<Void> = api.rechazarEmpresa(id, mensaje)
    
    suspend fun eliminarSolicitud(id: Long): Response<Void> = api.eliminarSolicitud(id)

    suspend fun listarEmpresasAceptadas(): Response<List<EmpresaDto>> = api.listarEmpresasAceptadas()
    
    suspend fun eliminarEmpresa(id: Long): Response<Void> = api.eliminarEmpresa(id)
    
    suspend fun listarOfertasPorEmpresa(idEmpresa: Long): Response<List<OfertaLaboralResponse>> {
        return try {
            val response = RetrofitClient.ofertaLaboralApi.listar()
            if (response.isSuccessful) {
                val filtradas = (response.body() ?: emptyList()).filter { it.idEmpresa == idEmpresa }
                Response.success(filtradas)
            } else {
                api.listarOfertasPorEmpresa(idEmpresa)
            }
        } catch (e: Exception) {
            api.listarOfertasPorEmpresa(idEmpresa)
        }
    }

    suspend fun listarPostulacionesPorOferta(idOferta: Long): Response<List<PostulacionDto>> = api.listarPostulacionesPorOferta(idOferta)
}
