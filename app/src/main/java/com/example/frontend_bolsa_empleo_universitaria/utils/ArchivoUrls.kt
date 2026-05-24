package com.example.frontend_bolsa_empleo_universitaria.utils

object ArchivoUrls {
    const val BASE_URL = "https://backend-sistema-empleo-universitario.onrender.com/"

    fun fotoUsuario(idUsuario: Long, cacheBuster: Long = 0L): String =
        "${BASE_URL}api/archivos/foto/usuario/$idUsuario${cacheQuery(cacheBuster)}"

    fun fotoEmpresa(idEmpresa: Long, cacheBuster: Long = 0L): String =
        "${BASE_URL}api/archivos/foto/empresa/$idEmpresa${cacheQuery(cacheBuster)}"

    private fun cacheQuery(cacheBuster: Long): String =
        if (cacheBuster > 0L) "?t=$cacheBuster" else ""
}
