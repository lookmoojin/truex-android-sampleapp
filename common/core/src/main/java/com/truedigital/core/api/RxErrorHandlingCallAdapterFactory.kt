package com.truedigital.core.api

import com.newrelic.agent.android.NewRelic
import com.truedigital.core.api.RetrofitException.Companion.asRetrofitException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.lang.reflect.Type

class RxErrorHandlingCallAdapterFactory private constructor() : CallAdapter.Factory() {
    private val original = RxJava2CallAdapterFactory.create()

    companion object {
        fun create(): CallAdapter.Factory {
            return RxErrorHandlingCallAdapterFactory()
        }
    }

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        return RxCallAdapterWrapper(original.get(returnType, annotations, retrofit) ?: return null)
    }

    private class RxCallAdapterWrapper<R>(private val wrapped: CallAdapter<R, *>) : CallAdapter<R, Any> {

        override fun responseType(): Type {
            return wrapped.responseType()
        }

        override fun adapt(call: Call<R>): Any {
            return when (val result = wrapped.adapt(call)) {
                is Single<*> -> result.onErrorResumeNext(Function { throwable -> Single.error(asRetrofitException(call.request().url.toString(), throwable)) })
                is Observable<*> -> result.onErrorResumeNext(Function { throwable -> Observable.error(asRetrofitException(call.request().url.toString(), throwable)) })
                is Completable -> result.onErrorResumeNext(Function { throwable -> Completable.error(asRetrofitException(call.request().url.toString(), throwable)) })
                else -> result
            }
        }
    }
}

class RetrofitException private constructor(
    message: String?,
    val url: String?,
    val response: Response<*>?,
    val kind: Kind,
    exception: Throwable
) : RuntimeException(message, exception) {

    enum class Kind {
        NETWORK,
        HTTP,
        ILLEGAL_ARGUMENT,
        UNEXPECTED
    }

    companion object {
        private fun httpError(url: String, response: Response<*>, httpException: HttpException): RetrofitException {
            val message = response.code().toString() + " " + response.message()

            val handlingExceptionMap = mapOf(
                "Key" to "Retrofit_ErrorHandling",
                "Value" to "HTTP Error with $url"
            )

            NewRelic.recordHandledException(Exception(httpException.localizedMessage), handlingExceptionMap)

            return RetrofitException(message, url, response, Kind.HTTP, httpException)
        }

        private fun networkError(requestURL: String, exception: IOException): RetrofitException {

            val handlingExceptionMap = mapOf(
                "Key" to "Retrofit_ErrorHandling",
                "Value" to "Network Error when calling $requestURL"
            )

            NewRelic.recordHandledException(Exception(exception.localizedMessage), handlingExceptionMap)

            return RetrofitException(exception.message, null, null, Kind.NETWORK, exception)
        }

        private fun illegalArgumentError(requestURL: String, exception: IllegalArgumentException): RetrofitException {

            val handlingExceptionMap = mapOf(
                "Key" to "Retrofit_ErrorHandling",
                "Value" to "IllegalArgumentException when calling $requestURL"
            )

            NewRelic.recordHandledException(Exception(exception.localizedMessage), handlingExceptionMap)

            return RetrofitException(exception.message, null, null, Kind.ILLEGAL_ARGUMENT, exception)
        }

        private fun unexpectedError(requestURL: String, exception: Throwable): RetrofitException {

            val handlingExceptionMap = mapOf(
                "Key" to "Retrofit_ErrorHandling",
                "Value" to "Unexpected Error with $requestURL"
            )

            NewRelic.recordHandledException(Exception(exception.localizedMessage), handlingExceptionMap)

            return RetrofitException(exception.message, null, null, Kind.UNEXPECTED, exception)
        }

        fun asRetrofitException(requestURL: String, throwable: Throwable): RetrofitException? {

            return when (throwable) {
                is RetrofitException -> throwable
                is HttpException -> {
                    val response = throwable.response()
                    return response?.let { httpError(response.raw().request.url.toString(), it, throwable) }
                }
                is IOException -> networkError(requestURL, throwable)
                is IllegalArgumentException -> illegalArgumentError(requestURL, throwable)
                else -> unexpectedError(requestURL, throwable)
            }
        }
    }

    override fun toString(): String {
        return super.toString() + " : " + kind + " : " + url + " : " + response?.errorBody()?.string()
    }
}
