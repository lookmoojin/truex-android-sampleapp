package com.truedigital.common.share.currentdate

import com.truedigital.core.constant.DateFormatConstant.E_d_MMM_yyyy
import com.truedigital.core.constant.DateFormatConstant.yyyy_MM_dd_DASH
import com.truedigital.core.constant.DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z
import java.util.Date
import java.util.Locale
import javax.inject.Inject

interface DateTimeInterface {
    fun getDate(): Date
    fun calculateRemainTime(startTimeInMills: Long, currentTimeInMills: Long? = null): Long
    fun getCurrentDateTime(dateFormat: String = yyyy_MM_dd_DASH): String
    fun convertToMilliSeconds(dateTime: String, locale: Locale): Long
    fun convertToMilliSecondsForCountDown(dateTime: String, locale: Locale): Long
    fun getFutureFromToday(days: Int, format: String = yyyy_MM_dd_DASH): String
    fun getPastFromMinute(minute: Int): String
    fun convertTimeMillisToBuddhistYear(milli: Long, dateFormat: String): String
    fun convertTimeMillisToDate(milli: Long, dateFormat: String): String
    fun convertTimeMillisToDateStrIgnoreBuddhistYear(
        timeMillis: Long,
        format: String,
        addDate: Int = 0
    ): String
    fun convertTimeFormatForNewCMS(dateTime: String?): String

    fun getYesterday(
        dateFormat: String = yyyy_MM_dd_DASH,
        locale: Locale = Locale.getDefault()
    ): String

    fun getCurrentDateInMillis(): Long
    fun isToday(dateString: String?, dateFormat: String = yyyy_MM_dd_T_HH_mm_ss_SSS_Z): Boolean
    fun isToday(timeInMills: Long): Boolean
    fun isTomorrow(timeInMills: Long): Boolean
    fun isYesterday(timeInMills: Long): Boolean
    fun toShortDate(startTimeInMills: Long): String
    fun getTimeInLong(time: String): Long
    fun toShortDate(
        startTimeInMills: Long,
        locale: Locale,
        dateFormat: String = E_d_MMM_yyyy
    ): String
    fun isLastTimeAfterCurrentTime(timeInMills: Long, lastTimeInMills: Long, plusDay: Int): Boolean
}

class DateTimeUtil @Inject constructor(
    private val dateTimeUtilController: DateTimeUtilController
) : DateTimeInterface {

    override fun getDate(): Date {
        return dateTimeUtilController.getDate()
    }

    /**=============================================================================================
     * Public methods
     *============================================================================================*/

    override fun calculateRemainTime(startTimeInMills: Long, currentTimeInMills: Long?): Long {
        return dateTimeUtilController.calculateRemainTime(
            startTimeInMills = startTimeInMills,
            currentTimeInMills = currentTimeInMills
        )
    }

    override fun convertToMilliSeconds(dateTime: String, locale: Locale): Long {
        return dateTimeUtilController.convertToMilliSeconds(
            dateTime = dateTime,
            locale = locale
        )
    }

    override fun convertToMilliSecondsForCountDown(dateTime: String, locale: Locale): Long {
        return dateTimeUtilController.convertToMilliSecondsForCountDown(
            dateTime = dateTime,
            locale = locale
        )
    }

    override fun toShortDate(startTimeInMills: Long): String {
        return dateTimeUtilController.toShortDate(
            startTimeInMills = startTimeInMills
        )
    }

    override fun toShortDate(
        startTimeInMills: Long,
        locale: Locale,
        dateFormat: String
    ): String {
        return dateTimeUtilController.toShortDate(
            startTimeInMills = startTimeInMills,
            locale = locale,
            dateFormat = dateFormat
        )
    }

    override fun getCurrentDateTime(dateFormat: String): String {
        return dateTimeUtilController.getCurrentDateTime(
            dateFormat = dateFormat
        )
    }

    override fun getCurrentDateInMillis(): Long {
        return dateTimeUtilController.getCurrentDateInMillis()
    }

    override fun isToday(dateString: String?, dateFormat: String): Boolean {
        return dateTimeUtilController.isToday(
            dateString = dateString,
            dateFormat = dateFormat
        )
    }

    override fun isToday(timeInMills: Long): Boolean {
        return dateTimeUtilController.isToday(
            timeInMills = timeInMills
        )
    }

    override fun isTomorrow(timeInMills: Long): Boolean {
        return dateTimeUtilController.isTomorrow(
            timeInMills = timeInMills
        )
    }

    override fun isYesterday(timeInMills: Long): Boolean {
        return dateTimeUtilController.isYesterday(
            timeInMills = timeInMills
        )
    }

    override fun getYesterday(dateFormat: String, locale: Locale): String {
        return dateTimeUtilController.getYesterday(
            dateFormat = dateFormat,
            locale = locale
        )
    }

    override fun getFutureFromToday(days: Int, format: String): String {
        return dateTimeUtilController.getFutureFromToday(
            days = days,
            format = format
        )
    }

    override fun getPastFromMinute(minute: Int): String {
        return dateTimeUtilController.getPastFromMinute(
            minute = minute
        )
    }

    override fun convertTimeMillisToBuddhistYear(milli: Long, dateFormat: String): String {
        return dateTimeUtilController.convertTimeMillisToBuddhistYear(
            milli = milli,
            dateFormat = dateFormat
        )
    }

    override fun convertTimeMillisToDate(milli: Long, dateFormat: String): String {
        return dateTimeUtilController.convertTimeMillisToDate(
            milli = milli,
            dateFormat = dateFormat
        )
    }

    override fun convertTimeMillisToDateStrIgnoreBuddhistYear(
        timeMillis: Long,
        format: String,
        addDate: Int
    ): String {
        return dateTimeUtilController.convertTimeMillisToDateStrIgnoreBuddhistYear(
            timeMillis = timeMillis,
            format = format,
            addDate = addDate
        )
    }

    override fun convertTimeFormatForNewCMS(dateTime: String?): String {
        return dateTimeUtilController.convertTimeFormatForNewCMS(
            dateTime = dateTime
        )
    }

    override fun getTimeInLong(time: String): Long {
        return dateTimeUtilController.getTimeInLong(
            time = time
        )
    }

    override fun isLastTimeAfterCurrentTime(timeInMills: Long, lastTimeInMills: Long, plusDay: Int): Boolean {
        return dateTimeUtilController.isLastTimeAfterCurrentTime(timeInMills, lastTimeInMills, plusDay)
    }
}
