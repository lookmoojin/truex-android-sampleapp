package com.truedigital.features.music.presentation.player

import android.app.Activity
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.domain.usecase.router.MusicPlayerRouterUseCase
import com.truedigital.features.music.navigation.router.MusicPlayerToPlayerQueue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class MusicPlayerNavigationViewModelTest {

    private val router: MusicPlayerRouterUseCase = mock()
    private val activity: Activity = mock()
    private lateinit var musicPlayerNavigationViewModel: MusicPlayerNavigationViewModel

    @BeforeEach
    fun setUp() {
        musicPlayerNavigationViewModel = MusicPlayerNavigationViewModel(router)
    }

    @Test
    fun navigateToPlayerQueue_routerIsCalled() {
        whenever(router.getLastDestination()).thenReturn(MusicPlayerToPlayerQueue)
        musicPlayerNavigationViewModel.navigateToPlayerQueue(1, 2, activity)

        verify(router, times(1)).execute(
            destination = any(),
            bundle = anyOrNull(),
            activity = any()
        )
        assertEquals(MusicPlayerToPlayerQueue, router.getLastDestination())
    }
}
