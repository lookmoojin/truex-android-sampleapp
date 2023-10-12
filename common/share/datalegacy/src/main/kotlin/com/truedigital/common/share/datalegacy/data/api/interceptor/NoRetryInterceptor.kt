package com.truedigital.common.share.datalegacy.data.api.interceptor

import com.newrelic.agent.android.NewRelic
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.coroutines.CoroutineContext

class NoRetryInterceptor(
    private val contentType: String,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : Interceptor {

    companion object {
        private const val HEADER_KEY_CONTENT_TYPE = "Content-Type"
        const val CONTENT_TYPE_JSON = "application/json"
        const val CONTENT_TYPE_MULTIPART_FORM_DATA = "multipart/form-data"
        const val CONTENT_TYPE_NON = ""
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            return@runBlocking try {
                withContext(coroutineDispatcherProvider.io()) {
                    addContentTypeToHeader(chain)
                }
            } catch (error: CancellationException) {
                logExceptionToNewRelic(error)
                throw error
            } catch (error: Exception) {
                logExceptionToNewRelic(error)
                throw error
            }
        }
    }

    private fun addContentTypeToHeader(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .header(HEADER_KEY_CONTENT_TYPE, contentType)
            .build()
        return chain.proceed(newRequest)
    }

    private fun logExceptionToNewRelic(error: Exception, context: CoroutineContext? = null) {
        val handlingExceptionMap = mapOf(
            "Key" to "NoRetryInterceptor.intercept()",
            "Value" to "Problem with Coroutine caused by $error in context $context\""
        )
        NewRelic.recordHandledException(Exception(error), handlingExceptionMap)
    }
}
