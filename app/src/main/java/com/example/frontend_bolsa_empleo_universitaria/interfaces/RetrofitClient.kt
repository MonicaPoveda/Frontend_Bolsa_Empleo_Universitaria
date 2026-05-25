package com.example.frontend_bolsa_empleo_universitaria.interfaces

import android.content.Context
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://backend-sistema-empleo-universitario.onrender.com/"

    private val publicAuthPaths = setOf(
        "/api/usuarios/login",
        "/api/usuarios/recuperar-password",
        "/api/usuarios/guardar",
        "/api/empresas/login",
        "/api/empresas/recuperar-password",
        "/api/empresas/guardar",
        "/api/empresas-pendientes/enviar",
        "/api/empresas-pendientes/listar",
        // ✅ Solo el documento de empresa pendiente es público durante el registro
        "/api/archivos/documento/empresa-pendiente"
    )

    lateinit var appContext: Context

    private val tokenManager: Token?
        get() = if (::appContext.isInitialized) Token(appContext) else null

    private val retrofit: Retrofit by lazy {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            val path = original.url.encodedPath

            // Verificamos si la ruta es pública
            val isPublic = publicAuthPaths.any { path.contains(it, ignoreCase = true) }

            if (!isPublic) {
                tokenManager?.getToken()
                    ?.takeIf { it.isNotBlank() }
                    ?.let { token ->
                        requestBuilder.header("Authorization", "Bearer $token")
                    }
            } else {
                // Para rutas públicas, nos aseguramos de NO enviar basura de sesiones previas
                requestBuilder.removeHeader("Authorization")
            }

            chain.proceed(requestBuilder.build())
        }

        val contentTypeInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val path = original.url.encodedPath
            
            // Si es una ruta de archivos, NO tocamos los headers para evitar romper el boundary de multipart
            if (path.contains("/api/archivos/", ignoreCase = true)) {
                return@Interceptor chain.proceed(original)
            }

            val requestBuilder = original.newBuilder()
            val body = original.body
            
            if (body != null) {
                val contentType = body.contentType()?.toString() ?: ""
                if (!contentType.contains("multipart", ignoreCase = true)) {
                    requestBuilder.header("Content-Type", "application/json")
                }
            }
            
            requestBuilder.header("Accept", "application/json")
            chain.proceed(requestBuilder.build())
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(contentTypeInterceptor)
            .addInterceptor(logging)
            .connectTimeout(180, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(180, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val usuarioApi: UsuarioApi by lazy { retrofit.create(UsuarioApi::class.java) }
    val empresaApi: EmpresaApi by lazy { retrofit.create(EmpresaApi::class.java) }
    val adminApi: AdminApi by lazy { retrofit.create(AdminApi::class.java) }
    val perfilApi: PerfilApi by lazy { retrofit.create(PerfilApi::class.java) }
    val ofertaLaboralApi: OfertaLaboralApi by lazy { retrofit.create(OfertaLaboralApi::class.java) }
    val postulacionApi: PostulacionApi by lazy { retrofit.create(PostulacionApi::class.java) }
    val seguimientoPostulacionApi: SeguimientoPostulacionApi by lazy { retrofit.create(SeguimientoPostulacionApi::class.java) }
    val archivoApi: ArchivoApi by lazy { retrofit.create(ArchivoApi::class.java) }

    fun init(context: Context) {
        appContext = context.applicationContext
    }
}
