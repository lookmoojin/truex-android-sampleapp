package com.truedigital.features.tuned.common.extensions

import android.content.Context
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.foundation.extension.getLocal
import java.util.Locale

fun List<LocalisedString>.valueForSystemLanguage(context: Context): String? =
    localisedStringForSystemLanguage(context)?.value

fun List<LocalisedString>.localisedStringForSystemLanguage(context: Context): LocalisedString? =
    if (this.size == 0) {
        null
    } else {
        var locale = context.resources.getLocal().toString().lowercase(Locale.getDefault())
        locale = if (locale == "in") "id" else locale

        localisedStringForLanguage(locale)
    }

fun List<LocalisedString>.localisedStringForLanguage(language: String): LocalisedString? =
    if (size == 0) {
        null
    } else {
        // Try to find a value that matches the language
        firstOrNull {
            it.language.lowercase(Locale.getDefault())
                .contains(language) && !it.value.isNullOrEmpty()
        }?.let { return it }

        // If we still can't find one then then remove region specific identifiers in the language and try again
        val localeComponents = language.split("_")
        if (localeComponents.size > 1) {
            firstOrNull {
                it.language.lowercase(Locale.getDefault()) == localeComponents.first() &&
                    !it.value.isNullOrEmpty()
            }?.let { return it }
        }

        // If we still can't find one then try use EN as the default language and try again
        firstOrNull {
            it.language.lowercase(Locale.getDefault()).contains("en") && !it.value.isNullOrEmpty()
        }?.let { return it }

        // If there wasn't a region specific identifier and/or we still can't find anything
        // then just find the first thing with a non-null or empty value
        // otherwise give up and return null
        firstOrNull { !it.value.isNullOrEmpty() }
    }
