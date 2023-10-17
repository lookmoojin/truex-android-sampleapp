package com.truedigital.features.tuned.domain.facade.authentication

import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.device.model.Device
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import io.reactivex.Single
import javax.inject.Inject

interface AuthenticationThirdPartyJWTUseCase {
    fun execute(device: Device, trueUserJwt: String): Single<User>
}

class AuthenticationThirdPartyJWTUseCaseImpl @Inject constructor(
    private val musicUserRepository: MusicUserRepository,
    private val authenticationTokenRepository: AuthenticationTokenRepository
) : AuthenticationThirdPartyJWTUseCase {

    override fun execute(device: Device, trueUserJwt: String): Single<User> {
        return authenticationTokenRepository.getTokenByJwt(device.uniqueId, trueUserJwt)
            .flatMap { authToken ->
                musicUserRepository.get(
                    authToken.userId ?: throw NullPointerException("failed to get userID")
                )
            }
            .flatMap { user ->
                musicUserRepository.refreshSettings().map { user }
            }
    }
}
