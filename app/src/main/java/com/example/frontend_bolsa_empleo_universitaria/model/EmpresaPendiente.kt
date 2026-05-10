// EmpresaPendiente.kt
package com.example.frontend_bolsa_empleo_universitaria.model

data class EmpresaPendiente(
    val idEmpresaPendiente: Long,
    val nombre: String,
    val email: String,
    val estado: String,  // "PENDIENTE", "APROBADA", "RECHAZADA"
    val mensaje: String
)