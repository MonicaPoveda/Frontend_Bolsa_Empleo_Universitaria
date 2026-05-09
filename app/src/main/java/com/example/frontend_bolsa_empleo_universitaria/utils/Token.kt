package com.example.frontend_bolsa_empleo_universitaria.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class Token(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // MODIFICADO: Agregar parámetro idEmpresa
    fun saveToken(token: String, email: String, rol: String, idEmpresa: Long = 0, nombre: String = "") {
        prefs.edit().apply {
            putString("auth_token", token)
            putString("user_email", email)
            putString("user_role", rol)
            putString("user_nombre", nombre)
            putLong("empresa_id", idEmpresa)  // ← NUEVO: Guardar ID de empresa
            apply()
        }
    }

    fun getToken(): String? = prefs.getString("auth_token", null)

    fun getUserEmail(): String? = prefs.getString("user_email", null)

    fun getUserRole(): String? = prefs.getString("user_role", null)

    fun getNombre(): String? = prefs.getString("user_nombre", null)

    // NUEVO: Obtener ID de la empresa
    fun getEmpresaId(): Long = prefs.getLong("empresa_id", 0)

    // NUEVO: Verificar si hay una empresa logueada
    fun hasEmpresaId(): Boolean = getEmpresaId() > 0

    fun isEstudiante(): Boolean = getUserRole() == "ESTUDIANTE"

    fun isEmpresa(): Boolean = getUserRole() == "EMPRESA"

    fun isAdmin(): Boolean = getUserRole() == "ADMIN"

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()
}