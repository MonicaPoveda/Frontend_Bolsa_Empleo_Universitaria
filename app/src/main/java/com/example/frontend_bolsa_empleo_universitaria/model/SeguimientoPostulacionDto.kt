package com.example.frontend_bolsa_empleo_universitaria.model

data class SeguimientoPostulacionDto(
    val idSeguimiento: Long = 0,
    val fechaCambio: String = "",
    val estadoAnterior: String = "",
    val estadoNuevo: String = "",
    val observacion: String = "",
    val idPostulacion: Long = 0
)