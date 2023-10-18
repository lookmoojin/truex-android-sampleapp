package com.truedigital.features.music.domain.player.usecase

import com.truedigital.features.music.data.player.repository.MusicPlayerCacheRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetMusicPlayerVisibleUseCaseTest {

    @MockK
    lateinit var musicPlayerCacheRepository: MusicPlayerCacheRepository

    private lateinit var getMusicPlayerVisibleUseCase: GetMusicPlayerVisibleUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        getMusicPlayerVisibleUseCase = GetMusicPlayerVisibleUseCaseImpl(
            musicPlayerCacheRepository = musicPlayerCacheRepository
        )
    }

    @Test
    fun execute_returnMusicPlayerVisible() {
        // Given
        every { getMusicPlayerVisibleUseCase.execute() } returns true

        // When
        val result = getMusicPlayerVisibleUseCase.execute()

        // Then
        assertTrue(result)
        verify(exactly = 1) { musicPlayerCacheRepository.getMusicPlayerVisible() }
    }
}
