package com.truedigital.features.tuned.presentation.player.presenter

import com.truedigital.features.tuned.domain.facade.playerqueue.PlayerQueueFacade
import com.truedigital.features.tuned.presentation.common.Presenter
import javax.inject.Inject

class PlayerQueuePresenter @Inject constructor(private val facade: PlayerQueueFacade) : Presenter {

    companion object {
        const val SHUFFLE_MODE_KEY = "shuffle_mode_key"
        const val REPEAT_MODE_KEY = "repeat_mode_key"
    }

    private lateinit var view: ViewSurface
    private lateinit var router: RouterSurface

    private var lastPlayingTrackId: Int? = null
    private var lastPlayingTrackIndex: Int? = null

    fun onInject(view: ViewSurface, router: RouterSurface) {
        this.view = view
        this.router = router
    }

    fun onUpdatePlaybackState(
        trackId: Int?,
        indexInQueue: Int?
    ) {
        if (trackId != lastPlayingTrackId || indexInQueue != lastPlayingTrackIndex) {
            view.showCurrentPlayingTrack(trackId, indexInQueue)
            lastPlayingTrackId = trackId
            lastPlayingTrackIndex = indexInQueue
        }
    }

    fun onItemRemoved(index: Int) {
        view.showItemRemoved(index)
    }

    fun onItemMoved(oldIndex: Int, newIndex: Int) {
        view.showItemMoved(oldIndex, newIndex)
    }

    fun onRepeatSelected() {
        view.toggleRepeat()
    }

    fun onShuffleSelected() {
        if (facade.isShuffleEnabled() && !facade.hasSequentialPlaybackRight()) {
            view.showUpgradeDialog()
        } else {
            view.toggleShuffle()
        }
    }

    fun onClearSelected() {
        view.showClearQueueDialog()
    }

    fun getHasPlaylistWriteRight() = facade.hasPlaylistWriteRight()

    interface ViewSurface {
        fun showCurrentPlayingTrack(trackId: Int?, index: Int?)
        fun showItemMoved(oldIndex: Int, newIndex: Int)
        fun showItemRemoved(index: Int)
        fun toggleRepeat()
        fun toggleShuffle()
        fun showClearQueueDialog()
        fun clearQueue()
        fun showUpgradeDialog()
    }

    interface RouterSurface
}
