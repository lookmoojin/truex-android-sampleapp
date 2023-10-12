package com.truedigital.core.extensions

import java.util.Calendar
import java.util.Date
import java.util.TimeZone

private val calendar: Calendar = Calendar.getInstance()

fun Date.toDefaultTimeZone(timeZone: TimeZone = TimeZone.getDefault()): Date {
    calendar.time = this
    calendar.timeZone = timeZone
    return calendar.time
}

fun Date.plusMinutes(minutes: Int): Date {
    calendar.time = this
    calendar.add(Calendar.MINUTE, minutes)
    return calendar.time
}

fun Date.minusMinutes(minutes: Int): Date {
    calendar.time = this
    calendar.add(Calendar.MINUTE, -minutes)
    return calendar.time
}

fun Date.plusDays(days: Int): Date {
    calendar.time = this
    calendar.add(Calendar.DATE, days)
    return calendar.time
}

fun Date.minusDays(days: Int): Date {
    calendar.time = this
    calendar.add(Calendar.DATE, -days)
    return calendar.time
}

fun Date.plusYears(year: Int): Date {
    calendar.time = this
    calendar.add(Calendar.YEAR, year)
    return calendar.time
}

fun Date.minusYears(year: Int): Date {
    calendar.time = this
    calendar.add(Calendar.YEAR, -year)
    return calendar.time
}

fun Date.getDayFromDate(): Int {
    calendar.time = this
    return calendar.get(Calendar.DAY_OF_YEAR)
}

fun Date.getHourFromDate(): Int {
    calendar.time = this
    return calendar.get(Calendar.HOUR_OF_DAY)
}

fun Date.getYearFromDate(): Int {
    calendar.time = this
    return calendar.get(Calendar.YEAR)
}
