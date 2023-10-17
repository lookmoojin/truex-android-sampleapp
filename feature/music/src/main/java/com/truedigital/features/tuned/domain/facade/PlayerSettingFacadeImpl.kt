package com.truedigital.features.tuned.domain.facade

import com.truedigital.features.tuned.data.setting.repository.SettingRepository
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.presentation.player.facade.PlayerSettingFacade
import io.reactivex.Single
import javax.inject.Inject

class PlayerSettingFacadeImpl @Inject constructor(
    private val musicUserRepository: MusicUserRepository,
    private val settingRepository: SettingRepository
) : PlayerSettingFacade {

    override fun loadMobileDataStreamingState(): Boolean =
        settingRepository.allowMobileDataStreaming()

    override fun toggleMobileDataStreamingState(isAllowed: Boolean) {
        settingRepository.setAllowMobileDataStreaming(isAllowed)
    }

    override fun loadHighQualityAudioState(): Single<Boolean> =
        musicUserRepository.get().map { it.audioQuality == "high" }

    override fun toggleHighQualityAudioState(isAllowed: Boolean): Single<User> =
        musicUserRepository.update(audioQuality = if (isAllowed) "high" else "low")
}
