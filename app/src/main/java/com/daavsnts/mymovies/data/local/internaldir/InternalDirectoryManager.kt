package com.daavsnts.mymovies.data.local.internaldir

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun copyFileToInternalDir(
    context: Context,
    sourceUri: Uri,
    filename: String
) {
    try {
        val inputStream = context.contentResolver.openInputStream(sourceUri)
        val file = File(context.filesDir, filename)

        if (file.exists()) {
            file.delete()
        }

        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
    } catch (e: IOException) {
        Log.e("copyImageToInternalDir", "Erro ao copiar imagem", e)
    }
}

fun getFileUriFromInternalDir(context:Context, filename: String): Uri {
    return Uri.fromFile(File("${context.filesDir.path}/$filename"))
}