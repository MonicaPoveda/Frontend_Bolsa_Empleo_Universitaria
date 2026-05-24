package com.example.frontend_bolsa_empleo_universitaria.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object PdfOpener {

    fun openPdfFile(context: Context, file: File): Result<Unit> {
        return try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("No se encontró una app para abrir PDF: ${e.message}"))
        }
    }

    fun saveToCache(context: Context, bytes: ByteArray, fileName: String): File {
        val file = File(context.cacheDir, fileName)
        file.writeBytes(bytes)
        return file
    }
}
