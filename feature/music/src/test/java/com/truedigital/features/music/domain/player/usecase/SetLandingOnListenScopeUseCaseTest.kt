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

internal class SetLandingOnListenScopeUseCaseTest {

    @MockK
    lateinit var musicPlayerCacheRepository: MusicPlayerCacheRepository

    private lateinit var setLandingOnListenScopeUseCase: SetLandingOnListenScopeUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        setLandingOnListenScopeUseCase = SetLandingOnListenScopeUseCaseImpl(
            musicPlayerCacheRepository
        )
    }

    @Test
    fun execute_landingOnListenScopeIsSaved() {
        // Given
        every { setLandingOnListenScopeUseCase.execute(any()) } just runs

        // When
        setLandingOnListenScopeUseCase.execute(true)

        // Then
        verify(exactly = 1) { musicPlayerCacheRepository.setLandingOnListenScope(any()) }
    }
}
