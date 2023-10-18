package com.truedigital.features.tuned.domain.facade.tag.model

import timber.log.Timber

enum class TagDisplayType {
    LANDSCAPE, SQUARE;

    companion object {
        fun getValue(display: String?): TagDisplayType =
            display?.let {
                try {
                    valueOf(it.uppercase())
                } catch (e: Exception) {
                    Timber.e(e)
                    LANDSCAPE
                }
            } ?: LANDSCAPE
    }
}
