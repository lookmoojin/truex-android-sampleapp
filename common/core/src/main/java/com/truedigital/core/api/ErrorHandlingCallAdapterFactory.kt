package com.truedigital.core.api

import com.newrelic.agent.android.NewRelic
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.Executor

/** A callback which offers granular callbacks for various conditions.  */
interface CommonCallback<T> {
    /** Called for [200, 300) responses.  */
    fun success(response: Response<T>?)

    /** Called for 401 responses.  */
    fun unauthenticated(response: Response<*>?)

    /** Called for [400, 500) responses, except 401.  */
    fun clientError(response: Response<*>?)

    /** Called for [500, 600) response.  */
    fun serverError(response: Response<*>?)

    /** Called for network errors while making the call.  */
    fun networkError(e: IOException?)

    /** Called for unexpected errors while making the call.  */
    fun unexpectedError(t: Throwable?)
}

interface CommonCall<T> {
    fun cancel()
    fun enqueue(callback: CommonCallback<T>?)
    fun clone(): CommonCall<T>
}

class ErrorHandlingCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? =
        when (CommonCall::class.java) {

            getRawType(returnType) -> {
                check(returnType is ParameterizedType) { "CommonCall must have generic type (e.g., CommonCall<ResponseBody>)" }
                val responseType = getParameterUpperBound(0, returnType)
                val callbackExecutor = retrofit.callbackExecutor()
                ErrorHandlingCallAdapter<Any>(responseType, callbackExecutor)
            }

            else -> null
        }

    private class ErrorHandlingCallAdapter<R>(
        private val responseType: Type,
        private val callbackExecutor: Executor?
    ) : CallAdapter<R, CommonCall<R>> {
        override fun responseType(): Type {
            return responseType
        }

        override fun adapt(call: Call<R>): CommonCall<R> {
            return CommonCallAdapter(call, callbackExecutor)
        }
    }
}

class CommonCallAdapter<T>(
    private val call: Call<T>,
    private val callbackExecutor: Executor?
) : CommonCall<T> {

    override fun cancel() {
        call.cancel()
    }

    override fun clone(): CommonCall<T> {
        return CommonCallAdapter(call.clone(), callbackExecutor)
    }

    override fun enqueue(callback: CommonCallback<T>?) {
        call.enqueue(
            object : Callback<T> {

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    when (response.code()) {
                        in 200..299 -> {

                            try {
                                callback?.success(response)
                            } catch (e: Throwable) {
                                callback?.unexpectedError(Throwable("Unexpected response $response"))

                                val handlingExceptionMap = mapOf(
                                    "Key" to "CommonCallAdapter",
                                    "Value" to "Response 200..299 but have errors with ${call.request().url}"
                                )

                                NewRelic.recordHandledException(Exception(e.localizedMessage), handlingExceptionMap)
                            }
                        }
                        401 -> {
                            callback?.unauthenticated(response)
                        }
                        in 400..499 -> {
                            callback?.clientError(response)
                        }
                        in 500..599 -> {
                            callback?.serverError(response)
                        }
                        else -> {
                            callback?.unexpectedError(RuntimeException("Unexpected response $response"))

                            val handlingExceptionMap = mapOf(
                                "Key" to "Retrofit_ErrorHandling",
                                "Value" to "Response 200..299 but have errors with ${call.request().url}"
                            )

                            NewRelic.recordHandledException(Exception("Undefined"), handlingExceptionMap)
                        }
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    if (t is IOException) {
                        callback?.networkError(t as IOException?)

                        val handlingExceptionMap = mapOf(
                            "Key" to "Retrofit_ErrorHandling",
                            "Value" to "Have some IOException errors with ${call.request().url}"
                        )

                        NewRelic.recordHandledException(Exception(t.localizedMessage), handlingExceptionMap)
                    } else {
                        callback?.unexpectedError(t)

                        val handlingExceptionMap = mapOf(
                            "Key" to "Retrofit_ErrorHandling",
                            "Value" to "Have some Undefined errors with ${call.request().url}"
                        )

                        NewRelic.recordHandledException(Exception(t.localizedMessage), handlingExceptionMap)
                    }
                }
            })
    }
}
