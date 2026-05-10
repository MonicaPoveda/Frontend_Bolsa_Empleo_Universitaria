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

        val authInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()

            // Usamos .header() en lugar de .addHeader() para evitar duplicados
            tokenManager?.getToken()?.let { token ->
                requestBuilder.header("Authorization", "Bearer $token")
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }

        // Interceptor para forzar Content-Type limpio sin charset
        val contentTypeInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            if (original.body != null) {
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
            .addInterceptor(authInterceptor)
            .addInterceptor(contentTypeInterceptor) // ✅ NUEVO: fuerza Content-Type sin charset
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
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

    val usuarioApi: UsuarioApi by lazy { retrofit.create(UsuarioApi::class.java) }
    val empresaApi: EmpresaApi by lazy { retrofit.create(EmpresaApi::class.java) }
    val perfilApi: PerfilApi by lazy { retrofit.create(PerfilApi::class.java) }
    val postulacionApi: PostulacionApi by lazy { retrofit.create(PostulacionApi::class.java)}

    val ofertaLaboralApi: OfertaLaboralApi by lazy { retrofit.create(OfertaLaboralApi::class.java) }

    val seguimientoPostulacionApi: SeguimientoPostulacionApi by lazy { retrofit.create(SeguimientoPostulacionApi::class.java) }
    fun init(context: Context) {
        appContext = context.applicationContext
        println("✅ RetrofitClient inicializado con BASE_URL: $BASE_URL")
    }
}