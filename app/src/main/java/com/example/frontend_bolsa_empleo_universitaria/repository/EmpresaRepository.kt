package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.EmpresaApi
import com.example.frontend_bolsa_empleo_universitaria.model.Empresa

class EmpresaRepository(private val api: EmpresaApi) {
    suspend fun getEmpresa(idEmpresa: Long): Empresa? {
        // Primero intentamos por ID directo
        try {
            val empresa = api.obtenerEmpresaPorId(idEmpresa)
            if (empresa != null) return empresa
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Si falla (ej. 404), intentamos listar todas y buscar en la lista
        return try {
            val empresas = api.listarEmpresas()
            empresas.find { it.idEmpresa == idEmpresa }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getNombreEmpresa(idEmpresa: Long): String {
        return getEmpresa(idEmpresa)?.nombre ?: "Empresa no disponible"
    }
}