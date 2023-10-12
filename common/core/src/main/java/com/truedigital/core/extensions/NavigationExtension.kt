package com.truedigital.core.extensions

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.newrelic.agent.android.NewRelic
import timber.log.Timber

fun NavController.navigateSafe(_navigationId: Int, bundle: Bundle? = null) {
    try {
        this.navigate(_navigationId, bundle)
    } catch (ex: IllegalArgumentException) {
        Timber.e("Can't open 2 links at once! ${ex.message}")
        trackNewrelic(_navigationId.toString(), "IllegalArgumentException", ex)
    } catch (ex: Exception) {
        Timber.e("Can't open 2 links at once! ${ex.message}")
        trackNewrelic(_navigationId.toString(), "Exception", ex)
    }
}

fun NavController.navigateSafe(deepLink: Uri, navOptions: NavOptions? = null) {
    try {
        this.navigate(deepLink, navOptions)
    } catch (ex: IllegalArgumentException) {
        Timber.e("Can't open deeplink! ${ex.message}")
        trackNewrelic(deepLink.toString(), "IllegalArgumentException", ex)
    } catch (ex: Exception) {
        Timber.e("Can't open deeplink! ${ex.message}")
        trackNewrelic(deepLink.toString(), "Exception", ex)
    }
}

private fun trackNewrelic(deepLink: String, exceptionType: String, ex: Exception) {
    val handlingExceptionMap = mapOf(
        "Key" to "navigateSafe_deeplink",
        "Value" to "$exceptionType when calling $deepLink"
    )
    NewRelic.recordHandledException(
        Exception(ex.message),
        handlingExceptionMap
    )
}
