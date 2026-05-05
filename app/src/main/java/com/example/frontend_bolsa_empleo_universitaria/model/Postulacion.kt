package com.example.frontend_bolsa_empleo_universitaria.model

import com.google.gson.annotations.SerializedName

data class Postulacion(
    @SerializedName("idPostulacion")
    val idPostulacion: Long? = null,
    val fechaPostulacion: String = "",
    val estado: String = "PENDIENTE",
    val idUsuario: Long? = null,
    val idOferta: Long? = null,

    // Objetos hidratados para la UI (no se envían al backend)
    @Transient
    var oferta: OfertaLaboral? = null,

    @Transient
    var empresa: Empresa? = null,

    @Transient
    var nombreEmpresa: String = "Cargando..."
)