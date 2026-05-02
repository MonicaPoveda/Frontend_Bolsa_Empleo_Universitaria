package com.example.frontend_bolsa_empleo_universitaria.model

data class Perfil(

    val idPerfil: Long,
    val carrera: String,
    val universidad: String,
    val semestre: String,
    val habilidades: String,
    val experiencia: String,
    val cvUrl: String,
    val disponibilidad: String,
    val idUsuario: Long

)