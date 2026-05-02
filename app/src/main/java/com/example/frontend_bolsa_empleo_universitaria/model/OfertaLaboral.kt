package com.example.frontend_bolsa_empleo_universitaria.model

data class OfertaLaboral(
    val idOferta: Long = 0,
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
