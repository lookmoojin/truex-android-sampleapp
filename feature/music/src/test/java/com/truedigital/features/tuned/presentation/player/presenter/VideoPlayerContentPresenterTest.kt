package com.truedigital.features.tuned.presentation.player.presenter

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class VideoPlayerContentPresenterTest {

    private val view: VideoPlayerContentPresenter.ViewSurface = mock()
    private lateinit var presenter: VideoPlayerContentPresenter

    @BeforeEach
    fun setUp() {
        presenter = VideoPlayerContentPresenter()
        presenter.onInject(view)
    }

    @Test
    fun onPause_interruptPlayer() {
        presenter.onPause()
        verify(view, times(1)).interruptPlayer()
    }

    @Test
    fun onResume_resumeIfInterrupted() {
        presenter.onResume()
        verify(view, times(1)).resumeIfInterrupted()
    }
}
