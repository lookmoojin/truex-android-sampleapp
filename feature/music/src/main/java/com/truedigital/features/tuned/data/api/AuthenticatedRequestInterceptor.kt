package com.truedigital.features.tuned.data.api

import android.content.Context
import com.truedigital.core.extensions.activityManager
import com.truedigital.core.extensions.runOnUiThread
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.isDeviceUser
import com.truedigital.features.tuned.common.extensions.toast
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.device.model.Device
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepositoryImpl
import com.truedigital.features.tuned.injection.module.NetworkModule.Companion.HTTP_CODE_UNAUTHORISED
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.HttpException

class AuthenticatedRequestInterceptor(
    private val context: Context,
    private val userSharedPreferences: ObfuscatedKeyValueStoreInterface,
    private val authenticationTokenRepository: AuthenticationTokenRepository,
    private val deviceRepository: DeviceRepository
) : Interceptor {

    companion object {
        private const val HTTP_CODE_SUCCESS_MIN = 400
        private const val HTTP_CODE_SUCCESS_MAX = 499
        private const val MAX_NUMBER = 32
    }

    private var user: User? = null
    private lateinit var device: Device

    private var hasRefreshed = false

    // device and msisdn user can use logins to recreate token without user action
    private var canReauth = false
    private var hasReauthed = false

    // only when a 400ish response is received is considered as failure thus requires user action
    private var authFailure = false

    override fun intercept(chain: Interceptor.Chain): Response {
        synchronized(this) {
            // init all flags and objects
            initFlags()

            // Step 1 : App level check
            // if token expires, refresh it
            // if refresh fails, recreate it if possible
            // process request
            var response = appLevelCheck(chain)

            // Step2 : if we receive a 401 and haven't tried refresh
            // force refresh token
            // if refresh fails, recreate it if possible
            // process request
            if (response.code == HTTP_CODE_UNAUTHORISED && !hasRefreshed) {
                response.close()
                response = unauthorizedRefreshCheck(chain)
            }

            // Step 3 : if we receive a 401 and haven't tried recreate token
            // recreate it if possible
            // process request
            if (response.code == HTTP_CODE_UNAUTHORISED && canReauth && !hasReauthed) {
                response.close()
                response = unauthorizedRecreateCheck(chain)
            }

            // Step 4 : if still 401, user action will be required
            if (response.code == HTTP_CODE_UNAUTHORISED && user != null) {
                response.close()
                unauthorizedActions()
            }

            return response
        }
    }

    private fun initFlags() {
        user = userSharedPreferences.get(MusicUserRepositoryImpl.CURRENT_USER_KEY)
        device = deviceRepository.get()

        hasRefreshed = false
        canReauth = user?.logins?.isDeviceUser == true
        hasReauthed = false
        authFailure = false
    }

    private fun appLevelCheck(chain: Interceptor.Chain): Response {
        val accessToken = try {
            // get current or if it expires, refresh it
            authenticationTokenRepository.get(device.uniqueId)
                .map {
                    hasRefreshed = it.second
                    it.first
                }
                .blockingGet()
        } catch (e: Exception) {
            // if we can't get a token and we can reauth, do it now
            // if canReauth, user is not null
            if (canReauth && !hasReauthed) {
                try {
                    hasReauthed = true
                    authenticationTokenRepository.recreateToken(
                        device.uniqueId,
                        device.token,
                        user?.logins.orEmpty()
                    ).blockingGet()
                } catch (e: Exception) {
                    authFailure = e.isHttpClientErrors()
                    null
                }
            } else null
        }?.accessToken

        return processRequest(chain, accessToken)
    }

    private fun unauthorizedRefreshCheck(chain: Interceptor.Chain): Response {
        val accessToken = try {
            // force refresh the token
            authenticationTokenRepository.get(device.uniqueId, true)
                .map {
                    hasRefreshed = true
                    it.first
                }
                .blockingGet()
        } catch (e: Exception) {
            if (canReauth && !hasReauthed) {
                try {
                    hasReauthed = true
                    authenticationTokenRepository.recreateToken(
                        device.uniqueId,
                        device.token,
                        user?.logins.orEmpty()
                    ).blockingGet()
                } catch (e: Exception) {
                    authFailure = e.isHttpClientErrors()
                    null
                }
            } else null
        }?.accessToken

        return processRequest(chain, accessToken)
    }

    private fun unauthorizedRecreateCheck(chain: Interceptor.Chain): Response {
        // if we have a valid token and still we received a 401,
        // this could be that a msisdn has been logged on to another device
        // try get a new token by reauth and generate the new response
        val accessToken = try {
            authenticationTokenRepository.recreateToken(
                device.uniqueId,
                device.token,
                user?.logins.orEmpty()
            ).blockingGet()
        } catch (e: Exception) {
            authFailure = e.isHttpClientErrors()
            null
        }?.accessToken

        return processRequest(chain, accessToken)
    }

    private fun unauthorizedActions() {
        if (canReauth) {
            // if server rejects reauth, user action should be required here
            // shouldHideErrorAction indicates that user action should not be prompted on certain page
            if (authFailure && !shouldHideErrorAction()) {
                context.runOnUiThread {
                    // remove user Object
                    userSharedPreferences.remove(MusicUserRepositoryImpl.CURRENT_USER_KEY)
                    userSharedPreferences.remove(MusicUserRepositoryImpl.MSISDN_KEY)
                    context.toast(R.string.token_expired)
                }
            }
        } else {
            // shouldHideErrorAction indicates that user action should not be prompted on certain page
            if (!shouldHideErrorAction()) {
                context.runOnUiThread {
                    context.toast(R.string.token_expired)
                }
            }
        }
    }

    private fun Exception.isHttpClientErrors(): Boolean =
        this is HttpException && this.code() in (HTTP_CODE_SUCCESS_MIN..HTTP_CODE_SUCCESS_MAX)

    private fun processRequest(chain: Interceptor.Chain, accessToken: String?): Response =
        chain.proceed(
            chain.request().newBuilder()
                .header(MusicConstant.Intercepter.AUTHORIZATION_HEADER, "Bearer $accessToken")
                .build()
        )

    private fun shouldHideErrorAction(): Boolean {
        @Suppress("DEPRECATION")
        val runningTasks = context.activityManager().getRunningTasks(MAX_NUMBER)
        return runningTasks.any {
            it.baseActivity?.packageName == context.packageName
        }
    }
}
