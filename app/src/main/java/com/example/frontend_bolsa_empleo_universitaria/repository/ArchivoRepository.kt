package com.example.frontend_bolsa_empleo_universitaria.repository

import android.content.Context
import android.net.Uri
import com.example.frontend_bolsa_empleo_universitaria.interfaces.ArchivoApi
import com.example.frontend_bolsa_empleo_universitaria.utils.FileValidation
import com.example.frontend_bolsa_empleo_universitaria.utils.PdfOpener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ArchivoRepository(
    private val api: ArchivoApi,
    private val context: Context
) {

    suspend fun subirFotoUsuario(idUsuario: Long, uri: Uri, replaceExisting: Boolean = false): Result<Unit> =
        uploadImage(uri, replaceExisting) { part ->
            if (replaceExisting) api.actualizarFotoUsuario(idUsuario, part)
            else api.subirFotoUsuario(idUsuario, part)
        }

    suspend fun subirFotoEmpresa(idEmpresa: Long, uri: Uri, replaceExisting: Boolean = false): Result<Unit> =
        uploadImage(uri, replaceExisting) { part ->
            if (replaceExisting) api.actualizarFotoEmpresa(idEmpresa, part)
            else api.subirFotoEmpresa(idEmpresa, part)
        }

    suspend fun subirDocumentoEmpresa(idEmpresa: Long, uri: Uri, replaceExisting: Boolean = false): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                FileValidation.validatePdf(context, uri).getOrElse { return@withContext Result.failure(it) }
                val part = createPdfPart(uri)
                val response = if (replaceExisting) {
                    api.actualizarDocumentoEmpresa(idEmpresa, part)
                } else {
                    api.subirDocumentoEmpresa(idEmpresa, part)
                }
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(httpErrorMessage(response.code(), response.errorBody()?.string())))
                }
            } catch (e: HttpException) {
                Result.failure(Exception(httpErrorMessage(e.code(), e.response()?.errorBody()?.string())))
            } catch (e: IOException) {
                Result.failure(Exception("Error de red al subir documento: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Error al subir documento"))
            }
        }

    // ✅ Nuevo: Subir documento para empresa en estado pendiente
    suspend fun subirDocumentoEmpresaPendiente(idEmpresaPendiente: Long, uri: Uri, replaceExisting: Boolean = false): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                FileValidation.validatePdf(context, uri).getOrElse { return@withContext Result.failure(it) }
                val part = createPdfPart(uri)
                val response = if (replaceExisting) {
                    api.actualizarDocumentoEmpresaPendiente(idEmpresaPendiente, part)
                } else {
                    api.subirDocumentoEmpresaPendiente(idEmpresaPendiente, part)
                }
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(httpErrorMessage(response.code(), response.errorBody()?.string())))
                }
            } catch (e: HttpException) {
                Result.failure(Exception(httpErrorMessage(e.code(), e.response()?.errorBody()?.string())))
            } catch (e: IOException) {
                Result.failure(Exception("Error de red al subir documento: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Error al subir documento"))
            }
        }

    suspend fun descargarYAbrirDocumentoEmpresa(idEmpresa: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.obtenerDocumentoEmpresa(idEmpresa)
                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        Exception(httpErrorMessage(response.code(), response.errorBody()?.string()))
                    )
                }
                val bytes = response.body()?.bytes()
                    ?: return@withContext Result.failure(Exception("El documento está vacío o no existe."))
                if (bytes.isEmpty()) {
                    return@withContext Result.failure(Exception("La empresa aún no ha subido un documento."))
                }
                val file = PdfOpener.saveToCache(context, bytes, "documento_empresa_$idEmpresa.pdf")
                PdfOpener.openPdfFile(context, file)
            } catch (e: HttpException) {
                Result.failure(Exception(httpErrorMessage(e.code(), e.response()?.errorBody()?.string())))
            } catch (e: IOException) {
                Result.failure(Exception("Error de red al descargar documento: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ✅ Nuevo: Descargar documento de empresa pendiente
    suspend fun descargarYAbrirDocumentoEmpresaPendiente(idEmpresaPendiente: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.obtenerDocumentoEmpresaPendiente(idEmpresaPendiente)
                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        Exception(httpErrorMessage(response.code(), response.errorBody()?.string()))
                    )
                }
                val bytes = response.body()?.bytes()
                    ?: return@withContext Result.failure(Exception("El documento está vacío o no existe."))
                val file = PdfOpener.saveToCache(context, bytes, "documento_pendiente_$idEmpresaPendiente.pdf")
                PdfOpener.openPdfFile(context, file)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private suspend fun uploadImage(
        uri: Uri,
        replaceExisting: Boolean,
        call: suspend (MultipartBody.Part) -> retrofit2.Response<Map<String, String>>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            FileValidation.validateImage(context, uri).getOrElse { return@withContext Result.failure(it) }
            val part = createImagePart(uri)
            val response = call(part)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(httpErrorMessage(response.code(), response.errorBody()?.string())))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(httpErrorMessage(e.code(), e.response()?.errorBody()?.string())))
        } catch (e: IOException) {
            Result.failure(Exception("Error de red al subir imagen: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error al subir imagen"))
        }
    }

    private fun createImagePart(uri: Uri): MultipartBody.Part {
        val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IllegalArgumentException("No se pudo leer la imagen seleccionada.")
        val body = bytes.toRequestBody(mime.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("archivo", "foto.jpg", body)
    }

    private fun createPdfPart(uri: Uri): MultipartBody.Part {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IllegalArgumentException("No se pudo leer el PDF seleccionado.")
        val body = bytes.toRequestBody("application/pdf".toMediaTypeOrNull())
        val name = uri.lastPathSegment?.takeIf { it.endsWith(".pdf", ignoreCase = true) } ?: "documento.pdf"
        return MultipartBody.Part.createFormData("archivo", name, body)
    }

    private fun httpErrorMessage(code: Int, rawBody: String?): String {
        val parsed = try {
            JSONObject(rawBody ?: "{}").optString("message").takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
        return parsed ?: rawBody?.takeIf { it.isNotBlank() } ?: "Error HTTP $code"
    }
}
