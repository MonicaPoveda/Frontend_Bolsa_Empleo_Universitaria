package com.example.frontend_bolsa_empleo_universitaria.model

data class PostulacionDto(
    val idPostulacion: Long = 0,
    val fechaPostulacion: String = "",
    val estado: String = "PENDIENTE",
    val idUsuario: Long = 0,
    val idOferta: Long = 0,
    // Datos adicionales (puedes agregar más si el backend los devuelve)
    val nombreEstudiante: String = "",
    val emailEstudiante: String = "",
    val tituloOferta: String = ""
)