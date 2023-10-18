package com.truedigital.core.utils.networkconnection.common

import timber.log.Timber

/**
 * just like runCatching but without result
 * @see runCatching
 */
internal inline fun <T> T.safeRun(TAG: String = "", block: T.() -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        // ignore but log it
        Timber.e(e.toString())
    }
}
