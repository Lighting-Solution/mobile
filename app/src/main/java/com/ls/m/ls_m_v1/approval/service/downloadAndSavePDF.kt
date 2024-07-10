package com.ls.m.ls_m_v1.approval.service

import android.content.Context
import android.os.Environment
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

suspend fun downloadAndSavePDF(context: Context, id: Int): Boolean {
    val response = RetrofitInstanceApproval.instance.downloadPDF(id)

    if (response.isSuccessful) {
        response.body()?.let { body ->
            return saveFileToDisk(context, body, "example$id.pdf")
        }
    }
    return false
}

private fun saveFileToDisk(context: Context, body: ResponseBody, fileName: String): Boolean {
    return try {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)
            val buffer = ByteArray(4096)
            var read: Int

            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }

            outputStream.flush()
            true
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
