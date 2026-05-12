package com.example.frontend_bolsa_empleo_universitaria.model

/**
 * Filtros combinados para la búsqueda de ofertas (estudiante).
 * Carrera → área / descripción; oficio → título + descripción; cargo → título.
 */
data class OfertaSearchFilters(
    val textoLibre: String = "",
    val nombreEmpresa: String = "",
    val cargo: String = "",
    val carrera: String = "",
    val oficio: String = "",
    val salarioMin: Double? = null,
    val salarioMax: Double? = null,
    val modalidad: String? = null,
    val categoria: String = "Todas"
)
