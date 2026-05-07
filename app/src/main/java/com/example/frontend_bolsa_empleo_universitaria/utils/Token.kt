package com.example.frontend_bolsa_empleo_universitaria.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class Token(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USUARIO_EMAIL_KEY = stringPreferencesKey("usuario_email")
        private val USUARIO_ROL_KEY = stringPreferencesKey("usuario_rol")
    }

    suspend fun saveToken(token: String, email: String, rol: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USUARIO_EMAIL_KEY] = email
            preferences[USUARIO_ROL_KEY] = rol
        }
    }

    fun getTokenFlow(): Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }

    fun getRolFlow(): Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[USUARIO_ROL_KEY]
        }

    fun getEmailFlow(): Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[USUARIO_EMAIL_KEY]
        }

    suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }
}