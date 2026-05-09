package com.example.frontend_bolsa_empleo_universitaria.model

data class PostulacionResponse(
    val idPostulacion: Long,
    val fechaPostulacion: String,
    val estado: String, // PENDIENTE, EN_REVISION, ACEPTADA, RECHAZADA
    val idUsuario: Long,
    val idOferta: Long,
    // Datos adicionales que traeremos de la oferta (para mostrar en lista)
    val tituloOferta: String = "",
    val nombreEmpresa: String = "",
    val salario: Double = 0.0,
    val modalidad: String = ""
)