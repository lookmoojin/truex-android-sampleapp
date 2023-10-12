package com.truedigital.core.base

import androidx.lifecycle.ViewModel
import com.newrelic.agent.android.NewRelic
import com.truedigital.core.BuildConfig
import com.truedigital.core.extensions.LaunchSafe
import com.truedigital.foundation.extension.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

open class ScopedViewModel : ViewModel(), CoroutineScope, LaunchSafe {

    private val job = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + job + safeCoroutineExceptionHandler())

    val errorLiveEvent = LiveEvent<Throwable>()

    override fun onCleared() {
        uiScope.coroutineContext.cancelChildren()
        super.onCleared()
    }

    override val coroutineContext: CoroutineContext
        get() = uiScope.coroutineContext

    override fun onCancellation(e: CancellationException) {
        // DO NOTHING
    }

    override fun onFailure(t: Throwable) {
        errorLiveEvent.postValue(t)
    }

    private fun safeCoroutineExceptionHandler() = CoroutineExceptionHandler { context, throwable ->
        if (BuildConfig.DEBUG) {
            Thread.currentThread()
                .uncaughtExceptionHandler
                ?.uncaughtException(Thread.currentThread(), throwable)
        } else {

            val handlingExceptionMap = mapOf(
                "Key" to "ScopedViewModel",
                "Value" to "Problem with Coroutine caused by ${throwable.message} in context $context"
            )
            NewRelic.recordHandledException(Exception(throwable.cause), handlingExceptionMap)
        }
    }
}
