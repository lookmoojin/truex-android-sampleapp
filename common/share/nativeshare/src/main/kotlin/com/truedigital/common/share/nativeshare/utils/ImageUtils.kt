package com.truedigital.common.share.nativeshare.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.truedigital.common.share.nativeshare.constant.ImageConstant
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

fun getImageFileFromUrl(context: Context, imageUrl: String): File? {
    return try {
        val url = URL(imageUrl)
        val filename: String = url.file.substring(url.file.lastIndexOf("/"))
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream

        saveBitmap(BitmapFactory.decodeStream(input), context, filename)
    } catch (exception: IOException) {
        exception.printStackTrace()
        null
    }
}

@Throws(IOException::class)
fun saveBitmap(bitmap: Bitmap, context: Context, filename: String): File {
    val bytes = ByteArrayOutputStream()
    val bitmapCompressFormat = when (filename.substring(filename.lastIndexOf("."))) {
        ImageConstant.JPG, ImageConstant.JPEG -> Bitmap.CompressFormat.JPEG
        ImageConstant.PNG -> Bitmap.CompressFormat.PNG
        else -> Bitmap.CompressFormat.PNG
    }

    bitmap.compress(bitmapCompressFormat, 60, bytes)

    val dir = File(context.filesDir, Environment.DIRECTORY_PICTURES)

    if (!dir.exists() && !dir.isDirectory) {
        dir.mkdirs()
    }

    val file = File(dir.path + filename)
    file.createNewFile()

    val fileOutputStream = FileOutputStream(file)
    fileOutputStream.write(bytes.toByteArray())
    fileOutputStream.close()
    return file
}
