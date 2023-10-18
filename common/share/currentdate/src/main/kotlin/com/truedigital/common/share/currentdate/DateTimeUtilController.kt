package com.truedigital.common.share.currentdate

import android.text.format.DateUtils
import com.newrelic.agent.android.NewRelic
import com.truedigital.core.constant.DateFormatConstant
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.domain.usecase.GetLocalizationUseCaseImpl
import com.truedigital.core.extensions.DAYS_MILLI
import com.truedigital.core.extensions.convertThaiYearFromString
import com.truedigital.core.extensions.plusDays
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Pattern
import javax.inject.Inject

class DateTimeUtilController @Inject constructor(
    private val localizationRepository: LocalizationRepository
) : DateTimeInterface {

    private val getLocalizationUseCase = GetLocalizationUseCaseImpl(localizationRepository)

    companion object {
        private const val BUDDHIST_YEAR = 543
        private const val HOUR_IN_MS_SECOND = 3600000
        private const val MINUTE_IN_MS_SECOND = 60000
        private const val SECOND_IN_MS_SECOND = 1000
        private const val HOUR = 1
        private const val MINUTE = 2
        private const val SECOND = 3
    }

    override fun getDate(): Date {
        return Date()
    }

    /**=============================================================================================
     * Public methods
     *============================================================================================*/

    override fun calculateRemainTime(startTimeInMills: Long, currentTimeInMills: Long?): Long {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val timeInMillis = currentTimeInMills ?: calendar.timeInMillis
        return (startTimeInMills - timeInMillis)
    }

    override fun convertToMilliSeconds(dateTime: String, locale: Locale): Long {
        return try {
            val sdf = SimpleDateFormat(DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z, locale)
            sdf.timeZone = TimeZone.getTimeZone(DateFormatConstant.UTC)
            sdf.parse(dateTime)?.time ?: 0L
        } catch (e: ParseException) {
            // Do nothing
            0L
        }
    }

    override fun convertToMilliSecondsForCountDown(dateTime: String, locale: Locale): Long {
        return try {
            val sdf = SimpleDateFormat(DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_Z, locale)
            sdf.timeZone = TimeZone.getTimeZone(DateFormatConstant.UTC)
            sdf.parse(dateTime)?.time ?: 0L
        } catch (e: ParseException) {
            // Do nothing
            0L
        }
    }

    override fun toShortDate(startTimeInMills: Long): String {
        val sdf =
            SimpleDateFormat(DateFormatConstant.E_d_MMM_yyyy, localizationRepository.getAppLocaleForEnTh())
        sdf.timeZone = TimeZone.getDefault()

        return if (getLocalizationUseCase.execute() == LocalizationRepository.Localization.TH) {
            sdf.format(Date(startTimeInMills)).convertThaiYearFromString()
        } else {
            sdf.format(Date(startTimeInMills))
        }
    }

    override fun toShortDate(
        startTimeInMills: Long,
        locale: Locale,
        dateFormat: String
    ): String {
        val sdf = SimpleDateFormat(dateFormat, locale)
        sdf.timeZone = TimeZone.getDefault()

        return if (locale.language == "th") {
            sdf.format(Date(startTimeInMills)).convertThaiYearFromString()
        } else {
            sdf.format(Date(startTimeInMills))
        }
    }

    override fun getCurrentDateTime(dateFormat: String): String {
        val sdf2 = SimpleDateFormat(dateFormat)
        sdf2.timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance(TimeZone.getDefault())

        val current = calendar.timeInMillis
        return sdf2.format(current)
    }

    override fun getCurrentDateInMillis(): Long {
        val sdf2 = SimpleDateFormat(DateFormatConstant.yyyy_MM_dd_DASH)
        sdf2.timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        return calendar.timeInMillis
    }

    override fun isToday(dateString: String?, dateFormat: String): Boolean {
        if (dateString == null) return false

        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        val timeZone = TimeZone.getDefault()
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone.displayName)
        val startDate = simpleDateFormat.parse(dateString)
        return DateUtils.isToday(startDate?.time ?: 0L)
    }

    override fun isToday(timeInMills: Long): Boolean {
        return DateUtils.isToday(timeInMills)
    }

    override fun isTomorrow(timeInMills: Long): Boolean {
        return DateUtils.isToday(timeInMills - DateUtils.DAY_IN_MILLIS)
    }

    override fun isYesterday(timeInMills: Long): Boolean {
        return DateUtils.isToday(timeInMills + DateUtils.DAY_IN_MILLIS)
    }

    override fun getYesterday(dateFormat: String, locale: Locale): String {
        val sdf2 = SimpleDateFormat(dateFormat, locale)
        sdf2.timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance(TimeZone.getDefault())

        val yesterday = calendar.timeInMillis - DAYS_MILLI

        return sdf2.format(yesterday)
    }

    override fun getFutureFromToday(days: Int, format: String): String {
        val sdf2: SimpleDateFormat = when (localizationRepository.getAppLanguageCode()) {
            LocalizationRepository.Localization.TH.languageCode -> {
                SimpleDateFormat(format, localizationRepository.getAppLocale())
            }
            else -> {
                SimpleDateFormat(
                    format,
                    localizationRepository.getLocale(LocalizationRepository.Localization.EN)
                )
            }
        }
        sdf2.timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.set(Calendar.DAY_OF_YEAR, calendar[Calendar.DAY_OF_YEAR] + days)
        val yesterday = calendar.timeInMillis - DAYS_MILLI
        return sdf2.format(yesterday)
    }

    override fun getPastFromMinute(minute: Int): String {
        val sdf2 = SimpleDateFormat(DateFormatConstant.yyyy_MM_dd_HH_mm_ss)
        sdf2.timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.set(Calendar.MINUTE, calendar[Calendar.MINUTE] - minute)
        val yesterday = calendar.timeInMillis - DAYS_MILLI

        return sdf2.format(yesterday)
    }

    override fun convertTimeMillisToBuddhistYear(milli: Long, dateFormat: String): String {
        val simpleDateFormat: DateFormat
        val date = Date(milli)
        val calendar = GregorianCalendar()
        calendar.time = date
        when (localizationRepository.getAppLanguageCode()) {
            LocalizationRepository.Localization.TH.languageCode -> {
                simpleDateFormat =
                    SimpleDateFormat(dateFormat, localizationRepository.getAppLocale())
                calendar.add(Calendar.YEAR, BUDDHIST_YEAR)
            }
            LocalizationRepository.Localization.IN.languageCode,
            LocalizationRepository.Localization.KH.languageCode,
            LocalizationRepository.Localization.MY.languageCode,
            LocalizationRepository.Localization.VN.languageCode -> {
                simpleDateFormat = SimpleDateFormat(
                    DateFormatConstant.d_M_yy_SLASH,
                    localizationRepository.getLocale(LocalizationRepository.Localization.EN)
                )
            }
            else -> {
                simpleDateFormat =
                    SimpleDateFormat(
                        dateFormat,
                        localizationRepository.getLocale(LocalizationRepository.Localization.EN)
                    )
            }
        }
        return simpleDateFormat.format(calendar.time)
    }

    override fun convertTimeMillisToDate(milli: Long, dateFormat: String): String {
        val simpleDateFormat: DateFormat
        val date = Date(milli)
        val calendar = GregorianCalendar()
        calendar.time = date
        val appLocale = localizationRepository.getAppLocale()
        when (localizationRepository.getAppLanguageCode()) {
            LocalizationRepository.Localization.TH.languageCode -> {
                simpleDateFormat = SimpleDateFormat(dateFormat, appLocale)
                calendar.add(Calendar.YEAR, BUDDHIST_YEAR)
            }
            else -> {
                simpleDateFormat = SimpleDateFormat(
                    dateFormat,
                    localizationRepository.getLocale(LocalizationRepository.Localization.EN)
                )
            }
        }
        return simpleDateFormat.format(calendar.time)
    }

    override fun convertTimeMillisToDateStrIgnoreBuddhistYear(
        timeMillis: Long,
        format: String,
        addDate: Int
    ): String {
        val simpleDateFormat = SimpleDateFormat(
            format,
            localizationRepository.getAppLocaleForEnTh()
        )
        val calendar = GregorianCalendar()
        calendar.time = Date(timeMillis)
        calendar.add(Calendar.DATE, addDate)

        return simpleDateFormat.format(calendar.time)
    }

    override fun convertTimeFormatForNewCMS(dateTime: String?): String {
        var result = ""
        if (dateTime != null && dateTime != "") {
            val sdf = SimpleDateFormat(DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss, localizationRepository.getAppLocale())
            try {
                val newDate = sdf.parse(dateTime)
                val calendar = Calendar.getInstance()
                calendar.time = newDate
                sdf.applyPattern(DateFormatConstant.dd_MMM_yyyy)
                if (getLocalizationUseCase.execute() == LocalizationRepository.Localization.TH) {
                    val convertDate = sdf.format(calendar.time)
                    val dates = convertDate.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    dates[2] =
                        (Integer.valueOf(dates[2]) + BUDDHIST_YEAR).toString() // convert to year in thai
                    for (date in dates) {
                        result += date
                        result += " "
                    }
                } else {
                    val sdfEn = SimpleDateFormat(
                        DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss,
                        localizationRepository.getLocale(LocalizationRepository.Localization.EN)
                    )
                    sdfEn.applyPattern(DateFormatConstant.dd_MMM_yyyy)
                    result = sdfEn.format(calendar.time)
                }
            } catch (ex: ParseException) {
                trackingNewRelic(ex, "ParseException", dateTime)
            } catch (ex: IllegalArgumentException) {
                trackingNewRelic(ex, "IllegalArgumentException", dateTime)
            } catch (ex: Exception) {
                trackingNewRelic(ex, "Exception", dateTime)
            }
        }
        return result
    }

    private fun trackingNewRelic(ex: Exception, typeException: String, dateTime: String) {
        val handlingExceptionMap = mapOf(
            "Key" to "DateTimeUtil",
            "Value" to "Exception cased by $typeException { $ex } when convertTimeFormatForNewCMS $dateTime"
        )
        NewRelic.recordHandledException(
            Exception(ex.message),
            handlingExceptionMap
        )
    }

    override fun getTimeInLong(time: String): Long {
        val pattern = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2})")
        val matcher = pattern.matcher(time)
        return if (matcher.matches()) {
            (matcher.group(HOUR)?.toLong() ?: 0L) * HOUR_IN_MS_SECOND +
                (matcher.group(MINUTE)?.toLong() ?: 0L) * MINUTE_IN_MS_SECOND +
                (matcher.group(SECOND)?.toLong() ?: 0L) * SECOND_IN_MS_SECOND
        } else {
            -1
        }
    }

    override fun isLastTimeAfterCurrentTime(timeInMills: Long, lastTimeInMills: Long, plusDay: Int): Boolean {
        val currentDate = Date(timeInMills)
        val latestDay = Date(lastTimeInMills)
        return latestDay.plusDays(plusDay).after(currentDate)
    }
}
