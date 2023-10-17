package com.tdg.login.base

import androidx.annotation.WorkerThread
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume

fun CoroutineScope.launchSafe(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    launchSafe: LaunchSafe = object : LaunchSafe {
        override fun onCancellation(e: CancellationException) {
            throw Exception(e)
        }

        override fun onFailure(t: Throwable) {
            throw Exception(t.cause)
        }
    },
    onStart: (suspend CoroutineScope.() -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
) = launch(context, start) {
    try {
        onStart?.invoke(this)
        block()
    } catch (e: CancellationException) {
        try {
            launchSafe.onCancellation(e)
        } catch (t: Throwable) {
            launchSafe.onFailure(t)
        }
    } catch (t: Throwable) {
        launchSafe.onFailure(t)
    }
}

fun <T> Flow<T>.launchSafeIn(scope: CoroutineScope): Job = scope.launchSafe {
    collect() // tail-call
}

suspend inline fun <T> Flow<T>.collectSafe(
    collectSafe: CollectSafe = object : CollectSafe {
        override fun onCancellation(e: CancellationException) {
            throw Exception(e.cause)
        }
    },
    crossinline action: suspend (value: T) -> Unit
): Unit =
    collect { value ->
        try {
            action(value)
        } catch (error: CancellationException) {
            collectSafe.onCancellation(error)
        }
    }

/**
 * Only call resume() if the CancellableContinuation was active
 * @link https://github.com/Kotlin/kotlinx.coroutines/issues/1874#issuecomment-936951673
 * */
fun <T> CancellableContinuation<T>.resumeIfActive(value: T) {
    if (isActive) {
        resume(value)
    }
}

interface LaunchSafe {

    @WorkerThread
    fun onCancellation(e: CancellationException)

    @WorkerThread
    fun onFailure(t: Throwable)
}

interface CollectSafe {

    @WorkerThread
    fun onCancellation(e: CancellationException)
}
