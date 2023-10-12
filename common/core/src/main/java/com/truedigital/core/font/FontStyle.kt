package com.truedigital.core.font

import android.content.Context
import android.graphics.Typeface

enum class FontStyle(val fontName: String) {
    Regular("ThaiSansNeue-Regular"),
    BOLD("ThaiSansNeue-Bold"),
    EXTRA_BOLD("ThaiSansNeue-ExtraBold"),
    LIGHT("ThaiSansNeue-Light"),
    SKBOLD("SukhumvitTadmaiBold"),
    SKTEXT("SukhumvitTadmaiText"),
    FONT_AWESOME("fontawesome-solid-900");
}

class FontManager {

    companion object {
        fun getTypeFace(context: Context, font: FontStyle): Typeface {
            return Typeface.createFromAsset(context.assets, "fonts/${font.fontName}.ttf")
        }
    }
}
