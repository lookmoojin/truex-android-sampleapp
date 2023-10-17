package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.setting.repository.SettingRepository
import com.truedigital.features.tuned.domain.facade.playerqueue.PlayerQueueFacade
import com.truedigital.features.tuned.domain.facade.playerqueue.PlayerQueueFacadeImpl
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerQueueFacadeImplTest {

    private lateinit var playerQueueFacade: PlayerQueueFacade
    private val settingRepository: SettingRepository = mock()
    private val authenticationTokenRepository: AuthenticationTokenRepository = mock()

    @BeforeEach
    fun setUp() {
        playerQueueFacade = PlayerQueueFacadeImpl(settingRepository, authenticationTokenRepository)
    }

    @Test
    fun loadIsShuffleEnabled_isShufflePlayEnabledIsTrue_returnTrue() {
        whenever(settingRepository.isShufflePlayEnabled()).thenReturn(true)

        assertTrue(playerQueueFacade.isShuffleEnabled())

        verify(settingRepository, times(1)).isShufflePlayEnabled()
    }

    @Test
    fun loadIsShuffleEnabled_isShufflePlayEnabledIsFalse_returnFalse() {
        whenever(settingRepository.isShufflePlayEnabled()).thenReturn(false)

        assertFalse(playerQueueFacade.isShuffleEnabled())

        verify(settingRepository, times(1)).isShufflePlayEnabled()
    }

    @Test
    fun testHasSequentialPlaybackRight_hasSequentialPlaybackRightIsFalse_returnFalse() {
        val mock =
            AuthenticationToken(refreshToken = "refreshToken", expiration = 1L, accessToken = null)
        whenever(authenticationTokenRepository.getCurrentToken()).thenReturn(mock)

        val test = playerQueueFacade.hasSequentialPlaybackRight()

        assertFalse(test)
    }

    @Test
    fun testHasSequentialPlaybackRight_getCurrentTokenIsNull_returnFalse() {
        whenever(authenticationTokenRepository.getCurrentToken()).thenReturn(null)

        val test = playerQueueFacade.hasSequentialPlaybackRight()

        assertFalse(test)
    }

    @Test
    fun testHasPlaylistWriteRight_hasPlaylistWriteRightIsFalse_returnFalse() {
        val mock =
            AuthenticationToken(refreshToken = "refreshToken", expiration = 1L, accessToken = null)
        whenever(authenticationTokenRepository.getCurrentToken()).thenReturn(mock)

        val test = playerQueueFacade.hasPlaylistWriteRight()

        assertFalse(test)
    }

    @Test
    fun testHasPlaylistWriteRight_getCurrentTokenIsNull_returnFalse() {
        whenever(authenticationTokenRepository.getCurrentToken()).thenReturn(null)

        val test = playerQueueFacade.hasPlaylistWriteRight()

        assertFalse(test)
    }
}
