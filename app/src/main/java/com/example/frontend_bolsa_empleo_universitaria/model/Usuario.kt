package com.example.frontend_bolsa_empleo_universitaria.Model

// model/Usuario.kt
data class Usuario(
    val idUsuario: Long? = null,
    val nombre: String,
    val apellido: String,
    val identificacion: String? = null, // Agregado para el Paso 1
    val email: String,
    val telefono: String?,
    val tipoUsuario: String,       // "ESTUDIANTE" o "EGRESADO"
    val fechaRegistro: String,
    val estado: Boolean,
    val password: String? = null,
    val version: Int? = null
)
