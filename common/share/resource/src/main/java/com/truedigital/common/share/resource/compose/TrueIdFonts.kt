package com.truedigital.common.share.resource.compose

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.truedigital.common.share.resource.R

object TrueIdFonts {

    private val defaultFont = listOf(
        Font(R.font.noto_sans_thai_regular, FontWeight.Normal),
        Font(R.font.noto_sans_thai_bold, FontWeight.Bold)
    )

    var current: FontFamily = FontFamily(defaultFont)
        private set

    fun setFontsByLang(lang: String): FontFamily {
        val fonts = when {
            lang.contains("th") -> {
                defaultFont
            }
            lang.contains("my") -> {
                listOf(
                    Font(R.font.noto_sans_myanmar_regular, FontWeight.Normal),
                    Font(R.font.noto_sans_myanmar_bold, FontWeight.Bold)
                )
            }
            lang.contains("km") -> {
                listOf(
                    Font(R.font.noto_sans_khmer_regular, FontWeight.Normal),
                    Font(R.font.noto_sans_khmer_bold, FontWeight.Bold)
                )
            }
            lang.contains("en") -> {
                listOf(
                    Font(R.font.noto_sans_regular, FontWeight.Normal),
                    Font(R.font.noto_sans_bold, FontWeight.Bold)
                )
            }
            else -> {
                defaultFont
            }
        }
        current = FontFamily(fonts)
        return current
    }
}
