package com.truedigital.features.truecloudv3.provider

import androidx.exifinterface.media.ExifInterface
import javax.inject.Inject

interface ExifProvider {
    fun getExif(filePath: String): ExifInterface
}

class ExifProviderImpl @Inject constructor() : ExifProvider {
    override fun getExif(filePath: String): ExifInterface {
        return ExifInterface(filePath)
    }
}
