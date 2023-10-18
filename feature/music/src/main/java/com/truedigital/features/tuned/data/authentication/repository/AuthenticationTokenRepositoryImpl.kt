package com.truedigital.features.tuned.data.authentication.repository

import com.newrelic.agent.android.NewRelic
import com.truedigital.authentication.presentation.AccessTokenListener
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.features.tuned.api.authentication.AuthenticationTokenApiInterface
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.model.response.AccessToken
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.put
import com.truedigital.features.tuned.data.user.model.Login
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.HttpException
import kotlin.coroutines.resume

class AuthenticationTokenRepositoryImpl(
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface,
    private val authenticationTokenApi: AuthenticationTokenApiInterface,
    private val config: Configuration,
    private val authManagerWrapper: AuthManagerWrapper
) : AuthenticationTokenRepository {

    companion object {
        const val AUTH_TOKEN_KEY = "authentication_token"
        const val SECONDS_TO_MILLISECONDS = 1_000
        private const val HTTP_CODE_SUCCESS_MIN = 400
        private const val HTTP_CODE_SUCCESS_MAX = 499
        private const val GRANT_TYPE_PASSWORD = "password"
        private const val GRANT_TYPE_REFRESH_TOKEN = "refresh_token"

        private enum class PerformAction {
            JWT, TOKEN
        }
    }

    private var refreshAccessTokenRequest: Single<Pair<AuthenticationToken, Boolean>>? = null

    override fun getTokenByJwt(uniqueId: String, userJwt: String): Single<AuthenticationToken> =
        performAuthorisation(PerformAction.JWT) {
            authenticationTokenApi.getTokenByJwt(
                it,
                "Bearer $userJwt",
                uniqueId,
                config.applicationId
            )
        }

    override fun getTokenByLogin(
        uniqueId: String,
        username: String,
        password: String
    ): Single<AuthenticationToken> =
        performAuthorisation(PerformAction.TOKEN) {
            authenticationTokenApi.getTokenByLogin(
                it, GRANT_TYPE_PASSWORD, username, password,
                uniqueId, config.applicationId
            )
        }

    /**
     * If we have a (valid) access token, return that
     * If we have a refresh token from a previous session &&
     * current access token expires, use that to get a new access token
     * Else return error
     *
     * The Boolean tells whether we have refreshed the token or not
     */
    override fun get(
        uniqueId: String,
        refresh: Boolean
    ): Single<Pair<AuthenticationToken, Boolean>> =
        sharedPreferences.get<AuthenticationToken>(AUTH_TOKEN_KEY)?.let { token ->
            if (token.accessToken != null && System.currentTimeMillis() < token.expiration && !refresh) {
                Single.just(Pair(token, false))
            } else {
                refreshAccessTokenRequest = refreshAccessTokenRequest
                    ?: performAuthorisation(PerformAction.TOKEN) {
                        authenticationTokenApi.refreshToken(
                            it, GRANT_TYPE_REFRESH_TOKEN,
                            token.refreshToken
                        )
                    }
                        .map {
                            if (it.expiration > System.currentTimeMillis())
                                it
                            else
                                throw IllegalStateException("Updated access token has already expired")
                        }
                        .map { Pair(it, true) }
                        .cache().doFinally {
                            refreshAccessTokenRequest = null
                        }
                refreshAccessTokenRequest
            }
        } ?: Single.error(IllegalStateException("auth token does not exist"))

    override fun getCurrentToken(): AuthenticationToken? {
        return sharedPreferences.get(AUTH_TOKEN_KEY)
    }

    /**
     * Recreate token if device or msisdn user
     * other Login type will return error directly
     */
    override fun recreateToken(
        uniqueId: String,
        password: String,
        userLogins: List<Login>
    ): Single<AuthenticationToken> = runBlocking {
        val token = getTokenVerify()
        return@runBlocking getTokenByJwt(uniqueId, token)
            .doOnError {
                if (it is HttpException && it.code() in (HTTP_CODE_SUCCESS_MIN..HTTP_CODE_SUCCESS_MAX)) {
                    sharedPreferences.remove(AUTH_TOKEN_KEY)
                }
            }
    }

    private suspend fun getTokenVerify() =
        suspendCancellableCoroutine<String> { suspendCancellableCoroutine ->
            authManagerWrapper.getAccessToken(object : AccessTokenListener {
                override fun onSuccess(accessToken: String) {
                    suspendCancellableCoroutine.resume(accessToken)
                }

                override fun onError(message: String?) {
                    suspendCancellableCoroutine.resume("")
                }
            })
        }

    override fun set(authToken: AuthenticationToken): Single<AuthenticationToken> {
        sharedPreferences.put(AUTH_TOKEN_KEY, authToken)
        return Single.just(authToken)
    }

    override fun clearToken() {
        sharedPreferences.remove(AUTH_TOKEN_KEY)
    }

    override fun getUserIdFromToken(): Int? {
        return sharedPreferences.get<AuthenticationToken>(AUTH_TOKEN_KEY)?.userId
    }

    // call auth method to retrieve a token and generate a AuthenticationToken object
    private fun performAuthorisation(
        performAction: PerformAction,
        authMethod: (grantURL: String) -> Single<AccessToken>
    ) =
        getGrantUrl(performAction).flatMap {
            authMethod(it)
        }.map {
            AuthenticationToken(
                it.refreshToken,
                System.currentTimeMillis() + (it.expiresIn * SECONDS_TO_MILLISECONDS),
                it.accessToken
            )
        }.doOnSuccess { token ->
            sharedPreferences.put(AUTH_TOKEN_KEY, token)
        }.doOnError {
            val handlingExceptionMap = mapOf(
                "Key" to "musicperformAuthorisation",
                "Value" to "Response error code ${it.message}"
            )
            NewRelic.recordHandledException(Exception(it.localizedMessage), handlingExceptionMap)
        }

    private fun getGrantUrl(performAction: PerformAction): Single<String> {
        return when (performAction) {
            PerformAction.TOKEN -> Single.just(config.authUrl + "oauth2/token")
            PerformAction.JWT -> Single.just(config.servicesUrl + "users/authenticateThirdPartyJWT")
        }
    }
}
