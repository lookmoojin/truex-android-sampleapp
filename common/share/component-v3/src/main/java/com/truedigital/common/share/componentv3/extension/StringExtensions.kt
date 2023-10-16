package com.truedigital.common.share.componentv3.extension

import com.newrelic.agent.android.NewRelic
import com.truedigital.core.extensions.convertDateToLong
import java.util.Calendar

fun String.checkAndConvertToBadgeType(newEpiBadgeStart: String?, newEpiBadgeEnd: String?): String {
    try {
        if (newEpiBadgeStart.isNullOrEmpty() || newEpiBadgeEnd.isNullOrEmpty()) return ""
        val getCurrentDate = Calendar.getInstance().time
        val currentDate = getCurrentDate.time
        val startTime = newEpiBadgeStart.convertDateToLong()
        val endTime = newEpiBadgeEnd.convertDateToLong()
        return if (currentDate in (startTime + 1) until endTime) return this else ""
    } catch (exception: Exception) {
        val handlingExceptionMap = mapOf(
            "Key" to "StreamerInfoDataExtension",
            "Value" to "$exception"
        )
        NewRelic.recordHandledException(Exception(exception.localizedMessage), handlingExceptionMap)
        return ""
    }
}

fun String?.toCategoryDescription(): String {
    return "title_${this.orEmpty()}_category_page"
}

/**
 * Example result
 * 2.16.1 return (int) 2016001
 */
fun String.appVersionToNumber(): Int {
    if (this.isEmpty()) {
        return 0
    }
    val versionSplit = this.trim { it <= ' ' }.split("\\.".toRegex())
    return try {
        var versionNumber = 0
        var i = 0
        while (i < versionSplit.size && i < 3) {
            val shipBit = (1000000 / Math.pow(1000.0, i.toDouble())).toInt()
            versionNumber += Integer.parseInt(versionSplit[i]) * shipBit
            i++
        }
        versionNumber
    } catch (e: Exception) {
        0
    }
}
