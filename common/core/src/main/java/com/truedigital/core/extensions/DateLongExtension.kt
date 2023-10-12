package com.truedigital.core.extensions

import com.truedigital.core.constant.DateFormatConstant.HH_mm
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

const val MILLISECONDS: Long = 1000
const val MINUTES_MILLI = MILLISECONDS * 60
const val HOURS_MILLI = MINUTES_MILLI * 60
const val DAYS_MILLI = HOURS_MILLI * 24

fun Long.isOneDayLeft(): Boolean {
    if (this > DAYS_MILLI) {
        return false
    }
    return true
}

fun Long.convertChristianYearToBuddhistYear(dateFormat: String): String {
    val sdf = SimpleDateFormat(dateFormat)
    val calendar = GregorianCalendar()
    calendar.time = Date(this)
    calendar.add(Calendar.YEAR, 543)
    return sdf.format(calendar.time)
}

fun Long.convertToDateString(dateFormat: String = HH_mm, locale: Locale): String {
    val sdf = SimpleDateFormat(dateFormat, locale)
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(this))
}

fun Long.convertToDate(): Date {
    return Date(this)
}

fun Long.convertToSecond(): String {
    val currentTime = this / MILLISECONDS
    return currentTime.toString()
}

fun Long.convertMillisecondsToHours(): Double {
    return this.toDouble() / HOURS_MILLI
}
