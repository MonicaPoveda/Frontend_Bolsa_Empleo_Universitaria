package com.example.frontend_bolsa_empleo_universitaria.model


data class UsuarioDTO(
    val idUsuario: Long,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String?,
    val tipoUsuario: String, // EST EMPR ADMIN
    val fechaRegistro: String,
    val estado: Boolean
)