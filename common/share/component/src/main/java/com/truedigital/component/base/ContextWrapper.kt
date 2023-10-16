package com.truedigital.component.base

import android.content.Context
import java.util.Locale

class ContextWrapper(base: Context) : android.content.ContextWrapper(base) {
    companion object {
        fun wrap(context: Context, newLocale: Locale): ContextWrapper {
            val res = context.resources
            val configuration = res.configuration

            configuration.setLocale(newLocale)
            res.updateConfiguration(configuration, res.displayMetrics)

            return ContextWrapper(context)
        }
    }
}
