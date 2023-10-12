package com.truedigital.core.extensions

import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.atomic.AtomicReference

fun <T : Any> ObservableSource<T>.asFlow(): Flow<T> = callbackFlow {
    val disposableRef = AtomicReference<Disposable>()
    val observer = object : Observer<T> {
        override fun onComplete() {
            close()
        }

        override fun onSubscribe(d: Disposable) {
            if (!disposableRef.compareAndSet(null, d)) d.dispose()
        }

        override fun onNext(t: T) {
            /*
             * Channel was closed by the downstream, so the exception (if any)
             * also was handled by the same downstream
             */
            try {
                trySendBlocking(t)
            } catch (e: InterruptedException) {
                // RxJava interrupts the source
            }
        }

        override fun onError(e: Throwable) {
            close(e)
        }
    }

    subscribe(observer)
    awaitClose { disposableRef.getAndSet(Disposables.disposed())?.dispose() }
}
