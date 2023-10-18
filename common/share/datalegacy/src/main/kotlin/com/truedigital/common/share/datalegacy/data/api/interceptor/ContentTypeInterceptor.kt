package com.truedigital.common.share.datalegacy.data.api.interceptor

import com.newrelic.agent.android.NewRelic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.coroutines.CoroutineContext

class ContentTypeInterceptor(private val contentType: String) : Interceptor {

    companion object {
        private const val HEADER_KEY_CONTENT_TYPE = "Content-Type"
        const val CONTENT_TYPE_JSON = "application/json"
        const val CONTENT_TYPE_MULTIPART_FORM_DATA = "multipart/form-data"
        const val CONTENT_TYPE_NON = ""
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            return@runBlocking try {
                withContext(Dispatchers.IO) {
                    addContentTypeToHeader(chain)
                }
            } catch (error: CancellationException) {
                logExceptionToNewRelic(error)
                chain.proceed(chain.request())
            } catch (error: Exception) {
                logExceptionToNewRelic(error)
                chain.proceed(chain.request())
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
            "Key" to "ContentTypeInterceptor.intercept()",
            "Value" to "Problem with Coroutine caused by $error in context $context\""
        )
        NewRelic.recordHandledException(Exception(error), handlingExceptionMap)
    }
}
