package com.truedigital.features.tuned.presentation.main.facade

import io.reactivex.Single

interface MusicAuthenticationFacade {
    fun getTrueUserId(): Int?
    fun loginJwt(trueUserId: Int, trueUserJwt: String): Single<Any>
    fun logout()
    fun isLogout(): Boolean
}
