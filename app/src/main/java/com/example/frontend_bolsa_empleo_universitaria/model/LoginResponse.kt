package com.example.frontend_bolsa_empleo_universitaria.model

data class LoginResponse(
    val usuario: UsuarioDTO,
    val token: String
) {
    // Propiedades calculadas para facilitar el acceso sin cambiar todo el ViewModel
    val rol: String get() = usuario.tipoUsuario
    val email: String get() = usuario.email
    val nombre: String get() = usuario.nombre
}
