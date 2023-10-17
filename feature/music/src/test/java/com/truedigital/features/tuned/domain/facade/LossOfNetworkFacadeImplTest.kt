package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.ad.model.AdProvider
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.user.model.Settings
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.domain.facade.lostnetwork.LossOfNetworkFacade
import com.truedigital.features.tuned.domain.facade.lostnetwork.LossOfNetworkFacadeImpl
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LossOfNetworkFacadeImplTest {

    private lateinit var lossOfNetworkFacade: LossOfNetworkFacade
    private val authenticationTokenRepository: AuthenticationTokenRepository = mock()
    private val musicUserRepository: MusicUserRepository = mock()

    @BeforeEach
    fun setUp() {
        lossOfNetworkFacade =
            LossOfNetworkFacadeImpl(authenticationTokenRepository, musicUserRepository)
    }

    @Test
    fun testHasOfflineRight_hasCatalogueOfflineRightIsNull_returnFalse() {
        whenever(authenticationTokenRepository.getCurrentToken()).thenReturn(
            AuthenticationToken(refreshToken = "", expiration = 0L, accessToken = null)
        )

        assertFalse(lossOfNetworkFacade.hasOfflineRight())
    }

    @Test
    fun testHasOfflineRight_getCurrentTokenIsNull_returnFalse() {
        whenever(authenticationTokenRepository.getCurrentToken()).thenReturn(null)

        assertFalse(lossOfNetworkFacade.hasOfflineRight())
    }

    @Test
    fun testIsUserAllowedOffline_allowOfflineIsTrue_returnTrue() {
        whenever(musicUserRepository.getSettings()).thenReturn(
            Settings(
                allowStreams = false,
                limitSkips = false,
                adFirstMinutes = 1,
                adIntervalMinutes = 1,
                allowPurchase = true,
                allowAlbumNavigation = true,
                allowOffline = true,
                allowSync = true,
                syncCutOffDays = 1,
                maxSkipsPerHour = 1,
                limitPlays = true,
                monthlyPlayLimit = 1,
                adProvider = AdProvider.NONE,
                tracksPerAd = 1,
                interstitialId = "1",
                facebookUrl = "",
                twitterUrl = "",
                instagramUrl = "",
                youtubeUrl = "",
                supportEmail = "",
                dmcaEnabled = true,
                offlineMaximumDuration = 11L
            )
        )

        assertTrue(lossOfNetworkFacade.isUserAllowedOffline())
    }

    @Test
    fun testIsUserAllowedOffline_allowOfflineIsFalse_returnFalse() {
        whenever(musicUserRepository.getSettings()).thenReturn(
            Settings(
                allowStreams = false,
                limitSkips = false,
                adFirstMinutes = 1,
                adIntervalMinutes = 1,
                allowPurchase = true,
                allowAlbumNavigation = true,
                allowOffline = false,
                allowSync = true,
                syncCutOffDays = 1,
                maxSkipsPerHour = 1,
                limitPlays = true,
                monthlyPlayLimit = 1,
                adProvider = AdProvider.NONE,
                tracksPerAd = 1,
                interstitialId = "1",
                facebookUrl = "",
                twitterUrl = "",
                instagramUrl = "",
                youtubeUrl = "",
                supportEmail = "",
                dmcaEnabled = true,
                offlineMaximumDuration = 11L
            )
        )

        assertFalse(lossOfNetworkFacade.isUserAllowedOffline())
    }

    @Test
    fun testIsUserAllowedOffline_getSettingIsNull_returnFalse() {
        whenever(musicUserRepository.getSettings()).thenReturn(null)

        assertFalse(lossOfNetworkFacade.isUserAllowedOffline())
    }
}
