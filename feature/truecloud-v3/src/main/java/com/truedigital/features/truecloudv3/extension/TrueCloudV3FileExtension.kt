package com.truedigital.features.truecloudv3.extension

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

fun File.isComplete(): Boolean {
    val fileSize = this.length()
    return fileSize > 0 && isWriteComplete(this)
}

private fun isWriteComplete(file: File): Boolean {
    return try {
        FileInputStream(file)
        true
    } catch (e: FileNotFoundException) {
        false
    }
}
