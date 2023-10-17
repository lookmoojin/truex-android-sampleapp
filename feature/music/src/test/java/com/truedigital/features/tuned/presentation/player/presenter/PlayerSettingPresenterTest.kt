package com.truedigital.features.tuned.presentation.player.presenter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.presentation.player.facade.PlayerSettingFacade
import com.truedigital.features.utils.MockDataModel
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PlayerSettingPresenterTest {

    private val playerSettingFacade: PlayerSettingFacade = mock()
    private val view: PlayerSettingPresenter.ViewSurface = mock()
    private val router: PlayerSettingPresenter.RouterSurface = mock()
    private lateinit var presenter: PlayerSettingPresenter

    @BeforeEach
    fun setUp() {
        presenter = PlayerSettingPresenter(playerSettingFacade)
        presenter.onInject(view, router)
    }

    @Test
    fun onResume_getToggleHighQualityAudioSubscription_success_setHighQualityAudioAllowed() {
        val mockToggleHighQualityAudioSubscription: Disposable = mock()
        presenter.setPrivateData(
            toggleHighQualityAudioSubscription = mockToggleHighQualityAudioSubscription,
            toggleHighQualityAudioObservable = Single.just(MockDataModel.mockUser)
        )
        whenever(playerSettingFacade.loadMobileDataStreamingState()).thenReturn(false)
        whenever(playerSettingFacade.loadHighQualityAudioState()).thenReturn(Single.just(true))

        presenter.onResume()

        verify(view, times(1)).setHighQualityAudioAllowed(any())
        verify(view, times(1)).setMobileStreamingAllowed(any())
        verify(playerSettingFacade, times(1)).loadMobileDataStreamingState()
        verify(playerSettingFacade, times(1)).loadHighQualityAudioState()
    }

    @Test
    fun onResume_getToggleHighQualityAudioSubscription_fail_setHighQualityAudioAllowed() {
        val mockToggleHighQualityAudioSubscription: Disposable = mock()
        presenter.setPrivateData(
            toggleHighQualityAudioSubscription = mockToggleHighQualityAudioSubscription,
            toggleHighQualityAudioObservable = Single.error(Throwable("error"))
        )
        whenever(playerSettingFacade.loadMobileDataStreamingState()).thenReturn(false)
        whenever(playerSettingFacade.loadHighQualityAudioState()).thenReturn(Single.just(true))

        presenter.onResume()

        verify(view, times(0)).setHighQualityAudioAllowed(any())
        verify(view, times(1)).setMobileStreamingAllowed(any())
        verify(playerSettingFacade, times(1)).loadMobileDataStreamingState()
        verify(playerSettingFacade, times(1)).loadHighQualityAudioState()
    }

    @Test
    fun onPause_subscriptionIsNotNull_disposeSubscription() {
        val mockToggleHighQualityAudioSubscription: Disposable = mock()
        presenter.setPrivateData(toggleHighQualityAudioSubscription = mockToggleHighQualityAudioSubscription)

        presenter.onPause()

        verify(mockToggleHighQualityAudioSubscription, times(1)).dispose()
    }

    @Test
    fun onPause_subscriptionIsNull_doNothing() {
        val mockToggleHighQualityAudioSubscription: Disposable = mock()
        presenter.setPrivateData(toggleHighQualityAudioSubscription = null)

        presenter.onPause()

        verify(mockToggleHighQualityAudioSubscription, times(0)).dispose()
    }

    @Test
    fun onToggleMobileStreaming_toggleMobileDataStreamingState() {
        // When
        presenter.onToggleMobileStreaming(true)

        // Then
        verify(playerSettingFacade, times(1)).toggleMobileDataStreamingState(any())
    }

    @Test
    fun onToggleHighQualityAudio_toggleHighQualityAudioState_toggleHighQualityAudioStateIsCalled() {
        // Given
        whenever(playerSettingFacade.toggleHighQualityAudioState(any())).thenReturn(
            Single.just(MockDataModel.mockUser)
        )

        // When
        presenter.onToggleHighQualityAudio(true)

        // Then
        verify(playerSettingFacade, times(1)).toggleHighQualityAudioState(any())
    }
}
