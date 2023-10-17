package com.truedigital.features.music.domain.forceloginbanner.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.forceloginbanner.model.LoginBannerItemConfigModel
import com.truedigital.features.music.data.forceloginbanner.repository.MusicConfigRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetLoginBannerUseCaseImplTest {
    private lateinit var getLoginBannerUseCase: GetLoginBannerUseCase
    private val musicConfigRepository: MusicConfigRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()

    private val imageTH = "image_path_th"
    private val imageEN = "image_path_en"
    private val mockLoginBannerConfig = LoginBannerItemConfigModel(
        imageTH = imageTH,
        imageEN = imageEN
    )

    @BeforeEach
    fun setup() {
        getLoginBannerUseCase = GetLoginBannerUseCaseImpl(
            musicConfigRepository,
            localizationRepository
        )
    }

    @Test
    fun testLoginBanner_null_returnError() = runTest {
        whenever(
            musicConfigRepository.getLoginBannerConfig()
        ).thenReturn(null)
        val response = getLoginBannerUseCase.execute()
        response.catch { exception ->
            assertNotNull(exception)
        }.collect()
    }

    @Test
    fun testLoginBanner_langTh_returnTh() = runTest {
        whenever(musicConfigRepository.getLoginBannerConfig()).thenReturn(
            flowOf(
                mockLoginBannerConfig
            )
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(localizationRepository.getAppLanguageCodeForEnTh()).thenReturn("th")

        getLoginBannerUseCase.execute()
            .collect {
                assertEquals(imageTH, it)
            }
    }

    @Test
    fun testLoginBanner_langEn_returnEn() = runTest {
        whenever(musicConfigRepository.getLoginBannerConfig()).thenReturn(
            flowOf(
                mockLoginBannerConfig
            )
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn("en")
        whenever(localizationRepository.getAppLanguageCodeForEnTh()).thenReturn("en")

        getLoginBannerUseCase.execute()
            .collect {
                assertEquals(imageEN, it)
            }
    }

    @Test
    fun testLoginBanner_langEmpty_returnEmpty() = runTest {
        whenever(musicConfigRepository.getLoginBannerConfig()).thenReturn(
            flowOf(
                mockLoginBannerConfig
            )
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn("")
        whenever(localizationRepository.getAppLanguageCodeForEnTh()).thenReturn("")

        getLoginBannerUseCase.execute()
            .collect {
                assertEquals("", it)
            }
    }

    @Test
    fun testLoginBanner_langEn_returnThNull() = runTest {
        whenever(musicConfigRepository.getLoginBannerConfig()).thenReturn(
            flowOf(
                null
            )
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(localizationRepository.getAppLanguageCodeForEnTh()).thenReturn("th")

        getLoginBannerUseCase.execute()
            .collect {
                assertEquals("", it)
            }
    }

    @Test
    fun testLoginBanner_langEn_returnEnNull() = runTest {
        whenever(musicConfigRepository.getLoginBannerConfig()).thenReturn(
            flowOf(
                null
            )
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn("en")
        whenever(localizationRepository.getAppLanguageCodeForEnTh()).thenReturn("en")

        getLoginBannerUseCase.execute()
            .collect {
                assertEquals("", it)
            }
    }
}
