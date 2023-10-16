package com.truedigital.common.share.componentv3.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.truedigital.common.share.componentv3.R

enum class SnackBarType(@DrawableRes val icon: Int, @ColorRes val color: Int, @ColorRes val textColor: Int) {
    ERROR(R.drawable.ic_cross_circle, R.color.error_toast_color, R.color.white),
    ERROR_SPORT(R.drawable.ic_cross_circle, R.color.error_sport_toast_color, R.color.white),

    SUCCESS(R.drawable.ic_check_circle, R.color.success_toast_color, R.color.white),
    SUCCESS_SPORT(R.drawable.ic_check_circle, R.color.success_sport_toast_color, R.color.white),

    WARNING_SPORT(R.drawable.ic_warning, R.color.warning_sport_toast_color, R.color.white),

    WARNING_SFV(R.drawable.ic_warning_dark, R.color.warning_toast_color, R.color.black)
}
