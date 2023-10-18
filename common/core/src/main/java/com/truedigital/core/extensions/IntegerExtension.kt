package com.truedigital.core.extensions

import android.content.Context
import com.truedigital.core.constant.NumberConstant.BILLION_NUMBER
import com.truedigital.core.constant.NumberConstant.MILLION_NUMBER
import com.truedigital.core.constant.NumberConstant.ONE_NUMBER
import com.truedigital.core.constant.NumberConstant.SIXTY_NUMBER
import com.truedigital.core.constant.NumberConstant.TEN_NUMBER
import com.truedigital.core.constant.NumberConstant.THOUSAND_NUMBER
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

fun Int.decToHex(): String = String.format("%x", this)

fun Int?.convertToNumberFormat(): String? {
    this?.let {
        return NumberFormat.getNumberInstance(Locale.US).format(it)
    } ?: kotlin.run {
        return this.toString()
    }
}

fun Int?.convertToCurrencyFormat(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US) as DecimalFormat
    formatter.applyPattern("#,###")
    return formatter.format(this)
}

fun Int.convertToCountViewVideo(): String {
    val am: Double
    val formatter = NumberFormat.getNumberInstance(Locale.US) as DecimalFormat
    formatter.roundingMode = RoundingMode.DOWN
    formatter.applyPattern("##.#")
    val formatCount = if (abs(this / BILLION_NUMBER) >= ONE_NUMBER) {
        am = this.toDouble() / BILLION_NUMBER
        formatter.format(am) + "B"
    } else if (abs(this / MILLION_NUMBER) >= ONE_NUMBER) {
        am = this.toDouble() / MILLION_NUMBER
        formatter.format(am) + "M"
    } else if (abs(this / THOUSAND_NUMBER) >= ONE_NUMBER) {
        am = this.toDouble() / THOUSAND_NUMBER
        formatter.format(am) + "K"
    } else {
        this.toString()
    }
    return formatCount
}

/**
 * Format:
 * 1-100    :    1,2,3,…,99
 * 1,000-9,999    :    1000,1001,…,9999
 * 10,000-99,999    :    10K,10.1K,…,99.9K
 * 100,000-999,999    :    100K,100.1K,…,999.9K
 * 1,000,000-9,999,999    :    1M,1.1M,…,9.9M
 * 10,000,000-99,999,999    :    10M,10.1M,…,99.9M
 * 1000,000,000-…   :   1B, 1.1B
 */
fun Int.convertToEngagementFormat(): String {
    val am: Float
    val formatter = NumberFormat.getNumberInstance(Locale.US) as DecimalFormat
    formatter.roundingMode = RoundingMode.DOWN
    formatter.applyPattern("##.#")
    val formatCount = if (abs(this / BILLION_NUMBER) >= ONE_NUMBER) {
        am = this.toFloat() / BILLION_NUMBER
        formatter.format(am) + "B"
    } else if (abs(this / MILLION_NUMBER) >= ONE_NUMBER) {
        am = this.toFloat() / MILLION_NUMBER
        formatter.format(am) + "M"
    } else if (abs(this / THOUSAND_NUMBER) >= TEN_NUMBER) {
        am = this.toFloat() / THOUSAND_NUMBER
        formatter.format(am) + "K"
    } else {
        this.toString()
    }
    return formatCount
}

fun Int.fromMinuteToHour(hourString: String, minuteString: String): String {
    val hour = this / 60
    val minute = this % 60
    return when {
        hour < 1 -> "$minute $minuteString"
        minute < 1 -> "$hour $hourString"
        else -> "$hour $hourString $minute $minuteString"
    }
}

fun Int.fromMilliSecondToMinute(): Int = ((this / THOUSAND_NUMBER) / SIXTY_NUMBER)

fun Int.dpToPx(context: Context): Float {
    return this * context.resources.displayMetrics.density
}

fun Int.withAlpha(alpha: Int): Int {
    require(alpha in 0..0xFF)
    return this and 0x00FFFFFF or (alpha shl 24)
}
