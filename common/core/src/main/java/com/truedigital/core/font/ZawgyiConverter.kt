package com.truedigital.core.font

import com.google.myanmartools.TransliterateU2Z
import com.google.myanmartools.TransliterateZ2U
import com.google.myanmartools.ZawgyiDetector

object ZawgyiConverter {
    private val detector: ZawgyiDetector = ZawgyiDetector()
    private val unicodeConverter: TransliterateZ2U = TransliterateZ2U("Zawgyi to Unicode")
    private val zawgyiConverter: TransliterateU2Z = TransliterateU2Z("Unicode to Zawgyi")
    private const val UNICODE_THRESHOLD = 0.45
    private const val ZAWGYI_THRESHOLD = 0.01

    fun convert(text: String?): String {
        text?.let {
            val score: Double = detector.getZawgyiProbability(text)
            return when {
                score > UNICODE_THRESHOLD -> {
                    unicodeConverter.convert(text)
                }
                score < ZAWGYI_THRESHOLD -> {
                    zawgyiConverter.convert(text)
                }
                else -> {
                    text
                }
            }
        }
        return ""
    }
}
