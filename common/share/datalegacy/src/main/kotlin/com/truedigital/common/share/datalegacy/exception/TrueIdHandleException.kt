package com.truedigital.common.share.datalegacy.exception

import com.newrelic.agent.android.NewRelic
import okhttp3.Headers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class TrueIdHandleException<T>(
    private val headers: ((Headers?) -> Unit)? = null,
    private val success: (T?) -> Unit,
    private val fail: ((TrueIdHandleExceptionType, Throwable?) -> Unit)?
) : Callback<T> {

    override fun onFailure(call: Call<T>, throwable: Throwable) {
        val exception = TrueIdExceptionHelper(throwable.message.toString(), call.request().url.toString())

        NewRelic.recordHandledException(exception)

        val exceptionType = when (throwable::class.java) {
            UnknownHostException::class.java -> TrueIdHandleExceptionType.NO_INTERNET
            IOException::class.java -> TrueIdHandleExceptionType.NO_INTERNET
            InterruptedIOException::class.java -> TrueIdHandleExceptionType.NO_INTERNET
            SocketTimeoutException::class.java -> TrueIdHandleExceptionType.TIMEOUT
            StringIndexOutOfBoundsException::class.java -> TrueIdHandleExceptionType.TIMEOUT
            else -> TrueIdHandleExceptionType.GENERAL_ERROR
        }

        fail?.invoke(exceptionType, throwable)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        headers?.invoke(response.headers())

        val url = call.request().url.toString()

        when (response.code()) {
            in 200..399 -> {
                if (response.body() == null || response.body() == "null") {

                    fail?.invoke(TrueIdHandleExceptionType.NULL_BODY, null)

                    val handlingExceptionMap = mapOf(
                        "Key" to "TrueIdHandleException",
                        "Value" to "Response 200..399 but response is NULL with $url"
                    )

                    NewRelic.recordHandledException(Exception("NULL"), handlingExceptionMap)
                } else {

                    try {
                        success.invoke(response.body())
                    } catch (e: Throwable) {

                        fail?.invoke(TrueIdHandleExceptionType.UNEXPECTED_RESPONSE, null)

                        val handlingExceptionMap = mapOf(
                            "Key" to "TrueIdHandleException",
                            "Value" to "Response 200..399 but have UNEXPECTED RESPONSE with $url"
                        )

                        NewRelic.recordHandledException(Exception(e.localizedMessage), handlingExceptionMap)
                    }
                }
            }
            else -> {
                val exceptionType = createExceptionType(code = response.code())

                val handlingExceptionMap = mapOf(
                    "Key" to "TrueIdHandleException",
                    "Value" to "Have some Exception errors with $url"
                )

                NewRelic.recordHandledException(Exception(exceptionType.name), handlingExceptionMap)

                fail?.invoke(exceptionType, null)
            }
        }
    }
}

class TrueIdExceptionHelper(errorType: String, url: String) : Exception("$errorType - $url")

class TrueIdMessageExceptionHelper(val errorCode: Int, errorType: String, errorMessages: String) : Exception("$errorType - $errorCode - $errorMessages")

fun <T> Call<T>.enqueue(
    headers: ((Headers?) -> Unit)? = null,
    success: (T?) -> Unit,
    fail: ((TrueIdHandleExceptionType, Throwable?) -> Unit)?
) {
    enqueue(TrueIdHandleException<T>(headers, success, fail))
}

fun createExceptionType(code: Int): TrueIdHandleExceptionType {
    return when (code) {
        400 -> TrueIdHandleExceptionType.BAD_REQUEST
        401 -> TrueIdHandleExceptionType.UNAUTHORIZED
        403 -> TrueIdHandleExceptionType.FORBIDDEN
        404 -> TrueIdHandleExceptionType.NOT_FOUND
        409 -> TrueIdHandleExceptionType.CONFLICT
        500 -> TrueIdHandleExceptionType.INTERNAL_SERVER_ERROR
        502 -> TrueIdHandleExceptionType.BAD_GATEWAY
        503 -> TrueIdHandleExceptionType.SERVICE_UNAVAILABLE
        504 -> TrueIdHandleExceptionType.GATEWAY_TIMEOUT
        in 500..599 -> TrueIdHandleExceptionType.INTERNAL_SERVER_ERROR
        else -> TrueIdHandleExceptionType.GENERAL_ERROR
    }
}
