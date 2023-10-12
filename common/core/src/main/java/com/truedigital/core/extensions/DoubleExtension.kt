package com.truedigital.core.extensions

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

fun Double.convertToCurrencyUnitFormat(iteration: Int = 0): String {
    return try {
        if (this < 1000) {
            val formatter = NumberFormat.getNumberInstance(Locale.US) as DecimalFormat
            formatter.applyPattern("###.#")
            return formatter.format(this)
        }
        val suffix = charArrayOf('K', 'M')
        val afterRound = this.toLong() / 100 / 10.0
        val isRound =
            afterRound * 10 % 10 == 0.0 // true if the decimal part is equal to 0 (then it's trimmed anyway)
        if (afterRound < 1000) { // this determines the class, i.e. 'k', 'm' etc
            if (isRound) { // this decides whether to trim the decimals
                (afterRound.toInt() * 10 / 10).toString() + suffix[iteration]
            } else {
                afterRound.toString() + suffix[iteration]
            }
        } else {
            afterRound.convertToCurrencyUnitFormat(iteration + 1)
        }
    } catch (e: NumberFormatException) {
        e.printStackTrace()
        "0"
    }
}

fun Double.convertToCurrencyFormat(): String {
    return try {
        val formatter = NumberFormat.getNumberInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("###,###,##0.00")
        return formatter.format(this)
    } catch (e: NumberFormatException) {
        e.printStackTrace()
        "0"
    }
}

fun Double.roundTo(size: Int): String = "%.${size}f".format(this)

fun Float.roundTo(size: Int): String = "%.${size}f".format(this)

fun Float.convertNearestNumber(size: Float): Float {
    val smallerValue = (this / size).toInt() * size
    val largerValue = smallerValue + size

    return if (this - smallerValue > largerValue - this) largerValue else smallerValue
}
