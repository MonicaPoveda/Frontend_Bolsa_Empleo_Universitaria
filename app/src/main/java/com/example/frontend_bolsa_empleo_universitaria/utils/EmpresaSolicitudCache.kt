package com.example.frontend_bolsa_empleo_universitaria.utils

import android.content.Context

data class EmpresaSolicitudDraft(
    val nombre: String,
    val email: String,
    val sector: String,
    val telefono: String,
    val ciudad: String,
    val descripcion: String,
    val idEmpresaPendiente: Long? = null
)

/**
 * Borrador local de solicitud empresarial (sin archivos).
 * Clave por email normalizado — el backend no expone GET por email.
 */
class EmpresaSolicitudCache(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(draft: EmpresaSolicitudDraft) {
        val key = normalizeEmail(draft.email)
        prefs.edit()
            .putString("$key.nombre", draft.nombre)
            .putString("$key.email", draft.email)
            .putString("$key.sector", draft.sector)
            .putString("$key.telefono", draft.telefono)
            .putString("$key.ciudad", draft.ciudad)
            .putString("$key.descripcion", draft.descripcion)
            .apply {
                if (draft.idEmpresaPendiente != null) {
                    putLong("$key.id_pendiente", draft.idEmpresaPendiente)
                } else {
                    remove("$key.id_pendiente")
                }
            }
            .apply()
    }

    fun load(email: String): EmpresaSolicitudDraft? {
        val key = normalizeEmail(email)
        val storedEmail = prefs.getString("$key.email", null) ?: return null
        return EmpresaSolicitudDraft(
            nombre = prefs.getString("$key.nombre", "") ?: "",
            email = storedEmail,
            sector = prefs.getString("$key.sector", "") ?: "",
            telefono = prefs.getString("$key.telefono", "") ?: "",
            ciudad = prefs.getString("$key.ciudad", "") ?: "",
            descripcion = prefs.getString("$key.descripcion", "") ?: "",
            idEmpresaPendiente = prefs.getLong("$key.id_pendiente", -1L).takeIf { it > 0L }
        )
    }

    fun clear(email: String) {
        val key = normalizeEmail(email)
        prefs.edit()
            .remove("$key.nombre")
            .remove("$key.email")
            .remove("$key.sector")
            .remove("$key.telefono")
            .remove("$key.ciudad")
            .remove("$key.descripcion")
            .remove("$key.id_pendiente")
            .apply()
    }

    private fun normalizeEmail(email: String): String =
        email.trim().lowercase()

    companion object {
        private const val PREFS_NAME = "empresa_solicitud_cache"
    }
}
