package com.truedigital.features.music.domain.landing.usecase

import com.google.gson.JsonParser
import com.truedigital.common.share.data.coredata.data.model.response.ContentDetailData
import com.truedigital.common.share.data.coredata.data.model.response.ContentDetailResponse
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.utils.MockDataModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class GetRadioUseCaseTest {

    @MockK
    private lateinit var localizationRepository: LocalizationRepository

    @MockK
    private lateinit var musicLandingRepository: MusicLandingRepository

    @MockK
    private lateinit var mapRadioUseCase: MapRadioUseCase

    private lateinit var getRadioUseCase: GetRadioUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        getRadioUseCase = GetRadioUseCaseImpl(
            localizationRepository = localizationRepository,
            musicLandingRepository = musicLandingRepository,
            mapRadioUseCase = mapRadioUseCase
        )
    }

    @Test
    fun execute_dataIsNotNull_settingIsNotnull_streamUrlIsNotNullOrEmpty_returnRadioShelfItem() =
        runTest {
            // Given
            val mockContentDetailData = ContentDetailData().apply {
                this.setting = JsonParser.parseString("{\"stream_url\":\"streamUrl\" }")
            }
            val mockContentDetailResponse = ContentDetailResponse().apply {
                this.data = mockContentDetailData
            }
            every {
                musicLandingRepository.getCmsContentDetails(any(), any(), any(), any())
            } returns flowOf(mockContentDetailResponse)
            every {
                mapRadioUseCase.execute(any(), any(), any(), any())
            } returns MockDataModel.mockRadioShelf
            every { localizationRepository.getAppCountryCode() } returns "th"
            every { localizationRepository.getAppLanguageCode() } returns "th"

            // When
            val result = getRadioUseCase.execute("id")

            // Then
            result.collectSafe {
                assertEquals(MockDataModel.mockRadioShelf, it)
            }
        }

    @Test
    fun execute_dataIsNotNull_settingIsNotnull_streamUrlIsEmpty_returnNull() = runTest {
        // Given
        val mockContentDetailData = ContentDetailData().apply {
            this.setting = JsonParser.parseString("{\"stream_url\":\"\" }")
        }
        val mockContentDetailResponse = ContentDetailResponse().apply {
            this.data = mockContentDetailData
        }
        every {
            musicLandingRepository.getCmsContentDetails(any(), any(), any(), any())
        } returns flowOf(mockContentDetailResponse)
        every { localizationRepository.getAppCountryCode() } returns "th"
        every { localizationRepository.getAppLanguageCode() } returns "th"

        // When
        val result = getRadioUseCase.execute("id")

        // Then
        result.collectSafe {
            assertNull(it)
        }
    }

    @Test
    fun execute_dataIsNotNull_settingIsNotnull_streamUrlIsNull_returnNull() = runTest {
        // Given
        val mockContentDetailData = ContentDetailData().apply {
            this.setting = JsonParser.parseString("{\"id\":\"\" }")
        }
        val mockContentDetailResponse = ContentDetailResponse().apply {
            this.data = mockContentDetailData
        }
        every {
            musicLandingRepository.getCmsContentDetails(any(), any(), any(), any())
        } returns flowOf(mockContentDetailResponse)
        every { localizationRepository.getAppCountryCode() } returns "th"
        every { localizationRepository.getAppLanguageCode() } returns "th"

        // When
        val result = getRadioUseCase.execute("id")

        // Then
        result.collectSafe {
            assertNull(it)
        }
    }

    @Test
    fun execute_dataIsNotNull_settingIsnull_returnNull() = runTest {
        // Given
        val mockContentDetailData = ContentDetailData().apply {
            this.setting = null
        }
        val mockContentDetailResponse = ContentDetailResponse().apply {
            this.data = mockContentDetailData
        }
        every {
            musicLandingRepository.getCmsContentDetails(any(), any(), any(), any())
        } returns flowOf(mockContentDetailResponse)
        every { localizationRepository.getAppCountryCode() } returns "th"
        every { localizationRepository.getAppLanguageCode() } returns "th"

        // When
        val result = getRadioUseCase.execute("id")

        // Then
        result.collectSafe {
            assertNull(it)
        }
    }

    @Test
    fun execute_dataIsNull_returnNull() = runTest {
        // Given
        val mockContentDetailResponse = ContentDetailResponse().apply {
            this.data = null
        }
        every {
            musicLandingRepository.getCmsContentDetails(any(), any(), any(), any())
        } returns flowOf(mockContentDetailResponse)
        every { localizationRepository.getAppCountryCode() } returns "th"
        every { localizationRepository.getAppLanguageCode() } returns "th"

        // When
        val result = getRadioUseCase.execute("id")

        // Then
        result.collectSafe {
            assertNull(it)
        }
    }
}
