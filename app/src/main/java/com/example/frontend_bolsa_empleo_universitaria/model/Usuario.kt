package com.example.frontend_bolsa_empleo_universitaria.Model

// model/Usuario.kt
data class Usuario(
    val idUsuario: Long? = null,    // Cambiado a nullable para que sea null en nuevos registros
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String?,
    val tipoUsuario: String,       // "ESTUDIANTE" o "EGRESADO"
    val fechaRegistro: String,
    val estado: Boolean,
    val password: String? = null,   // Se requiere para el registro
)
