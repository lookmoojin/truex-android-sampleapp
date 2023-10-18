package com.truedigital.features.tuned.presentation.player.presenter

import androidx.annotation.VisibleForTesting
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.successSubscribe
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.presentation.common.Presenter
import com.truedigital.features.tuned.presentation.player.facade.PlayerSettingFacade
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class PlayerSettingPresenter @Inject constructor(
    private val playerSettingFacade: PlayerSettingFacade
) : Presenter {

    private lateinit var view: ViewSurface
    private lateinit var router: RouterSurface

    private var toggleHighQualityAudioObservable: Single<User>? = null
    private var toggleHighQualityAudioSubscription: Disposable? = null

    fun onInject(view: ViewSurface, router: RouterSurface) {
        this.view = view
        this.router = router
    }

    override fun onResume() {
        super.onResume()
        view.setMobileStreamingAllowed(playerSettingFacade.loadMobileDataStreamingState())
        playerSettingFacade.loadHighQualityAudioState().cacheOnMainThread()
            .successSubscribe { view.setHighQualityAudioAllowed(it) }
        toggleHighQualityAudioSubscription = getToggleHighQualityAudioSubscription()
    }

    override fun onPause() {
        super.onPause()
        toggleHighQualityAudioSubscription?.dispose()
    }

    fun onToggleMobileStreaming(enabled: Boolean) {
        playerSettingFacade.toggleMobileDataStreamingState(enabled)
    }

    fun onToggleHighQualityAudio(enabled: Boolean) {
        toggleHighQualityAudioObservable = getToggleHighQualityAudioObservable(enabled)
        toggleHighQualityAudioSubscription = getToggleHighQualityAudioSubscription()
    }

    private fun getToggleHighQualityAudioObservable(enabled: Boolean) =
        playerSettingFacade.toggleHighQualityAudioState(enabled).cacheOnMainThread()

    private fun getToggleHighQualityAudioSubscription() = toggleHighQualityAudioObservable
        ?.tunedSubscribe(
            {
                toggleHighQualityAudioObservable = null
                view.setHighQualityAudioAllowed(it.audioQuality == "high")
            },
            {
                toggleHighQualityAudioObservable = null
            }
        )

    @VisibleForTesting
    fun setPrivateData(
        toggleHighQualityAudioSubscription: Disposable? = null,
        toggleHighQualityAudioObservable: Single<User>? = null
    ) {
        this.toggleHighQualityAudioSubscription = toggleHighQualityAudioSubscription
        this.toggleHighQualityAudioObservable = toggleHighQualityAudioObservable
    }

    interface ViewSurface {
        fun setMobileStreamingAllowed(isAllowed: Boolean)
        fun setHighQualityAudioAllowed(isAllowed: Boolean)
    }

    interface RouterSurface
}
