package com.example.frontend_bolsa_empleo_universitaria.model


data class PostulacionEnriquecida(
val idPostulacion: Long,
val fechaPostulacion: String,
val estado: String,
val idOferta: Long,
val tituloOferta: String,    // de OfertaLaboralResponse
val area: String,            // de OfertaLaboralResponse
val salario: Double,         // de OfertaLaboralResponse
val modalidad: String,       // de OfertaLaboralResponse
val nombreEmpresa: String    // opcional, si puedes obtenerlo
)
