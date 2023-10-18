package com.truedigital.features.music.api.interceptor

import com.newrelic.agent.android.NewRelic
import com.truedigital.common.share.datalegacy.data.endpoint.ApiConfigurationManager
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.domain.authentication.usecase.RefreshMusicTokenUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class MusicRefreshTokenInterceptor @Inject constructor(
    private val apiConfigurationManager: ApiConfigurationManager,
    private val refreshMusicTokenUseCase: RefreshMusicTokenUseCase,
    private val loginManagerInterface: LoginManagerInterface
) : Interceptor {

    companion object {
        const val BEARER_VALUE = "Bearer"
    }

    private val handler = CoroutineExceptionHandler { context, error ->
        val handlingExceptionMap = mapOf(
            "Key" to "MusicRequestTokenInterceptor.handler()",
            "Value" to "Problem with Coroutine caused by $error in context $context"
        )
        NewRelic.recordHandledException(Exception(error), handlingExceptionMap)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // check internet connection first, before using validate access token
        if (!ConnectivityStateHolder.isConnected) {
            return chain.proceed(chain.request())
        }

        val featurePath = chain.request().url.pathSegments.first()
        val isApiSupportJwt = apiConfigurationManager.isApiSupportJwt(featurePath)
        return if (isApiSupportJwt && loginManagerInterface.isLoggedIn()) {
            runBlocking {
                return@runBlocking try {
                    withContext(Dispatchers.IO + handler) {
                        return@withContext refreshToken(chain)
                    }
                } catch (error: CancellationException) {
                    val handlingExceptionMap = mapOf(
                        "Key" to "MusicRequestTokenInterceptor.refreshToken()",
                        "Value" to "Problem with Coroutine caused by $error "
                    )
                    NewRelic.recordHandledException(Exception(error), handlingExceptionMap)
                    chain.proceed(chain.request())
                } catch (error: Exception) {
                    val handlingExceptionMap = mapOf(
                        "Key" to "MusicRequestTokenInterceptor.refreshToken()",
                        "Value" to "Problem with Coroutine caused by $error "
                    )
                    NewRelic.recordHandledException(Exception(error), handlingExceptionMap)
                    chain.proceed(chain.request())
                }
            }
        } else {
            chain.proceed(chain.request())
        }
    }

    private suspend fun refreshToken(chain: Interceptor.Chain): Response {
        return refreshMusicTokenUseCase.execute()
            .map { token ->
                getSuccessResponse(chain, token)
            }.catch { throwable ->
                throw throwable
            }.single()
    }

    private fun getSuccessResponse(chain: Interceptor.Chain, token: String): Response {
        val request = chain.request().newBuilder()
            .header(MusicConstant.Intercepter.AUTHORIZATION_HEADER, "$BEARER_VALUE $token")
            .method(chain.request().method, chain.request().body)
            .build()
        return chain.proceed(request)
    }
}
