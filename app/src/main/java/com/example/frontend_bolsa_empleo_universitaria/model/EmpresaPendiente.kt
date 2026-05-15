// EmpresaPendiente.kt
package com.example.frontend_bolsa_empleo_universitaria.model

data class EmpresaPendiente(
    val idEmpresaPendiente: Long,
    val nombre: String,
    val email: String,
    val estado: String,  // "PENDIENTE", "APROBADA", "RECHAZADA", "BLOQUEADA"
    val mensaje: String,
    val rechazos: Int = 0, // Contador de rechazos (máximo 3)
    val actualizada: Boolean = false // Indica si la empresa reenvió datos tras un rechazo
)