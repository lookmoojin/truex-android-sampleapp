package com.truedigital.core.utils.extension

import android.content.res.Configuration
import java.util.Locale

fun Configuration.getLocale(): Locale {
    return locales[0]
}
