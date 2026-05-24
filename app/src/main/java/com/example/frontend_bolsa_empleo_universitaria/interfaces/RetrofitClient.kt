package com.example.frontend_bolsa_empleo_universitaria.interfaces

import android.content.Context
import com.example.frontend_bolsa_empleo_universitaria.utils.Token
import com.google.gson.GsonBuilder
import okhttp3.ConnectionPool
import okhttp3.MultipartBody
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
        "/api/empresas-pendientes/enviar"
    )

    lateinit var appContext: Context

    private val tokenManager: Token?
        get() = if (::appContext.isInitialized) Token(appContext) else null

    private val retrofit: Retrofit by lazy {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val authInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()

            if (shouldAddAuthorizationHeader(original.url.encodedPath)) {
                tokenManager?.getToken()
                    ?.takeIf { it.isNotBlank() }
                    ?.let { token ->
                        requestBuilder.header("Authorization", "Bearer $token")
                    }
            } else {
                requestBuilder.removeHeader("Authorization")
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }

        val contentTypeInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val body = original.body
            if (body != null && body !is MultipartBody) {
                val newRequest = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(original)
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .addInterceptor(authInterceptor)
            .addInterceptor(contentTypeInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // ✅ APIs - SIN DUPLICADOS
    val usuarioApi: UsuarioApi by lazy { retrofit.create(UsuarioApi::class.java) }
    val empresaApi: EmpresaApi by lazy { retrofit.create(EmpresaApi::class.java) }
    val adminApi: AdminApi by lazy { retrofit.create(AdminApi::class.java) }
    val perfilApi: PerfilApi by lazy { retrofit.create(PerfilApi::class.java) }
    val ofertaLaboralApi: OfertaLaboralApi by lazy { retrofit.create(OfertaLaboralApi::class.java) }
    val postulacionApi: PostulacionApi by lazy { retrofit.create(PostulacionApi::class.java) }  // ← Solo una vez
    val seguimientoPostulacionApi: SeguimientoPostulacionApi by lazy { retrofit.create(SeguimientoPostulacionApi::class.java) }
    val archivoApi: ArchivoApi by lazy { retrofit.create(ArchivoApi::class.java) }

    fun init(context: Context) {
        appContext = context.applicationContext
        println("✅ RetrofitClient inicializado con BASE_URL: $BASE_URL")
    }

    private fun shouldAddAuthorizationHeader(path: String): Boolean {
        return path !in publicAuthPaths
    }
}
