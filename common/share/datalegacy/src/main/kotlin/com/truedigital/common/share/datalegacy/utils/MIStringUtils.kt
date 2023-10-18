@file:JvmName("MIStringUtils")

package com.truedigital.common.share.datalegacy.utils

import android.text.Html
import android.text.Spanned
import org.jetbrains.annotations.VisibleForTesting
import kotlin.math.pow

object MIStringUtils {
    @JvmStatic
    fun humanReadableByteCount(bytes: Long): String {
        val unit = 1024
        val exp = 3
        return String.format("%.1f GB", bytes / unit.toDouble().pow(exp.toDouble()))
    }

    @JvmStatic
    fun trimLastString(str: String): String {
        return if (str.isNotEmpty() && str[str.length - 1] == '/') {
            str.substring(0, str.length - 1)
        } else {
            str
        }
    }

    @JvmStatic
    fun getTimeStringFromInt(timeMillisecond: Int): String {
        val second = if (getSecondFromInt(timeMillisecond) < 10) {
            "0${getSecondFromInt(timeMillisecond)}"
        } else {
            "${getSecondFromInt(timeMillisecond)}"
        }
        return "${getMinuteFromInt(timeMillisecond)}:$second"
    }

    @JvmStatic
    fun getMinuteFromInt(milliseconds: Int): Int {
        return milliseconds / 1000 / 60
    }

    @JvmStatic
    fun getFileExtension(fileName: String?, fileExt: String?): String {
        var fileNameLocal = fileName ?: ""
        val fileExtLocal = fileExt ?: ""

        val nameFileToken = fileNameLocal.split("[.]").toTypedArray()
        if (nameFileToken.size == 1) {
            val fileExtToken = fileExtLocal.split("[/]").toTypedArray()
            fileNameLocal += if (fileExtToken.size > 1) {
                ".${fileExtToken[fileExtToken.size - 1]}"
            } else {
                ".$fileExt"
            }
        }
        return fileNameLocal
    }

    @JvmStatic
    fun isNullOrEmpty(str: String?): Boolean {
        return str.isNullOrEmpty()
    }

    fun fromHtml(html: String): Spanned {
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    }

    @VisibleForTesting
    fun getSecondFromInt(milliseconds: Int): Int {
        return milliseconds / 1000 % 60
    }
}
