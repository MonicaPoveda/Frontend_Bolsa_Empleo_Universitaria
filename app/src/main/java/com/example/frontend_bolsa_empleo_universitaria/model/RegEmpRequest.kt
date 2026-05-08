package com.example.frontend_bolsa_empleo_universitaria.model

data class RegEmpRequest(
    val nombre: String,
    val sector: String,
    val descripcion: String,
    val email: String,
    val telefono: String,
    val ciudad: String,
    val password: String
)
