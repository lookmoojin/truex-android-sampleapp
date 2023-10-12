package com.truedigital.core.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.newrelic.agent.android.NewRelic
import com.truedigital.core.BuildConfig
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class UiLifecycleScope : CoroutineScope, LifecycleObserver {

    private lateinit var job: Job

    private val handler = CoroutineExceptionHandler { context, throwable ->
        if (BuildConfig.DEBUG) {
            Thread.currentThread()
                .uncaughtExceptionHandler
                ?.uncaughtException(Thread.currentThread(), throwable)
        } else {

            val handlingExceptionMap = mapOf(
                "Key" to "AuthManagerWrapper.handler()",
                "Value" to "Problem with Coroutine caused by ${throwable.message} in context $context"
            )
            NewRelic.recordHandledException(Exception(throwable.cause), handlingExceptionMap)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main + handler

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onCreate() {
        job = Job()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun destroy() = job.cancel()
}
