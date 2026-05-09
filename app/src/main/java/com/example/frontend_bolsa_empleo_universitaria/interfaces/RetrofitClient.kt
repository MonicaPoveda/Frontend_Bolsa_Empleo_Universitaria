package com.example.frontend_bolsa_empleo_universitaria.interfaces

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://backend-sistema-empleo-universitario.onrender.com/"
    //private const val BASE_URL = "http://192.168.10.43:8080/"

    private val retrofit: Retrofit by lazy {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)  // Reducido de 60 a 15
            .readTimeout(15, TimeUnit.SECONDS)     // Reducido de 60 a 15
            .writeTimeout(15, TimeUnit.SECONDS)    // Reducido de 60 a 15
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
}