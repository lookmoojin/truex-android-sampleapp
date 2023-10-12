package com.truedigital.foundation.extension

import android.content.res.Resources
import java.util.Locale

@Suppress("DEPRECATION")
fun Resources.getLocal(): Locale {
    val config = this.configuration
    return config.locales.get(0)
}
