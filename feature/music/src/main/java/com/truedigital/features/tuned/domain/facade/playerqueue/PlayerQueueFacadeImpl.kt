package com.truedigital.features.tuned.domain.facade.playerqueue

import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.setting.repository.SettingRepository
import javax.inject.Inject

class PlayerQueueFacadeImpl @Inject constructor(
    private val settingRepository: SettingRepository,
    private val authenticationTokenRepository: AuthenticationTokenRepository
) : PlayerQueueFacade {

    override fun isShuffleEnabled(): Boolean = settingRepository.isShufflePlayEnabled()

    override fun hasSequentialPlaybackRight(): Boolean =
        authenticationTokenRepository.getCurrentToken()?.hasSequentialPlaybackRight ?: false

    override fun hasPlaylistWriteRight() =
        authenticationTokenRepository.getCurrentToken()?.hasPlaylistWriteRight ?: false
}
