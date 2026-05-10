package com.example.frontend_bolsa_empleo_universitaria.model

data class PostulacionResponse(

        val idPostulacion: Long,
        val fechaPostulacion: String,   // o LocalDate
        val estado: String,             // "PENDIENTE", "EN_REVISION", "ACEPTADA", "RECHAZADA"
        val idUsuario: Long,
        val idOferta: Long
)
