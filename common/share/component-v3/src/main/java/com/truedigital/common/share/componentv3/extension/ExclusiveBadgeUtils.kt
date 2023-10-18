package com.truedigital.common.share.componentv3.extension

import com.truedigital.core.constant.DateFormatConstant
import com.truedigital.core.extensions.checkNowDateIsBetweenStartDateAndEndDate
import com.truedigital.core.extensions.convertTimeFormat
import java.util.Locale

object ExclusiveBadgeUtils {

    const val TRUE_ID_ORIGINAL_EXCLUSIVE_BADGE = "true_original"
    const val TRUE_ID_ONLY_EXCLUSIVITY_BADGE = "exclusivity"
    const val TRUE_ID_FIRST_WINDOW_BADGE = "first_window"
    const val TRUE_VISION_CATCHUP_VOD_BADGE = "c-tv"
    const val BADGE_VECTOR_ICON = "1"
    const val BADGE_WEBP_ICON = "2"

    fun checkExclusiveBadgeExpirationDate(
        appLocale: Locale,
        exclusiveBadgeStartDate: String?,
        exclusiveBadgeEndDate: String?
    ): Boolean {

        val badgeEndDate = exclusiveBadgeEndDate?.convertTimeFormat(
            appLocale,
            DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z,
            DateFormatConstant.yyyy_MM_dd_HH_mm_ss
        ).orEmpty()
        val badgeStartDate = exclusiveBadgeStartDate?.convertTimeFormat(
            appLocale,
            DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z,
            DateFormatConstant.yyyy_MM_dd_HH_mm_ss
        ).orEmpty()

        return badgeEndDate.checkNowDateIsBetweenStartDateAndEndDate(badgeStartDate)
    }
}
