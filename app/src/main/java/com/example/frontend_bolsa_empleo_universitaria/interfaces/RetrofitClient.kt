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

    lateinit var appContext: Context

    private val tokenManager: Token?
        get() = if (::appContext.isInitialized) Token(appContext) else null

    private val retrofit: Retrofit by lazy {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Interceptor para agregar el token automáticamente
        val authInterceptor = okhttp3.Interceptor { chain ->
            var request = chain.request()

            println("🔐 ===== INTERCEPTOR ===== 🔐")
            println("📡 URL: ${request.url}")
            println("📡 Método: ${request.method}")

            // Agregar token si existe
            tokenManager?.getToken()?.let { token ->
                println("✅ Token encontrado: ${token.take(50)}...")
                request = request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                println("✅ Header Authorization agregado")
            } ?: run {
                println("❌ No hay token disponible")
            }

            val response = chain.proceed(request)
            println("📡 Código respuesta: ${response.code}")

            if (response.code == 403) {
                println("❌ ERROR 403 - Posibles causas:")
                println("   1. Token inválido o expirado")
                println("   2. Usuario no tiene rol EMPRESA")
                println("   3. Empresa no aprobada por administrador")
                println("   4. El token no se envió correctamente")
            }

            response
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .serializeNulls()
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val usuarioApi: UsuarioApi by lazy { retrofit.create(UsuarioApi::class.java) }
    val empresaApi: EmpresaApi by lazy { retrofit.create(EmpresaApi::class.java) }
    val ofertaLaboralApi: OfertaLaboralApi by lazy { retrofit.create(OfertaLaboralApi::class.java) }

    fun init(context: Context) {
        appContext = context.applicationContext
        println("✅ RetrofitClient inicializado")
    }
}