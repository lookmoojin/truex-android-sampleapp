package com.truedigital.features.tuned.domain.facade.lostnetwork

import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import javax.inject.Inject

class LossOfNetworkFacadeImpl @Inject constructor(
    private val authenticationTokenRepository: AuthenticationTokenRepository,
    private val musicUserRepository: MusicUserRepository
) : LossOfNetworkFacade {

    override fun hasOfflineRight(): Boolean =
        authenticationTokenRepository.getCurrentToken()?.hasCatalogueOfflineRight ?: false

    override fun isUserAllowedOffline() = musicUserRepository.getSettings()?.allowOffline ?: false
}
