package com.truedigital.features.tuned.common.extensions

import com.truedigital.features.tuned.data.api.ConnectivityInterceptor
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber

fun <T> Single<T>.cacheOnMainThread(): Single<T> =
    this.cache().observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.emptySubscribe(): Disposable =
    subscribe({}, { Timber.e(it) })

fun <T> Single<T>.successSubscribe(onSuccess: (t: T) -> Unit): Disposable =
    subscribe({ onSuccess(it) }, { Timber.e(it) })

fun <T> Single<T>.tunedSubscribe(
    onSuccess: (t: T) -> Unit,
    onError: (throwable: Throwable) -> Unit
): Disposable =
    subscribe(
        {
            onSuccess(it)
        },
        {
            Timber.e(it)
            if (it !is ConnectivityInterceptor.NoConnectivityException) onError(it)
        }
    )

val Disposable.isNotDisposed
    get() = !isDisposed
