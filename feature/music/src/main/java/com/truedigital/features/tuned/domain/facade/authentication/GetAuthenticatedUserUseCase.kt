package com.truedigital.features.tuned.domain.facade.authentication

import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.device.model.Device
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

interface GetAuthenticatedUserUseCase {
    fun execute(device: Device): Single<User>
}

class GetAuthenticatedUserUseCaseImpl @Inject constructor(
    private val musicUserRepository: MusicUserRepository,
    private val authenticationTokenRepository: AuthenticationTokenRepository
) : GetAuthenticatedUserUseCase {

    override fun execute(device: Device): Single<User> {
        return musicUserRepository.get()
            .flatMap { user ->
                checkAuthToken(
                    device.uniqueId,
                    device.token,
                    user
                ).map { user }
            }
            .flatMap { musicUserRepository.get(true) }
            .flatMap { user -> musicUserRepository.refreshSettings().map { user } }
    }

    private fun checkAuthToken(
        uniqueId: String,
        token: String,
        user: User
    ): Single<AuthenticationToken> =
        authenticationTokenRepository.get(uniqueId)
            .map {
                val authToken = it.first
                val tokenUserId = authToken.userId
                val userId = user.userId
                if (tokenUserId == userId) {
                    authToken
                } else {
                    Timber.d(token)
                    throw IllegalStateException("failed the userId does not match")
                }
            }
}
