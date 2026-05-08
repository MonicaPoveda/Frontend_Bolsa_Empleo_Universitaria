package com.example.frontend_bolsa_empleo_universitaria.model

import com.google.gson.annotations.SerializedName
data class EmpresaDto(
    @SerializedName("idEmpresa", alternate = ["id", "id_empresa"])
    val idEmpresa: Long = 0,
    val nombre: String = "",
    val sector: String = "",
    val descripcion: String = "",
    val email: String = "",
    val telefono: String = "",
    val ciudad: String = ""
)

