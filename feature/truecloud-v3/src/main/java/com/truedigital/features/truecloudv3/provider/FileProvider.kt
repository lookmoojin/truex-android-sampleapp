package com.truedigital.features.truecloudv3.provider

import java.io.File
import javax.inject.Inject

interface FileProvider {
    fun getFile(filePath: String): File
    fun getFile(parent: String, child: String): File
}

class FileProviderImpl @Inject constructor() : FileProvider {
    override fun getFile(filePath: String): File {
        return File(filePath)
    }

    override fun getFile(parent: String, child: String): File {
        return File(parent, child)
    }
}
