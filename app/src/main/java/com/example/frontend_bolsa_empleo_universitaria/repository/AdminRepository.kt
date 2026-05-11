package com.example.frontend_bolsa_empleo_universitaria.repository

import android.content.Context
import com.example.frontend_bolsa_empleo_universitaria.interfaces.AdminApi
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient
import com.example.frontend_bolsa_empleo_universitaria.model.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import com.example.frontend_bolsa_empleo_universitaria.utils.Token

class AdminRepository(private val context: Context) {

    // Usamos el cliente compartido para asegurar consistencia en la autenticación
    private val api: AdminApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val tokenManager = Token(context)

        val authInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            tokenManager.getToken()?.let { token ->
                requestBuilder.header("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

        Retrofit.Builder()
            .baseUrl("https://backend-sistema-empleo-universitario.onrender.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AdminApi::class.java)
    }

    suspend fun listarEmpresasPendientes(): Response<List<EmpresaPendiente>> = api.listarEmpresasPendientes()
    suspend fun aprobarEmpresa(id: Long): Response<EmpresaDto> = api.aprobarEmpresa(id)
    suspend fun rechazarEmpresa(id: Long): Response<Void> = api.rechazarEmpresa(id)
    suspend fun listarEmpresasAceptadas(): Response<List<EmpresaDto>> = api.listarEmpresasAceptadas()
    
    // ✅ CORRECCIÓN: Usamos el mismo endpoint que las empresas y filtramos para asegurar que vemos los datos
    suspend fun listarOfertasPorEmpresa(idEmpresa: Long): Response<List<OfertaLaboralResponse>> {
        return try {
            // Llamamos al listado general que ya funciona para estudiantes/empresas
            val response = RetrofitClient.ofertaLaboralApi.listar()
            if (response.isSuccessful) {
                val todas = response.body() ?: emptyList()
                // Filtramos por la empresa seleccionada
                val filtradas = todas.filter { it.idEmpresa == idEmpresa }
                Response.success(filtradas)
            } else {
                response
            }
        } catch (e: Exception) {
            // Fallback al endpoint de admin si el anterior falla
            api.listarOfertasPorEmpresa(idEmpresa)
        }
    }

    suspend fun listarPostulacionesPorOferta(idOferta: Long): Response<List<PostulacionDto>> = api.listarPostulacionesPorOferta(idOferta)
}
