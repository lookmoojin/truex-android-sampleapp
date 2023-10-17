package com.truedigital.features.music.presentation.player

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.truedigital.features.music.domain.usecase.router.MusicPlayerRouterUseCase
import com.truedigital.features.music.navigation.router.MusicPlayerToPlayerQueue
import com.truedigital.features.tuned.presentation.player.presenter.PlayerQueuePresenter
import javax.inject.Inject

class MusicPlayerNavigationViewModel @Inject constructor(
    private val router: MusicPlayerRouterUseCase
) : ViewModel() {

    fun navigateToPlayerQueue(repeatMode: Int, shuffleMode: Int, activity: Activity) {
        router.execute(
            MusicPlayerToPlayerQueue,
            Bundle().apply {
                putInt(PlayerQueuePresenter.REPEAT_MODE_KEY, repeatMode)
                putInt(PlayerQueuePresenter.SHUFFLE_MODE_KEY, shuffleMode)
            },
            activity
        )
    }
}
