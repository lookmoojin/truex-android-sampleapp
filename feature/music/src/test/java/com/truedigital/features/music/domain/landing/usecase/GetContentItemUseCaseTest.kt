package com.truedigital.features.music.domain.landing.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Data
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Shelf
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.music.constant.MusicShelfType
import com.truedigital.features.music.constant.MusicViewType
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GetContentItemUseCaseTest {
    private val mapRadioUseCase: MapRadioUseCase = mock()
    private val localizationRepository: LocalizationRepository = mock()
    private val musicLandingRepository: MusicLandingRepository = mock()
    private lateinit var getContentItemUseCase: GetContentItemUseCase

    @BeforeEach
    fun setUp() {
        getContentItemUseCase = GetContentItemUseCaseImpl(
            mapRadioUseCase = mapRadioUseCase,
            localizationRepository = localizationRepository,
            musicLandingRepository = musicLandingRepository
        )
    }

    @Test
    fun execute_viewTypeIsRadio_shelfListIsNotNull_returnRadioShelfList() = runTest {
        // Given
        val mockBaseShelfId = "baseShelfId"
        val mockData = Data().apply {
            setting = Setting().apply {
                viewType = MusicViewType.RADIO.value
            }
            shelfList = listOf(
                Shelf().apply {
                    id = "100"
                    contentType = "contentType"
                    setting = Setting().apply {
                        viewType = "viewType"
                    }
                }
            )
        }
        val mockRadioShelfItem = MusicForYouItemModel.RadioShelfItem(
            mediaAssetId = 100,
            index = 0,
            radioId = "100",
            contentType = "contentType",
            description = "description",
            thumbnail = "thumbnail",
            titleEn = "titleEn",
            titleTh = "titleTh",
            title = "titleTh",
            viewType = MusicViewType.RADIO.value,
            shelfType = MusicShelfType.GRID_2,
            streamUrl = "streamUrl"
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
        whenever(musicLandingRepository.getCmsShelfList(any(), any(), any(), any())).thenReturn(
            flowOf(mockData)
        )
        whenever(mapRadioUseCase.execute(any(), any(), any(), any(), any(), any())).thenReturn(
            mockRadioShelfItem
        )

        // When
        val data = getContentItemUseCase.execute(
            mockBaseShelfId,
            MusicShelfType.GRID_2
        )

        // Then
        data.collectSafe { item ->
            assertEquals(mockRadioShelfItem, item.first())
        }

        verify(musicLandingRepository, times(1)).getCmsShelfList(any(), any(), any(), any())
        verify(mapRadioUseCase, times(1)).execute(any(), any(), any(), any(), any(), any())
        verify(localizationRepository, times(1)).getAppCountryCode()
        verify(localizationRepository, times(1)).getAppLanguageCode()
    }

    @Test
    fun execute_viewTypeIsRadio_shelfListIsNull_returnEmptyList() = runTest {
        // Given
        val mockBaseShelfId = "baseShelfId"
        val mockData = Data().apply {
            setting = Setting().apply {
                viewType = MusicViewType.RADIO.value
            }
            shelfList = null
        }

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
        whenever(musicLandingRepository.getCmsShelfList(any(), any(), any(), any())).thenReturn(
            flowOf(mockData)
        )

        // When
        val data = getContentItemUseCase.execute(
            mockBaseShelfId,
            MusicShelfType.GRID_2
        )

        // Then
        data.collectSafe { item ->
            assertEquals(emptyList(), item)
        }

        verify(musicLandingRepository, times(1)).getCmsShelfList(any(), any(), any(), any())
        verify(mapRadioUseCase, times(0)).execute(any(), any(), any(), any(), any(), any())
        verify(localizationRepository, times(1)).getAppCountryCode()
        verify(localizationRepository, times(1)).getAppLanguageCode()
    }

    @Test
    fun execute_viewTypeIsNotRadio_returnEmptyList() = runTest {
        // Given
        val mockBaseShelfId = "baseShelfId"
        val mockData = Data().apply {
            setting = Setting().apply {
                viewType = "otherViewType"
            }
            shelfList = null
        }

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
        whenever(musicLandingRepository.getCmsShelfList(any(), any(), any(), any())).thenReturn(
            flowOf(mockData)
        )

        // When
        val data = getContentItemUseCase.execute(
            mockBaseShelfId,
            MusicShelfType.GRID_2
        )

        // Then
        data.collectSafe { item ->
            assertEquals(emptyList(), item)
        }

        verify(musicLandingRepository, times(1)).getCmsShelfList(any(), any(), any(), any())
        verify(mapRadioUseCase, times(0)).execute(any(), any(), any(), any(), any(), any())
        verify(localizationRepository, times(1)).getAppCountryCode()
        verify(localizationRepository, times(1)).getAppLanguageCode()
    }
}
