package com.example.frontend_bolsa_empleo_universitaria.model



data class SolicitudRegistroEmpresa(
    val nombre: String,
    val email: String,
    val password: String
    // NOTA: No incluye sector, descripcion, telefono, ciudad en la solicitud inicial
)
