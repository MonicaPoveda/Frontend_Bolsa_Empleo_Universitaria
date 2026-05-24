package com.example.frontend_bolsa_empleo_universitaria.model

data class SolicitudRegistroEmpresa(
    val idEmpresaPendiente: Long? = null,  // Para actualización
    val nombre: String,
    val email: String,
    val password: String?,  // null cuando no se cambia
    val sector: String,
    val telefono: String,
    val ciudad: String,
    val descripcion: String,
    val estado: String? = null,      // opcional
    val mensaje: String? = null,     // opcional
    val rechazos: Int? = null,       // opcional
    val actualizada: Boolean? = null // opcional
)
