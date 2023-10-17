package com.truedigital.features.music.presentation.landing

import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
internal class MusicLandingActionViewModelTest {

    private lateinit var musicLandingActionViewModel: MusicLandingActionViewModel

    @BeforeEach
    fun setUp() {
        musicLandingActionViewModel = MusicLandingActionViewModel()
    }

    @Test
    fun setActiveTopNav_activeTopNavBySlugIsCalled() {
        // Given
        val mockSlug = "slug"

        // When
        musicLandingActionViewModel.setActiveTopNav(mockSlug)

        // Then
        assertEquals(mockSlug, musicLandingActionViewModel.onActiveTopNavBySlug().getOrAwaitValue())
    }
}
