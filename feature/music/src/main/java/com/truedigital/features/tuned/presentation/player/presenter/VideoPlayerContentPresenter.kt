package com.truedigital.features.tuned.presentation.player.presenter

import com.truedigital.features.tuned.presentation.common.Presenter
import javax.inject.Inject

class VideoPlayerContentPresenter @Inject constructor() : Presenter {

    private lateinit var view: ViewSurface

    override fun onPause() {
        view.interruptPlayer()
    }

    override fun onResume() {
        view.resumeIfInterrupted()
    }

    fun onInject(view: ViewSurface) {
        this.view = view
    }

    interface ViewSurface {
        fun interruptPlayer()
        fun resumeIfInterrupted()
    }
}
