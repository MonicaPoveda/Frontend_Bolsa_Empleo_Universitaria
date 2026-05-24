package com.example.frontend_bolsa_empleo_universitaria.model

import com.google.gson.annotations.SerializedName

data class EmpresaPendiente(
    @SerializedName("idEmpresaPendiente", alternate = ["id", "id_empresa_pendiente", "id_pendiente"])
    val idEmpresaPendiente: Long,
    val nombre: String,
    val email: String,
    val estado: String,  // "PENDIENTE", "APROBADA", "RECHAZADA", "BLOQUEADA"
    val mensaje: String,
    
    @SerializedName("sector", alternate = ["sector_empresa", "sector_economico"])
    val sector: String? = null,
    
    @SerializedName("telefono", alternate = ["tel", "telefono_contacto"])
    val telefono: String? = null,
    
    @SerializedName("ciudad", alternate = ["ubicacion", "ciudad_sede"])
    val ciudad: String? = null,
    
    @SerializedName("descripcion", alternate = ["resumen", "perfil"])
    val descripcion: String? = null,
    
    val rechazos: Int = 0, // Contador de rechazos (máximo 3)
    val actualizada: Boolean = false // Indica si la empresa reenvió datos tras un rechazo
)
