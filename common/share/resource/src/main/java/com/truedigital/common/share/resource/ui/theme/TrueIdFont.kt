package com.truedigital.common.share.resource.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.tdg.common.share.resource.R

private val NotoSansFamily = FontFamily(
    Font(resId = R.font.noto_sans_regular, weight = FontWeight.Normal),
    Font(resId = R.font.noto_sans_bold, weight = FontWeight.Bold)
)
private val NotoSansThaiFamily = FontFamily(
    Font(resId = R.font.noto_sans_thai_regular, weight = FontWeight.Normal),
    Font(resId = R.font.noto_sans_thai_bold, weight = FontWeight.Bold)
)
private val NotoSansMyanmarFamily = FontFamily(
    Font(resId = R.font.noto_sans_myanmar_regular, weight = FontWeight.Normal),
    Font(resId = R.font.noto_sans_myanmar_bold, weight = FontWeight.Bold)
)
private val NotoSansKhmerFamily = FontFamily(
    Font(resId = R.font.noto_sans_khmer_regular, weight = FontWeight.Normal),
    Font(resId = R.font.noto_sans_khmer_bold, weight = FontWeight.Bold)
)

@Immutable
class TrueIdFont internal constructor(
    val default: FontFamily,
    val notoSans: FontFamily
) {
    constructor(
        language: String = "en"
    ) : this(
        default = getNotoSansFontByLanguage(language),
        notoSans = getNotoSansFontByLanguage(language)
    )
}

private fun getNotoSansFontByLanguage(language: String): FontFamily = when {
    language.contains("th", true) -> NotoSansThaiFamily
    language.contains("my", true) -> NotoSansMyanmarFamily
    language.contains("km", true) -> NotoSansKhmerFamily
    else -> NotoSansFamily
}

val LocalFontFamily = staticCompositionLocalOf { TrueIdFont() }
