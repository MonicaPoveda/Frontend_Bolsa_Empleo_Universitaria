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

    //private const val BASE_URL = "https://backend-sistema-empleo-universitario.onrender.com/"
    private const val BASE_URL = "https://backend-sistema-empleo-universitario.onrender.com/"

    // Variable para contexto (debes inicializarlo desde Application)
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

            // Agregar token si existe
            tokenManager?.getToken()?.let { token ->
                request = request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            }

            val response = chain.proceed(request)
            response
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)  // Primero el auth
            .addInterceptor(logging)          // Después el logging
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
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

    // Función para inicializar el contexto (llamar desde Application)
    fun init(context: Context) {
        appContext = context.applicationContext
    }
}