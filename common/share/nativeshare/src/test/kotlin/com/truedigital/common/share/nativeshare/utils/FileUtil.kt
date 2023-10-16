package com.truedigital.common.share.nativeshare.utils

import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/** Read more detail @see https://medium.com/mobile-app-development-publication/android-reading-a-text-file-during-test-2815671e8b3b */

object FileUtil {

    @Throws(IOException::class)
    fun readFileWithoutNewLineFromResources(fileName: String): String {
        var inputStream: InputStream? = null
        try {
            inputStream = getInputStreamFromResource(fileName)
            val builder = StringBuilder()
            val reader = BufferedReader(InputStreamReader(inputStream))

            var str: String? = reader.readLine()
            while (str != null) {
                builder.append(str)
                str = reader.readLine()
            }
            return builder.toString()
        } finally {
            inputStream?.close()
        }
    }

    @Throws(IOException::class)
    fun readFileWithNewLineFromResources(fileName: String): String {
        var inputStream: InputStream? = null
        try {
            inputStream = getInputStreamFromResource(fileName)
            val builder = StringBuilder()
            val reader = BufferedReader(InputStreamReader(inputStream))

            var theCharNum = reader.read()
            while (theCharNum != -1) {
                builder.append(theCharNum.toChar())
                theCharNum = reader.read()
            }

            return builder.toString()
        } finally {
            inputStream?.close()
        }
    }

    fun kotlinReadFileWithNewLineFromResources(fileName: String): String {
        return getInputStreamFromResource(fileName)?.bufferedReader()
            .use { bufferReader -> bufferReader?.readText() } ?: ""
    }

    @Throws(IOException::class)
    fun readBinaryFileFromResources(fileName: String): ByteArray {
        var inputStream: InputStream? = null
        val byteStream = ByteArrayOutputStream()
        try {
            inputStream = getInputStreamFromResource(fileName)

            var nextValue = inputStream?.read() ?: -1

            while (nextValue != -1) {
                byteStream.write(nextValue)
                nextValue = inputStream?.read() ?: -1
            }
            return byteStream.toByteArray()
        } finally {
            inputStream?.close()
            byteStream.close()
        }
    }

    fun kotlinReadBinaryFileFromResources(fileName: String): ByteArray {
        ByteArrayOutputStream().use { byteStream ->
            getInputStreamFromResource(fileName)?.copyTo(byteStream)
            return byteStream.toByteArray()
        }
    }

    private fun getInputStreamFromResource(fileName: String) =
        javaClass.classLoader?.getResourceAsStream(fileName)
}
