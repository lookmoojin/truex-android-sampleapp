package com.truedigital.features.music.domain.logout.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.player.repository.CacheServicePlayerRepository
import com.truedigital.features.music.manager.player.MusicPlayerActionManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LogoutMusicUseCaseTest {

    private lateinit var logoutMusicUseCase: LogoutMusicUseCase
    private val cacheServicePlayerRepository: CacheServicePlayerRepository = mock()
    private val musicPlayerActionManager: MusicPlayerActionManager = mock()

    @BeforeEach
    fun setUp() {
        logoutMusicUseCase = LogoutMusicUseCaseImpl(
            cacheServicePlayerRepository,
            musicPlayerActionManager
        )
    }

    @Test
    fun testLogout_getServiceRunningTrue_verifyActionClose() {
        whenever(cacheServicePlayerRepository.getServiceRunning()).thenReturn(true)

        logoutMusicUseCase.execute()

        verify(musicPlayerActionManager, times(1)).actionClose()
    }

    @Test
    fun testLogout_getServiceRunningFalse_notVerifyActionClose() {
        whenever(cacheServicePlayerRepository.getServiceRunning()).thenReturn(false)

        logoutMusicUseCase.execute()

        verify(musicPlayerActionManager, times(0)).actionClose()
    }
}
