package com.truedigital.features.truecloudv3.data.api

import com.newrelic.agent.android.NewRelic
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.core.data.repository.DeviceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.coroutines.CoroutineContext

class TrueCloudV3OauthInterceptor(
    private val deviceRepository: DeviceRepository,
    private val authManagerWrapper: AuthManagerWrapper,
    private val userRepository: UserRepository
) : Interceptor {

    companion object {
        private const val HEADER_KEY_CONTENT_TYPE = "Content-Type"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_DEVICE_ID = "device-id"
        const val CONTENT_TYPE_JSON = "application/json"

        const val CONTENT_TYPE_MULTIPART_FORM_DATA = "multipart/form-data"
        const val CONTENT_TYPE_NON = ""
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            return@runBlocking try {
                withContext(Dispatchers.IO) {
                    addContentTypeToHeader(chain, deviceRepository.getAndroidId())
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

    private fun addContentTypeToHeader(chain: Interceptor.Chain, deviceId: String): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader(HEADER_KEY_CONTENT_TYPE, CONTENT_TYPE_JSON)
            .addHeader(HEADER_AUTHORIZATION, "Bearer " + userRepository.getAccessToken())
            // TODO: reverse
//            .addHeader(HEADER_AUTHORIZATION, "Bearer " + authManagerWrapper.getAccessToken())
            .addHeader(HEADER_DEVICE_ID, deviceId)
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
