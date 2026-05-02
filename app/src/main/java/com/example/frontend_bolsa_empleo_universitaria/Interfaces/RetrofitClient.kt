package com.example.frontend_bolsa_empleo_universitaria.Interfaces

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.20.36:8080/"

    val api: OfertaLaboralApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OfertaLaboralApi::class.java)
    }
}