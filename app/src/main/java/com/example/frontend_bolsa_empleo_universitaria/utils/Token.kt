package com.example.frontend_bolsa_empleo_universitaria.utils

import android.content.Context
import android.content.SharedPreferences

class Token(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? =
        prefs.getString("jwt_token", null)?.takeIf { it.isNotBlank() }
            ?: prefs.getString("auth_token", null)?.takeIf { it.isNotBlank() }

    fun saveUserEmail(email: String) {
        prefs.edit().putString("user_email", email).apply()
    }

    fun getUserEmail(): String? = prefs.getString("user_email", null)

    fun saveUserId(id: Long) {
        prefs.edit().putLong("user_id", id).apply()
    }

    fun getUserId(): Long? {
        if (!prefs.contains("user_id")) return null
        val id = prefs.getLong("user_id", -1)
        return id.takeIf { it > 0L }
    }
    fun getUserTelefono(): String = prefs.getString("user_telefono", "") ?: ""
    fun saveUserName(nombre: String, apellido: String) {
        prefs.edit()
            .putString("user_nombre", nombre)
            .putString("user_apellido", apellido)
            .apply()
    }

    fun saveUserTelefono(telefono: String) {
        prefs.edit().putString("user_telefono", telefono).apply()
    }

    fun getUserNombre(): String = prefs.getString("user_nombre", "Usuario") ?: "Usuario"
    fun getUserApellido(): String = prefs.getString("user_apellido", "") ?: ""

    // Métodos para el perfil
    fun setProfileCreated(created: Boolean) {
        prefs.edit().putBoolean("profile_created", created).commit()
    }

    fun isProfileCreated(): Boolean = prefs.getBoolean("profile_created", false)

    fun setUserType(userType: String) {
        prefs.edit().putString("user_type", userType).apply()
    }

    fun getUserType(): String? = prefs.getString("user_type", null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }


    // MODIFICADO: Agregar parámetros idUsuario y nombre/apellido
    // Dentro de la clase Token
    fun saveToken(token: String, email: String, rol: String, idEmpresa: Long = 0, idUsuario: Long = -1, nombre: String = "", apellido: String = "", telefono: String = "") {
        prefs.edit().apply {
            putString("jwt_token", token)
            putString("auth_token", token)
            putString("user_email", email)
            putString("user_role", rol)
            putLong("empresa_id", idEmpresa)
            putLong("user_id", idUsuario)
            putString("user_nombre", nombre)
            putString("user_apellido", apellido)
            putString("user_telefono", telefono)
            commit()
        }
    }




    fun getUserRole(): String? = prefs.getString("user_role", null)

    // NUEVO: Obtener ID de la empresa
    fun getEmpresaId(): Long = prefs.getLong("empresa_id", 0)

    // NUEVO: Verificar si hay una empresa logueada
    fun hasEmpresaId(): Boolean = getEmpresaId() > 0

    fun isEstudiante(): Boolean {
        val role = getUserRole()
        val userType = getUserType()
        return role == "ESTUDIANTE" || role == "EGRESADO" ||
            userType == "ESTUDIANTE" || userType == "EGRESADO"
    }

    fun isEmpresa(): Boolean = getUserRole() == "EMPRESA"

    fun isAdmin(): Boolean = getUserRole() == "ADMIN"



    fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()
}
