package com.example.frontend_bolsa_empleo_universitaria.model

import com.google.gson.annotations.SerializedName

data class OfertaLaboral(
    @SerializedName("idOferta", alternate = ["id", "id_oferta"])
    val idOferta: Long = 0,
    val titulo: String = "",
    val descripcion: String = "",
    val area: String = "",
    val salario: Double = 0.0,
    val modalidad: String = "",
    val fechaPublicacion: String = "",
    val fechaCierre: String = "",
    val estado: Boolean = true,
    @SerializedName("idEmpresa", alternate = ["id_empresa", "empresaId", "empresa_id"])
    val idEmpresa: Long = 0
)