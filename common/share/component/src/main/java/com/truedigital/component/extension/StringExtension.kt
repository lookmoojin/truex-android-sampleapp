package com.truedigital.component.extension

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.truedigital.component.R
import com.truedigital.core.constant.DateFormatConstant.MMM_dd_yyyy
import com.truedigital.core.constant.DateFormatConstant.UTC
import com.truedigital.core.constant.DateFormatConstant.dd_MMMM_yyyy
import com.truedigital.core.constant.DateFormatConstant.yyyy
import com.truedigital.core.constant.DateFormatConstant.yyyy_MM_dd_DASH
import com.truedigital.core.constant.DateFormatConstant.yyyy_MM_dd_HH_mm_ss
import com.truedigital.core.constant.DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.domain.usecase.GetAppLocaleUseCaseImpl
import com.truedigital.core.domain.usecase.GetLocalizationUseCase
import com.truedigital.core.domain.usecase.GetLocalizationUseCaseImpl
import com.truedigital.core.extensions.DAYS_MILLI
import com.truedigital.core.extensions.HOURS_MILLI
import com.truedigital.core.extensions.MINUTES_MILLI
import com.truedigital.core.injections.CoreComponent
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

private val localizationRepository: LocalizationRepository by lazy {
    CoreComponent.getInstance().getCoreSubComponent().getLocalizationRepository()
}

fun String?.fromFormatApiToHasLocale(
    resultFormat: String = yyyy_MM_dd_DASH,
    isAddTimeZone: Boolean
): String {

    val getAppLocaleUseCase = GetAppLocaleUseCaseImpl(localizationRepository)
    val getLocalizationUseCase = GetLocalizationUseCaseImpl(localizationRepository)
    val isThai = getLocalizationUseCase.execute() == LocalizationRepository.Localization.TH

    var result = ""

    try {
        val sdf = SimpleDateFormat(yyyy_MM_dd_HH_mm_ss, getAppLocaleUseCase.execute())
        if (isAddTimeZone) {
            sdf.timeZone = TimeZone.getTimeZone(UTC)
        }
        val date = sdf.parse(this)
        sdf.applyPattern(resultFormat)
        if (isThai) {
            val convertDate = sdf.format(Date(date.toString()))
            val dates = convertDate.split(" ").toTypedArray()
            if (isThai && dates.isNotEmpty() && dates.size > 2) {
                dates[2] = (Integer.valueOf(dates[2]) + 543).toString() + "" // convert to Thai year
                for (date in dates) {
                    result += "$date "
                }
            }
            result = convertDate
        } else {
            result = sdf.format(Date(date.toString()))
        }
        return result
    } catch (e: ParseException) {
        return ""
    } catch (e: NullPointerException) {
        return ""
    }
}

fun String?.toDateFormatMultipleLanguage(
    resultFormatTh: String = dd_MMMM_yyyy,
    resultFormatEn: String = MMM_dd_yyyy,
    isAddTimeZone: Boolean = false,
    isEnThOnly: Boolean = false
): String {

    val getLocalizationUseCase = GetLocalizationUseCaseImpl(localizationRepository)
    val isThai = getLocalizationUseCase.execute() == LocalizationRepository.Localization.TH

    var result = ""

    try {
        val locale = if (isEnThOnly) {
            localizationRepository.getAppLocaleForEnTh()
        } else {
            localizationRepository.getAppLocale()
        }
        val sdf = SimpleDateFormat(yyyy_MM_dd_DASH, locale)
        if (isAddTimeZone) {
            sdf.timeZone = TimeZone.getTimeZone(UTC)
        }
        val date = sdf.parse(this)
        if (isThai) {
            sdf.applyPattern(resultFormatTh)
            val convertDate = sdf.format(Date(date.toString()))
            val dates = convertDate.split(" ").toTypedArray()
            if (isThai && dates.isNotEmpty() && dates.size > 2) {
                dates[2] = (Integer.valueOf(dates[2]) + 543).toString() + "" // convert to Thai year
                for (date in dates) {
                    result += "$date "
                }
            } else {
                result = convertDate
            }
        } else {
            sdf.applyPattern(resultFormatEn)
            result = sdf.format(Date(date.toString()))
        }
        return result
    } catch (e: ParseException) {
        return ""
    } catch (e: NullPointerException) {
        return ""
    }
}

/**
 * use streaming lang
 */
fun String.toFullLang(): String {

    when (this) {
        "tha" -> {
            return "Thai"
        }
        "eng" -> {
            return "English"
        }
        "kor" -> {
            return "Korea"
        }
        "chn" -> {
            return "Chinese"
        }
        "jpn" -> {
            return "Japanese"
        }
        "ger" -> {
            return "Germany"
        }
        "fra" -> {
            return "France"
        }
        "hkg" -> {
            return "Hongkong"
        }
        "spa" -> {
            return "Spain"
        }
        "ind" -> {
            return "India"
        }
        "cam" -> {
            return "Cambodia"
        }
        "mya" -> {
            return "Myanmar"
        }
    }

    return this
}

fun String?.toBoolean(): Boolean {
    if (this?.trim().equals(other = "true", ignoreCase = true)) {
        return true
    }
    return false
}

/**
 * Example for birth_date format "1991-01-04"
 *
 * Users with age 18+ but not completed 26 years old are eligible to join student campaign
 * max & min parameters retrieved from Firebase
 */
fun String?.isBirthDateEligible(max: String?, min: String?): Boolean {
    val SPLITTER = "-"
    if (this.isNullOrEmpty() || !this.contains(SPLITTER)) {
        // invalid birthDate value return not eligible
        return false
    }

    val year = this.split(SPLITTER).firstOrNull()
    val birthYear = try {
        year?.run { Integer.parseInt(year) }
    } catch (e: NumberFormatException) {
        // Not eligible if could not get birthday year
        return false
    } catch (e: NullPointerException) {
        return false
    }

    val maxValue = if (max.isNullOrEmpty()) "26" else max
    val minValue = if (min.isNullOrEmpty()) "18" else min

    val minBirthYear =
        getSystemCurrentYearInt() - maxValue.run {
            try {
                Integer.parseInt(this)
            } catch (e: NumberFormatException) {
                // Default max age if could not get it
                26
            } catch (e: Exception) {
                // Default max age if could not get it
                26
            }
        }

    val maxBirthYear =
        getSystemCurrentYearInt() - minValue.run {
            try {
                Integer.parseInt(this)
            } catch (e: NumberFormatException) {
                // Default min age if could not get it
                18
            } catch (e: Exception) {
                // Default min age if could not get it
                18
            }
        }

    if (birthYear in minBirthYear..maxBirthYear) {
        return true
    }
    return false
}

private fun getSystemCurrentTime(dateFormat: String = yyyy_MM_dd_HH_mm_ss): String {
    try {
        val dateFormat: DateFormat
        val date = Date(System.currentTimeMillis())
        val calendar = GregorianCalendar()
        calendar.time = date
        dateFormat = SimpleDateFormat(yyyy_MM_dd_HH_mm_ss, localizationRepository.getAppLocale())
        return dateFormat.format(calendar.time)
    } catch (e: Exception) {
        return ""
    }
}

private fun getSystemCurrentYearInt(): Int {
    return try {
        Integer.parseInt(getSystemCurrentTime(dateFormat = yyyy))
    } catch (e: NumberFormatException) {
        // Any error occur, Use this year at 2018
        2018
    }
}

fun String?.formatterSport(format: String): String {
    val locale = localizationRepository.getAppLocale()

    val formatter = SimpleDateFormat(yyyy_MM_dd_T_HH_mm_ss_SSS_Z, locale)
    val date = formatter.parse(this)

    val newFormatter = SimpleDateFormat(format, locale)
    return newFormatter.format(date)
}

fun String?.formatterFullDateTime(context: Context, localLanguage: GetLocalizationUseCase): String {
    val dateTimeFormat = if (localLanguage.execute() == LocalizationRepository.Localization.TH) {
        SimpleDateFormat(yyyy_MM_dd_HH_mm_ss, Locale("th", "TH"))
    } else {
        SimpleDateFormat(yyyy_MM_dd_HH_mm_ss, Locale("en", "EN"))
    }
    dateTimeFormat.timeZone = TimeZone.getTimeZone("GMT+07:00")

    val dateTimeLocal = dateTimeFormat.format(Calendar.getInstance().time)
    var finalDateTime = ""

    try {

        val timeDateInboxMessage = dateTimeFormat.parse(this)
        val timeDateLocal = dateTimeFormat.parse(dateTimeLocal)

        // in milliseconds
        val diff = timeDateLocal.time - timeDateInboxMessage.time

        var diffMinutes = diff / MINUTES_MILLI % 60
        var diffHours = diff / HOURS_MILLI % 24
        val diffDays = diff / DAYS_MILLI

        if (diffDays.toInt() == 0 && diffHours.toInt() == 0) {

            if (diffMinutes < 0L) diffMinutes = 0L

            if (diffMinutes == 0L) {
                finalDateTime = context.getString(R.string.notification_time_just_now)
            } else if (diffMinutes == 1L) {
                finalDateTime =
                    "$diffMinutes ${context.getString(R.string.notification_time_min_ago)}"
            } else {
                finalDateTime =
                    "$diffMinutes ${context.getString(R.string.notification_time_mins_ago)}"
            }
        } else if (diffDays.toInt() == 0) {

            if (diffHours < 0L) diffHours = 0L
            if (diffHours.toInt() == 1) {
                finalDateTime =
                    "$diffHours ${context.getString(R.string.notification_time_hour_ago)}"
            } else {
                finalDateTime =
                    "$diffHours ${context.getString(R.string.notification_time_hours_ago)}"
            }
        } else if (diffDays.toInt() == 1) {

            var dateTimeDay = SimpleDateFormat(FORMAT_INBOX_YESTERDAY_EN, Locale("en", "EN"))
            if (localLanguage.execute() == LocalizationRepository.Localization.TH) {
                dateTimeDay = SimpleDateFormat(FORMAT_INBOX_YESTERDAY_TH, Locale("th", "TH"))
            }
            val dateTime = dateTimeDay.format(timeDateInboxMessage)

            finalDateTime = "${context.getString(R.string.notification_time_yesterday)} $dateTime"
        } else if (diffDays >= 2) {

            if (localLanguage.execute() == LocalizationRepository.Localization.TH) {
                val formatter = SimpleDateFormat(yyyy_MM_dd_HH_mm_ss, Locale("th", "TH"))
                val newFormatter = SimpleDateFormat(FORMAT_INBOX_TH, Locale("th", "TH"))

                val calendar = Calendar.getInstance()
                calendar.time = formatter.parse(this)
                calendar[Calendar.YEAR] = calendar[Calendar.YEAR] + 543
                finalDateTime = newFormatter.format(calendar.time)
            } else {
                val formatter = SimpleDateFormat(FORMAT_INBOX_EN, Locale("en", "EN"))
                finalDateTime = formatter.format(timeDateInboxMessage)
            }
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return finalDateTime
}

fun String.alertDecideDialog(context: Context, listener: DialogInterface.OnClickListener) {
    val alertDialog = AlertDialog.Builder(context)
        .setMessage(this)
        .setCancelable(false)
        .setPositiveButton(R.string.label_go_to_setting_button, listener)
        .setNegativeButton(R.string.cancel, listener)
    alertDialog.show()
}

/**
 * @param this is 2019-02-02T12:00:00.000Z
 *
 * Example result
 * @return You can watch this program in 7 days
 * @return You can watch this program in 7 hrs 7 mins
 * @return You can watch this program in 7 mins 7 seconds
 */

fun String.toTimeDifferenceNow(context: Context): String {

    try {

        val dateFormat = SimpleDateFormat(yyyy_MM_dd_T_HH_mm_ss_SSS_Z, Locale.getDefault())

        val newDateString = dateFormat.format(Calendar.getInstance().time)

        val startDate = dateFormat.parse(this)
        val nowDate = dateFormat.parse(newDateString)

        val diff = startDate.time - nowDate.time

        val diffSeconds = diff / 1000 % 60
        val diffMinutes = diff / MINUTES_MILLI % 60
        val diffHours = diff / HOURS_MILLI % 24
        val diffDays = diff / DAYS_MILLI

        return if (diffDays > 0) {
            if (diffDays > 1) {
                context.getString(R.string.reminder_remaining_days, diffDays)
            } else {
                context.getString(R.string.reminder_remaining_day, diffDays)
            }
        } else if (diffHours > 0) {
            if (diffHours > 1) {
                if (diffMinutes > 1) {
                    context.getString(R.string.reminder_remaining_hrs_mins, diffHours, diffMinutes)
                } else {
                    context.getString(R.string.reminder_remaining_hrs_min, diffHours, diffMinutes)
                }
            } else {
                if (diffMinutes > 1) {
                    context.getString(R.string.reminder_remaining_hr_mins, diffHours, diffMinutes)
                } else {
                    context.getString(R.string.reminder_remaining_hr_min, diffHours, diffMinutes)
                }
            }
        } else if (diffMinutes > 0) {
            if (diffMinutes > 1) {
                if (diffSeconds > 1) {
                    context.getString(
                        R.string.reminder_remaining_mins_seconds,
                        diffMinutes,
                        diffSeconds
                    )
                } else {
                    context.getString(
                        R.string.reminder_remaining_mins_second,
                        diffMinutes,
                        diffSeconds
                    )
                }
            } else {
                if (diffSeconds > 1) {
                    context.getString(
                        R.string.reminder_remaining_min_seconds,
                        diffMinutes,
                        diffSeconds
                    )
                } else {
                    context.getString(
                        R.string.reminder_remaining_min_second,
                        diffMinutes,
                        diffSeconds
                    )
                }
            }
        } else if (diffSeconds > 0) {
            if (diffSeconds > 1) {
                context.getString(R.string.reminder_remaining_seconds, diffSeconds)
            } else {
                context.getString(R.string.reminder_remaining_second, diffSeconds)
            }
        } else {
            ""
        }
    } catch (e: ParseException) {
        return ""
    }
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

fun String.isContainsInStringList(stringList: List<String>, ignoreCase: Boolean = false): Boolean {
    val stringValue = stringList.find {
        this.contains(it, ignoreCase)
    }
    return stringValue != null
}

fun String.isEqualsInStringList(stringList: List<String>, ignoreCase: Boolean = false): Boolean {
    val stringValue = stringList.find {
        this.equals(it, ignoreCase)
    }
    return stringValue != null
}
