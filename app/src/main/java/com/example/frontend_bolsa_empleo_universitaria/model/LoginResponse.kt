package com.example.frontend_bolsa_empleo_universitaria.model

data class LoginResponse(
    val token: String,
    val usuario: UsuarioDTO
)