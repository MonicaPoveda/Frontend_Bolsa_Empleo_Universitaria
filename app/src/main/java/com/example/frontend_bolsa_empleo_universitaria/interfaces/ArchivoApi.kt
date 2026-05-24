package com.example.frontend_bolsa_empleo_universitaria.interfaces

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ArchivoApi {

    @Multipart
    @POST("api/archivos/foto/usuario/{id}")
    suspend fun subirFotoUsuario(
        @Path("id") id: Long,
        @Part archivo: MultipartBody.Part
    ): Response<Map<String, String>>

    @Multipart
    @PUT("api/archivos/foto/usuario/{id}")
    suspend fun actualizarFotoUsuario(
        @Path("id") id: Long,
        @Part archivo: MultipartBody.Part
    ): Response<Map<String, String>>

    @Multipart
    @POST("api/archivos/foto/empresa/{id}")
    suspend fun subirFotoEmpresa(
        @Path("id") id: Long,
        @Part archivo: MultipartBody.Part
    ): Response<Map<String, String>>

    @Multipart
    @PUT("api/archivos/foto/empresa/{id}")
    suspend fun actualizarFotoEmpresa(
        @Path("id") id: Long,
        @Part archivo: MultipartBody.Part
    ): Response<Map<String, String>>

    @Multipart
    @POST("api/archivos/documento/empresa/{id}")
    suspend fun subirDocumentoEmpresa(
        @Path("id") id: Long,
        @Part archivo: MultipartBody.Part
    ): Response<Map<String, String>>

    @Multipart
    @PUT("api/archivos/documento/empresa/{id}")
    suspend fun actualizarDocumentoEmpresa(
        @Path("id") id: Long,
        @Part archivo: MultipartBody.Part
    ): Response<Map<String, String>>

    @GET("api/archivos/documento/empresa/{id}")
    @Streaming
    suspend fun obtenerDocumentoEmpresa(
        @Path("id") id: Long
    ): Response<ResponseBody>

    @Multipart
    @POST("api/archivos/documento/empresa-pendiente/{id}")
    suspend fun subirDocumentoEmpresaPendiente(
        @Path("id") id: Long,
        @Part archivo: MultipartBody.Part
    ): Response<Map<String, String>>

    @Multipart
    @PUT("api/archivos/documento/empresa-pendiente/{id}")
    suspend fun actualizarDocumentoEmpresaPendiente(
        @Path("id") id: Long,
        @Part archivo: MultipartBody.Part
    ): Response<Map<String, String>>

    @GET("api/archivos/documento/empresa-pendiente/{id}")
    @Streaming
    suspend fun obtenerDocumentoEmpresaPendiente(
        @Path("id") id: Long
    ): Response<ResponseBody>
}
