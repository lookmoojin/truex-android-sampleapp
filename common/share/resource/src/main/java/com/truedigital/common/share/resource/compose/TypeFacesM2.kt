package com.truedigital.common.share.resource.compose

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object TypeFacesM2 {

    fun getTypography(fontFamily: FontFamily) = Typography(
        defaultFontFamily = fontFamily,
        h1 = TextStyle(
            fontSize = 34.sp,
            lineHeight = 36.sp,
            fontFamily = fontFamily
        ),
        h2 = TextStyle(
            fontSize = 24.sp,
            fontFamily = fontFamily
        ),
        h3 = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily
        ),
        h4 = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily
        ),
        h5 = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily
        ),
        h6 = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily
        ),
        subtitle1 = TextStyle(
            fontSize = 16.sp,
            fontFamily = fontFamily
        ),
        subtitle2 = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily
        ),
        body1 = TextStyle(
            fontSize = 16.sp,
            fontFamily = fontFamily
        ),
        body2 = TextStyle(
            fontSize = 14.sp,
            fontFamily = fontFamily
        ),
        button = TextStyle(
            fontSize = 14.sp,
            fontFamily = fontFamily
        ),
        caption = TextStyle(
            fontSize = 12.sp,
            fontFamily = fontFamily
        ),
        overline = TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily
        )
    )
}
