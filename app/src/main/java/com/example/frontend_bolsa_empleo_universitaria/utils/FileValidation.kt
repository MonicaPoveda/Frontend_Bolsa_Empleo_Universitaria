package com.example.frontend_bolsa_empleo_universitaria.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap

object FileValidation {

  private const val MAX_IMAGE_BYTES = 10 * 1024 * 1024L
  private const val MAX_PDF_BYTES = 10 * 1024 * 1024L

  private val allowedImageMimeTypes = setOf(
      "image/jpeg",
      "image/jpg",
      "image/png",
      "image/webp",
      "image/gif"
  )

  fun validateImage(context: Context, uri: Uri): Result<Unit> {
      val mime = resolveMimeType(context, uri)
      if (mime == null || mime !in allowedImageMimeTypes) {
          return Result.failure(IllegalArgumentException("Solo se permiten imágenes JPG, PNG, WEBP o GIF."))
      }
      return validateSize(context, uri, MAX_IMAGE_BYTES, "La imagen no puede superar 10 MB.")
  }

  fun validatePdf(context: Context, uri: Uri): Result<Unit> {
      val mime = resolveMimeType(context, uri)
      val isPdfMime = mime == "application/pdf"
      val isPdfName = uri.lastPathSegment?.lowercase()?.endsWith(".pdf") == true
      if (!isPdfMime && !isPdfName) {
          return Result.failure(IllegalArgumentException("Solo se permiten archivos PDF."))
      }
      return validateSize(context, uri, MAX_PDF_BYTES, "El PDF no puede superar 10 MB.")
  }

  private fun validateSize(context: Context, uri: Uri, maxBytes: Long, message: String): Result<Unit> {
      val size = context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length }
          ?: context.contentResolver.openInputStream(uri)?.use { it.available().toLong() }
          ?: -1L
      if (size > maxBytes) {
          return Result.failure(IllegalArgumentException(message))
      }
      return Result.success(Unit)
  }

  private fun resolveMimeType(context: Context, uri: Uri): String? {
      context.contentResolver.getType(uri)?.let { return it }
      val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
      return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
  }
}
