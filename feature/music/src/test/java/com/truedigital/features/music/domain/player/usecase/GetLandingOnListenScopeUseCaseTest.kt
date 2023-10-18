package com.truedigital.features.music.domain.player.usecase

import com.truedigital.features.music.data.player.repository.MusicPlayerCacheRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class GetLandingOnListenScopeUseCaseTest {

    @MockK
    lateinit var musicPlayerCacheRepository: MusicPlayerCacheRepository

    private lateinit var getLandingOnListenScopeUseCase: GetLandingOnListenScopeUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        getLandingOnListenScopeUseCase = GetLandingOnListenScopeUseCaseImpl(
            musicPlayerCacheRepository
        )
    }

    @Test
    fun execute_returnLandingOnListenScopeFromCache() {
        // Given
        every { musicPlayerCacheRepository.getLandingOnListenScope() } returns true

        // When
        val result = getLandingOnListenScopeUseCase.execute()

        // Then
        assertTrue(result)
        verify(exactly = 1) { musicPlayerCacheRepository.getLandingOnListenScope() }
    }
}
