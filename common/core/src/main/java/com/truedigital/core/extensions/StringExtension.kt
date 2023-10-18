package com.truedigital.core.extensions

import android.text.Spanned
import androidx.core.text.parseAsHtml
import com.newrelic.agent.android.NewRelic
import java.net.URLEncoder
import java.nio.charset.IllegalCharsetNameException
import java.nio.charset.StandardCharsets
import kotlin.math.min

fun String.hexToDec(): Int = if (this.isEmpty()) {
    0
} else {
    try {
        Integer.parseInt(this, 16)
    } catch (exception: NumberFormatException) {
        0
    }
}

fun String.isNumeric(): Boolean {
    return this.matches("-?\\d+(\\.\\d+)?".toRegex())
}

fun String.compareVersionTo(version: String): CompareVersion {
    val regex = "[0-9]+(\\.[0-9]+)*".toRegex()
    if (!this.matches(regex) || !version.matches(regex)) {
        val errorMessage = "Invalid version format"
        val handlingExceptionMap = mapOf(
            "Key" to "compareVersionTo",
            "Value" to "Unexpected Error in StringExtension : $errorMessage , SystemWebViewVersion : $this , minimumVersion : $version"
        )

        NewRelic.recordHandledException(
            IllegalArgumentException(errorMessage),
            handlingExceptionMap
        )
        return CompareVersion.EQUAL
    }
    val thisParts: List<String> = this.split(".")
    val thatParts: List<String> = version.split(".")
    val length = thisParts.size.coerceAtLeast(thatParts.size)
    for (i in 0 until length) {
        val thisPart = if (i < thisParts.size) thisParts[i].toInt() else 0
        val thatPart = if (i < thatParts.size) thatParts[i].toInt() else 0
        if (thisPart < thatPart) return CompareVersion.LESS_THAN
        if (thisPart > thatPart) return CompareVersion.GREATER_THAN
    }
    return CompareVersion.EQUAL
}

enum class CompareVersion {
    GREATER_THAN, LESS_THAN, EQUAL
}

fun String.ifNotNullOrEmpty(block: ((String) -> Unit)): String {
    if (isNotEmpty()) block(this)
    return this
}

fun String?.ifIsNullOrEmpty(defaultValue: () -> String): String {
    return if (isNullOrEmpty()) {
        defaultValue()
    } else {
        this
    }
}

fun String?.doOnNullOrEmpty(block: ((String?) -> Unit)) {
    if (isNullOrEmpty()) block(this)
}

fun String.ignoreHtmlTagSpan(): Spanned {
    val regexHtml = Regex("\\<.*?\\>|&nbsp;")
    val regexNewLine = "\\n".toRegex()
    val textIgnoreHtml = regexHtml.replace(this, "")
    return regexNewLine.replace(textIgnoreHtml, "").parseAsHtml()
}

inline fun <T> String.environmentCase(
    staging: ((String) -> T),
    preProd: ((String) -> T),
    prod: ((String) -> T)
): T {
    return when (this) {
        "staging" -> staging(this)
        "preprod" -> preProd(this)
        else -> prod(this)
    }
}

fun String.urlEncode(): String {
    return try {
        URLEncoder.encode(this, StandardCharsets.UTF_8.displayName())
            .replace("\\+".toRegex(), "%20")
    } catch (e: IllegalCharsetNameException) {
        val handlingExceptionMap = mapOf(
            "Key" to "String.urlEncode()",
            "Value" to "(IllegalCharsetNameException) ${e.localizedMessage}"
        )
        NewRelic.recordHandledException(Exception(e.localizedMessage), handlingExceptionMap)

        ""
    }
}

fun String.fillUrlPattern(): String {
    val url = this.removeSpace()
    return if (url.startsWith("http://") || url.startsWith("https://")) {
        url
    } else {
        "http://$url"
    }
}

fun String.insertPeriodically(insert: String, period: Int): String {
    if (period <= 0 || insert.isEmpty()) return this
    val builder = StringBuilder(length + insert.length * (length / period) + 1)
    var index = 0
    while (index < length) {
        if (index > 0) {
            builder.append(insert)
        }
        builder.append(substring(index, min(index + period, length)))
        index += period
    }
    return builder.toString()
}

fun String.removeStartTagHtml(tag: String): String {
    return if (this.startsWith("<$tag", true)) {
        val closeTagIndex = ">".toRegex().find(this)?.range?.last?.inc() ?: 0
        this.drop(closeTagIndex).removeSuffix("</${tag.lowercase()}>")
    } else {
        this
    }
}

fun String.removeSpace(): String {
    return this.trim().replace("\\s".toRegex(), "")
}

fun String.removeSpaceWithLowerCase(): String {
    return this.removeSpace().lowercase()
}

fun String?.orNull(): String? {
    return if (this.isNullOrEmpty()) {
        null
    } else {
        this
    }
}

fun String?.toIntOrDefault(default: Int = 0): Int {
    return this?.toIntOrNull() ?: default
}

fun String?.orEmptyFA(): String {
    return if (this.isNullOrEmpty()) {
        " "
    } else {
        this
    }
}

fun String.checkIsColorAndNotEmpty(): Boolean {
    return this.isNotEmpty() && this.matches("#([0-9a-fA-F]{6})".toRegex())
}

fun String.checkIsStartWithTagAndRemove(): String {
    return if (this.trimStart().startsWith("#")) {
        this.removePrefix("#")
    } else {
        this
    }
}

fun String.checkIsStartWithTag(): Boolean {
    return this.trimStart().startsWith("#")
}

fun String.decodeHashTag(): String {
    return this.replace("%23", "#")
}
