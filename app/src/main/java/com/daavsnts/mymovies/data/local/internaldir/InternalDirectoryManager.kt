package com.daavsnts.mymovies.data.local.internaldir

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
interface FileNumber {
    companion object {
        var num = 0
    }
}
fun copyFileToInternalDir(
    context: Context,
    sourceUri: Uri,
    filename: String
) {
    try {
        val inputStream = context.contentResolver.openInputStream(sourceUri)
        val file = if (FileNumber.num == 0) {
            FileNumber.num = 1
            File(context.filesDir, "${filename}1")
        } else {
            FileNumber.num = 0
            File(context.filesDir, "${filename}0")
        }

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
    val numberedFilename = if (FileNumber.num == 0) "${filename}0" else "${filename}1"
    return Uri.fromFile(File("${context.filesDir.path}/$numberedFilename"))
}