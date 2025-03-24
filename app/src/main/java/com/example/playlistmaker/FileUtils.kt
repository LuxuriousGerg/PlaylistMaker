package com.example.playlistmaker.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun copyUriToInternalStorage(context: Context, sourceUri: Uri?): String? {
        if (sourceUri == null) return null
        val fileName = "cover_${System.currentTimeMillis()}.jpg"
        val destFile = File(context.filesDir, fileName)

        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        return destFile.absolutePath
    }
}
