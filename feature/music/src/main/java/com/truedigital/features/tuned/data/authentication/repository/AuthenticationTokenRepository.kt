package com.truedigital.features.tuned.data.authentication.repository

import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.user.model.Login
import io.reactivex.Single

interface AuthenticationTokenRepository {
    fun clearToken()
    fun getTokenByJwt(uniqueId: String, userJwt: String): Single<AuthenticationToken>
    fun getTokenByLogin(
        uniqueId: String,
        username: String,
        password: String
    ): Single<AuthenticationToken>

    fun get(uniqueId: String, refresh: Boolean = false): Single<Pair<AuthenticationToken, Boolean>>
    fun getCurrentToken(): AuthenticationToken?
    fun getUserIdFromToken(): Int?
    fun recreateToken(
        uniqueId: String,
        password: String,
        userLogins: List<Login>
    ): Single<AuthenticationToken>

    fun set(authToken: AuthenticationToken): Single<AuthenticationToken>
}
