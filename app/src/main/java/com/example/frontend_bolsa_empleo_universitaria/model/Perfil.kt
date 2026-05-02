package com.example.frontend_bolsa_empleo_universitaria.Model

data class Perfil(
    val idPerfil: Long? = null,
    val carrera: String,
    val universidad: String,
    val semestre: String,
    val habilidades: String,       // Guardaremos las áreas de interés aquí separadas por comas
    val promedio: Double? = null,   // Agregado para el Paso 2
    val experiencia: String? = null,
    val cvUrl: String? = null,
    val disponibilidad: String? = null,
    val idUsuario: Long? = null
)
