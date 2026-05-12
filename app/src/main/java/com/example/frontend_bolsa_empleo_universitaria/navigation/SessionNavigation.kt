package com.example.frontend_bolsa_empleo_universitaria.navigation

import com.example.frontend_bolsa_empleo_universitaria.utils.Token

/**
 * Destino inicial tras splash según sesión guardada (JWT + rol + perfil estudiante).
 */
fun resolvePostSplashRoute(token: Token): String {
    if (!token.isLoggedIn()) return "login"
    return when {
        token.isAdmin() -> "admin_home"
        token.isEmpresa() -> "empresa_home"
        token.isEstudiante() -> {
            if (token.isProfileCreated()) "estudiante_home"
            else "mensaje_alerta_crear_perfil"
        }
        else -> "login"
    }
}
