package com.example.frontend_bolsa_empleo_universitaria.model

data class OfertaLaboral(
    @Transient
    val idOferta: Long = 0,
    val titulo: String = "",
    val descripcion: String = "",
    val area: String = "",
    val salario: Double = 0.0,
    val modalidad: String = "",
    val fechaPublicacion: String = "",  // ← String en lugar de LocalDate
    val fechaCierre: String = "",       // ← String en lugar de LocalDate
    val estado: Boolean = true,
    val idEmpresa: Long = 0
)