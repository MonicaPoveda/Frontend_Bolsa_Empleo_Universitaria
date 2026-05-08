package com.example.frontend_bolsa_empleo_universitaria.model

data class RegUsuRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val password: String,
    val tipoUsuario: String = "ESTUDIANTE"
)
