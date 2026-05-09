package com.example.frontend_bolsa_empleo_universitaria.model

data class LoginResponseEmpresa(
    val token: String,
    val empresa: EmpresaDto,
    val usuario: UsuarioDTO? = null
) {
    // El rol siempre será EMPRESA para este tipo de login
    val rol: String get() = "EMPRESA"
    val email: String get() = empresa.email
}
