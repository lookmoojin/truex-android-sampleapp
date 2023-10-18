package com.truedigital.common.share.currentdate.usecase

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import com.truedigital.common.share.currentdate.DateTimeInterface
import com.truedigital.common.share.currentdate.model.TimeResourceUnitModel
import com.truedigital.core.constant.DateFormatConstant
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.DAYS_MILLI
import com.truedigital.core.extensions.HOURS_MILLI
import com.truedigital.core.extensions.MINUTES_MILLI
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

interface ConvertToDateFormatUseCase {
    fun execute(
        date: String,
        dateFormat: String,
        timeResourceUnitModel: TimeResourceUnitModel = TimeResourceUnitModel(),
        dateTime: Date = Calendar.getInstance().time
    ): String
}

class ConvertToDateFormatUseCaseImpl @Inject constructor(
    private val context: Context,
    private val localizationRepository: LocalizationRepository,
    private val dateTimeUtil: DateTimeInterface
) : ConvertToDateFormatUseCase {

    override fun execute(
        date: String,
        dateFormat: String,
        timeResourceUnitModel: TimeResourceUnitModel,
        dateTime: Date
    ): String {
        val local = localizationRepository.getAppLocale()
        val dateMilliSeconds =
            dateTimeUtil.convertToMilliSeconds(date, local)
        val simpleDateFormat = SimpleDateFormat(dateFormat, local)
        val dateTimeLocal = simpleDateFormat.format(dateTime)
        var resultDate = ""
        try {
            val timeDateLocal = simpleDateFormat.parse(dateTimeLocal) ?: Date()
            val diff = timeDateLocal.time - dateMilliSeconds

            val diffMinutes = diff / MINUTES_MILLI % 60
            val diffHours = diff / HOURS_MILLI % 24
            val diffDays = diff / DAYS_MILLI

            if (diffDays.toInt() == 0 && diffHours.toInt() == 0) {
                resultDate = when {
                    diffMinutes < 0L -> {
                        ""
                    }
                    diffMinutes == 0L -> {
                        getStringResourceByLanguage(timeResourceUnitModel.justNow)
                    }
                    diffMinutes == 1L -> {
                        "$diffMinutes ${getStringResourceByLanguage(timeResourceUnitModel.minuteAgo)}"
                    }
                    else -> {
                        "$diffMinutes ${getStringResourceByLanguage(timeResourceUnitModel.minutesAgo)}"
                    }
                }
            } else if (diffDays.toInt() == 0) {
                resultDate = when {
                    diffHours.toInt() < 0L -> {
                        ""
                    }
                    diffHours.toInt() == 1 -> {
                        "$diffHours ${getStringResourceByLanguage(timeResourceUnitModel.hourAgo)}"
                    }
                    else -> {
                        "$diffHours ${getStringResourceByLanguage(timeResourceUnitModel.hoursAgo)}"
                    }
                }
            } else if (diffDays.toInt() == 1) {
                resultDate = getStringResourceByLanguage(timeResourceUnitModel.yesterday)
            } else if (diffDays in 2..7) {
                resultDate =
                    "$diffDays ${getStringResourceByLanguage(timeResourceUnitModel.daysAgo)}"
            } else if (diffDays < 0) {
                resultDate = ""
            } else {
                resultDate = dateTimeUtil.convertTimeMillisToBuddhistYear(
                    dateMilliSeconds,
                    DateFormatConstant.d_MMM_yy
                )
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return if (dateMilliSeconds != 0L) {
            resultDate
        } else {
            ""
        }
    }

    private fun getStringResourceByLanguage(@StringRes stringResId: Int): String {
        val configuration = Configuration(context.resources?.configuration)
        configuration.setLocale(Locale(localizationRepository.getAppLanguageCode()))
        return context.createConfigurationContext(configuration)?.resources?.getString(stringResId)
            .orEmpty()
    }
}
