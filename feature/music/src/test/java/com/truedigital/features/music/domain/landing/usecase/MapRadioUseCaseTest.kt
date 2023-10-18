package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.constant.MusicShelfType
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MapRadioUseCaseTest {

    @MockK
    private lateinit var getRadioMediaAssetIdUseCase: GetRadioMediaAssetIdUseCase

    @MockK
    private lateinit var localizationRepository: LocalizationRepository

    private lateinit var mapRadioUseCase: MapRadioUseCase

    private val mockSetting = Setting().apply {
        this.descriptionEn = "descriptionEn"
        this.descriptionTh = "descriptionTh"
        this.titleTh = "titleTh"
        this.titleEn = "titleEn"
        this.thumbnail = "thumbnail"
        this.streamUrl = "streamUrl"
    }
    private val mockMediaAssetId = 100
    private val mockId = "id"
    private val mockViewType = "viewType"
    private val mockContentType = "contentType"
    private val mockIndex = 10

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mapRadioUseCase = MapRadioUseCaseImpl(
            getRadioMediaAssetIdUseCase = getRadioMediaAssetIdUseCase,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun execute_settingIsNotNull_viewTypeIsNotNull_returnRadioData() {
        // Given
        every { getRadioMediaAssetIdUseCase.execute(any()) } returns mockMediaAssetId
        every { localizationRepository.getAppLanguageCode() } returns LocalizationRepository.Localization.TH.languageCode
        every { localizationRepository.getAppCountryCode() } returns LocalizationRepository.Localization.TH.countryCode

        // When
        val result = mapRadioUseCase.execute(
            id = mockId,
            index = mockIndex,
            setting = mockSetting,
            viewType = mockViewType,
            contentType = mockContentType
        )

        // Then
        assertEquals(mockMediaAssetId, result.mediaAssetId)
        assertEquals(mockIndex, result.index)
        assertEquals(mockId, result.radioId)
        assertEquals(mockSetting.descriptionTh, result.description)
        assertEquals(mockSetting.titleTh, result.titleTh)
        assertEquals(mockSetting.titleEn, result.titleEn)
        assertEquals(mockSetting.thumbnail, result.thumbnail)
        assertEquals(mockViewType, result.viewType)
        assertEquals(mockSetting.streamUrl, result.streamUrl)
        assertEquals(mockContentType, result.contentType)
    }

    @Test
    fun execute_settingIsNull_viewTypeIsNull_returnRadioData() {
        // Given
        every { getRadioMediaAssetIdUseCase.execute(any()) } returns mockMediaAssetId
        every { localizationRepository.getAppLanguageCode() } returns LocalizationRepository.Localization.TH.languageCode
        every { localizationRepository.getAppCountryCode() } returns LocalizationRepository.Localization.TH.countryCode

        // When
        val result = mapRadioUseCase.execute(
            id = mockId,
            index = mockIndex,
            setting = null,
            viewType = null,
            shelfType = null
        )

        // Then
        assertEquals(mockMediaAssetId, result.mediaAssetId)
        assertEquals(mockIndex, result.index)
        assertEquals(mockId, result.radioId)
        assertEquals(result.shelfType, MusicShelfType.HORIZONTAL)
        assertTrue(result.description.isEmpty())
        assertTrue(result.titleTh.isEmpty())
        assertTrue(result.titleEn.isEmpty())
        assertTrue(result.thumbnail.isEmpty())
        assertTrue(result.viewType.isEmpty())
        assertTrue(result.streamUrl.isEmpty())
        assertTrue(result.contentType.isEmpty())
    }
}
