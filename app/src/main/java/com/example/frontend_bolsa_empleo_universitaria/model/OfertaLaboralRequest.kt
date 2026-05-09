package com.example.frontend_bolsa_empleo_universitaria.model

data class OfertaLaboralRequest(
    val titulo: String = "",
    val descripcion: String = "",
    val area: String = "",
    val salario: Double = 0.0,
    val modalidad: String = "",
    val fechaPublicacion: String = "",
    val fechaCierre: String = "",
    val estado: Boolean = true,
    val idEmpresa: Long = 0
)