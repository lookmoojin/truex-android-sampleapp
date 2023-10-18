package com.truedigital.features.music.domain.ads.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.music.data.forceloginbanner.model.AdsBannerPlayerModel
import com.truedigital.features.music.data.forceloginbanner.model.MusicEnableModel
import com.truedigital.features.music.data.forceloginbanner.repository.MusicConfigRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

interface GetMusicPlayerAdsUrlUseCaseTestCase {
    fun execute_adsBannerPlayerNull_returnEmptyString()
    fun execute_enableFalse_returnEmptyString()
    fun execute_enableTrue_returnAdsUrl()
}

class GetMusicPlayerAdsUrlUseCaseTest : GetMusicPlayerAdsUrlUseCaseTestCase {

    lateinit var useCase: GetMusicPlayerAdsUrlUseCase

    @MockK
    lateinit var musicConfigRepository: MusicConfigRepository

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        useCase = GetMusicPlayerAdsUrlUseCaseImpl(musicConfigRepository)
    }

    @Test
    override fun execute_adsBannerPlayerNull_returnEmptyString() = runTest {
        coEvery { musicConfigRepository.getAdsBannerPlayerConfig() } returns flowOf(null)
        useCase.execute()
            .collectSafe { result ->
                assertEquals("", result)
            }
    }

    @Test
    override fun execute_enableFalse_returnEmptyString() = runTest {
        coEvery { musicConfigRepository.getAdsBannerPlayerConfig() } returns flowOf(
            AdsBannerPlayerModel(
                enable = MusicEnableModel(),
                urlAds = "url"
            )
        )
        useCase.execute()
            .collectSafe { result ->
                assertEquals("", result)
            }
    }

    @Test
    override fun execute_enableTrue_returnAdsUrl() = runTest {
        coEvery { musicConfigRepository.getAdsBannerPlayerConfig() } returns flowOf(
            AdsBannerPlayerModel(
                enable = MusicEnableModel(
                    android = true
                ),
                urlAds = "url"
            )
        )
        useCase.execute()
            .collectSafe { result ->
                assertEquals("url", result)
            }
    }
}
