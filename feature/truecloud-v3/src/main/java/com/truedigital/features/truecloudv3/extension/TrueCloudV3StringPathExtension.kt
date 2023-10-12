package com.truedigital.features.truecloudv3.extension

import java.io.File

fun String.getNotExistsPath(): String {
    var file = try {
        File(this)
    } catch (e: SecurityException) {
        return this
    }

    val fileName = file.name
    val orgName = fileName.substringBefore(".")
    val fileExtension = fileName.substringAfter(".")

    var newPath = this
    var index = 0
    while (file.exists()) {
        index++
        val newFileName = "$orgName($index).$fileExtension"
        newPath = "${substringBeforeLast('/')}/$newFileName"
        file = File(newPath)
    }

    return newPath
}
