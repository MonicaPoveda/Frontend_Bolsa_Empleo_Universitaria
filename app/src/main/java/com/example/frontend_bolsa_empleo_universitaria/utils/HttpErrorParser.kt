package com.example.frontend_bolsa_empleo_universitaria.utils

import org.json.JSONObject
import retrofit2.Response

object HttpErrorParser {

    fun fromResponse(response: Response<*>): String {
        val raw = try {
            response.errorBody()?.string()
        } catch (_: Exception) {
            null
        }
        return fromBody(response.code(), raw)
    }

    fun fromBody(code: Int, rawBody: String?): String {
        if (!rawBody.isNullOrBlank()) {
            try {
                val json = JSONObject(rawBody)
                json.optString("message").takeIf { it.isNotBlank() }?.let { return it }
                json.optString("error").takeIf { it.isNotBlank() }?.let { return it }
            } catch (_: Exception) {
                if (rawBody.length < 200) return rawBody
            }
        }
        return when (code) {
            400 -> "Datos inválidos. Revisa el formulario."
            401 -> "No autorizado."
            403 -> "Acceso denegado."
            404 -> "Recurso no encontrado."
            409 -> "Conflicto con datos existentes."
            in 500..599 -> "Error del servidor ($code). Intenta más tarde."
            else -> "Error HTTP $code"
        }
    }
}
