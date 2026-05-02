package com.example.frontend_bolsa_empleo_universitaria.repository

import com.example.frontend_bolsa_empleo_universitaria.interfaces.EmpresaApi

class EmpresaRepository(private val api: EmpresaApi) {
    suspend fun getNombreEmpresa(idEmpresa: Long): String {
        return api.listarEmpresas()
            .find { it.idEmpresa == idEmpresa }
            ?.nombre ?: "Empresa no disponible"
    }
}