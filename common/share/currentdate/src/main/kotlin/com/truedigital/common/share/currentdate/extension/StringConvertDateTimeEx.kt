package com.truedigital.common.share.currentdate.extension

import android.annotation.SuppressLint
import com.truedigital.core.constant.DateFormatConstant
import java.text.ParseException
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun String.convertToMilliSeconds(): Long {
    val sdf = SimpleDateFormat(DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z)
    return try {
        sdf.parse(this)?.time ?: 0L
    } catch (e: ParseException) {
        0L
    }
}
