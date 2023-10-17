package com.truedigital.features.music.domain.player.usecase

import com.truedigital.features.music.data.player.repository.MusicPlayerCacheRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SetMusicPlayerVisibleUseCaseTest {

    @MockK
    lateinit var musicPlayerCacheRepository: MusicPlayerCacheRepository

    private lateinit var setMusicPlayerVisibleUseCase: SetMusicPlayerVisibleUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        setMusicPlayerVisibleUseCase = SetMusicPlayerVisibleUseCaseImpl(
            musicPlayerCacheRepository = musicPlayerCacheRepository
        )
    }

    @Test
    fun execute_musicPlayerVisibleIsSet() {
        // Given
        every { setMusicPlayerVisibleUseCase.execute(any()) } just runs

        // When
        setMusicPlayerVisibleUseCase.execute(true)

        // Then
        verify(exactly = 1) { musicPlayerCacheRepository.setMusicPlayerVisible(any()) }
    }
}
