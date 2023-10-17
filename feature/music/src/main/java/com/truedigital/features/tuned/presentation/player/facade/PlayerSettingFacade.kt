package com.truedigital.features.tuned.presentation.player.facade

import com.truedigital.features.tuned.data.user.model.User
import io.reactivex.Single

interface PlayerSettingFacade {
    fun loadMobileDataStreamingState(): Boolean
    fun toggleMobileDataStreamingState(isAllowed: Boolean)

    fun loadHighQualityAudioState(): Single<Boolean>
    fun toggleHighQualityAudioState(isAllowed: Boolean): Single<User>
}
