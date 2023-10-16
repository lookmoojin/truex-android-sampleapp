package com.truedigital.component.extension

import com.truedigital.core.constant.DateFormatConstant.HH_mm
import com.truedigital.core.constant.DateFormatConstant.MMMM_dd_yyyy
import com.truedigital.core.constant.DateFormatConstant.MMM_dd_yyyy
import com.truedigital.core.constant.DateFormatConstant.d_MMM_yyyy
import com.truedigital.core.constant.DateFormatConstant.dd_MMMM_yyyy
import com.truedigital.core.constant.DateFormatConstant.dd_MMM_yyyy_HH_mm
import com.truedigital.core.constant.DateFormatConstant.dd_MM_yyyy_SLASH
import com.truedigital.core.constant.DateFormatConstant.yyyy_MM_dd_DASH
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.domain.usecase.GetAppLocaleUseCaseImpl
import com.truedigital.core.domain.usecase.GetLocalizationUseCase
import com.truedigital.core.domain.usecase.GetLocalizationUseCaseImpl
import com.truedigital.core.injections.CoreComponent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

const val FORMAT_DATE = "$dd_MM_yyyy_SLASH 'at' $HH_mm"
const val FORMAT_DATE_TH = "$dd_MM_yyyy_SLASH 'เวลา' $HH_mm 'น.'"

const val FORMAT_DATE_LONG_EN = MMMM_dd_yyyy
const val FORMAT_DATE_LONG_TH = dd_MMMM_yyyy

const val FORMAT_MOVIE_EXPIRE_EN = "$dd_MMMM_yyyy '-' $HH_mm"
const val FORMAT_MOVIE_EXPIRE_TH = "$dd_MMMM_yyyy '-' $HH_mm 'น.'"

const val FORMAT_INBOX_YESTERDAY_EN = HH_mm
const val FORMAT_INBOX_YESTERDAY_TH = "$HH_mm 'น.'"
const val FORMAT_INBOX_EN = dd_MMM_yyyy_HH_mm
const val FORMAT_INBOX_TH = "$dd_MMM_yyyy_HH_mm 'น.'"
const val FORMAT_MY_CHANNEL_EN = "$MMM_dd_yyyy 'at' $HH_mm"
const val FORMAT_MY_CHANNEL_TH = "$dd_MMMM_yyyy $HH_mm 'น.'"

const val DAY_TH = "วัน"

private val localizationRepository: LocalizationRepository by lazy {
    CoreComponent.getInstance().getCoreSubComponent().getLocalizationRepository()
}

fun Date.format(thaiFormat: String, engFormat: String): String {
    val getLocalizationUseCase = GetLocalizationUseCaseImpl(localizationRepository)
    val getAppLocaleUseCase = GetAppLocaleUseCaseImpl(localizationRepository)

    val sdf =
        if (getLocalizationUseCase.execute() == LocalizationRepository.Localization.TH) SimpleDateFormat(
            thaiFormat,
            getAppLocaleUseCase.execute()
        )
        else SimpleDateFormat(engFormat, localizationRepository.getAppLocaleForEnTh())

    val calendar = GregorianCalendar()
    calendar.time = this

    if (getLocalizationUseCase.execute() == LocalizationRepository.Localization.TH) {
        // Add constant for B.E. (Buddhist calendar)
        calendar.add(Calendar.YEAR, 543)
    }

    return sdf.format(calendar.time)
}

fun Date.formatEn(format: String): String {
    val sdf = SimpleDateFormat(format, localizationRepository.getAppLocaleForEnTh())
    val calendar = GregorianCalendar()
    calendar.time = this

    return sdf.format(calendar.time)
}

fun Date.formatTh(format: String): String {
    val sdf = SimpleDateFormat(format, localizationRepository.getAppLocaleForEnTh())
    val calendar = GregorianCalendar()
    calendar.time = this
    calendar.add(Calendar.YEAR, 543)

    return sdf.format(calendar.time)
}

fun Date.formatEnTh(thaiFormat: String, engFormat: String): String {
    val getLocalizationUseCase: GetLocalizationUseCase =
        GetLocalizationUseCaseImpl(localizationRepository)

    return if (getLocalizationUseCase.execute() == LocalizationRepository.Localization.TH) {
        this.formatTh(thaiFormat)
    } else {
        this.formatEn(engFormat)
    }
}

fun Date.format(): String = this.format(FORMAT_DATE_TH, FORMAT_DATE)

fun Date.formatShort(): String = this.formatEn(yyyy_MM_dd_DASH)

fun Date.formatLong(): String = this.formatEnTh(FORMAT_DATE_LONG_TH, FORMAT_DATE_LONG_EN)

fun Date.formatMovieExpire(): String = this.format(FORMAT_MOVIE_EXPIRE_TH, FORMAT_MOVIE_EXPIRE_EN)

fun Date.formatWorldCupCoupon(): String = this.format(d_MMM_yyyy, d_MMM_yyyy)
