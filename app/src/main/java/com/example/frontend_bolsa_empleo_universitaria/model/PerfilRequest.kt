package com.example.frontend_bolsa_empleo_universitaria.model

data class PerfilRequest(
    val carrera: String,
    val universidad: String,
    val semestre: String,
    val habilidades: String,
    val experiencia: String,
    val cvUrl: String,
    val disponibilidad: String,
    val idUsuario: Long
)