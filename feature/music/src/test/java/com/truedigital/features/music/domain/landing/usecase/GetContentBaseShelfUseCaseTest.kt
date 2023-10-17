package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Data
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Shelf
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.constant.MusicShelfType
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class GetContentBaseShelfUseCaseTest {

    @MockK
    lateinit var localizationRepository: LocalizationRepository

    @MockK
    lateinit var musicLandingRepository: MusicLandingRepository

    private lateinit var getContentBaseShelfUseCase: GetContentBaseShelfUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        getContentBaseShelfUseCase = GetContentBaseShelfUseCaseImpl(
            localizationRepository = localizationRepository,
            musicLandingRepository = musicLandingRepository
        )
    }

    @Test
    fun execute_success_responseDataIsNull_returnEmptyList() = runTest {
        // Given
        every { localizationRepository.getAppCountryCode() } returns LocalizationRepository.Localization.TH.countryCode
        every { localizationRepository.getAppLanguageCode() } returns LocalizationRepository.Localization.TH.languageCode
        every { musicLandingRepository.getCmsShelfList(any(), any(), any(), any()) } returns
            flowOf(null)

        // When
        val result = getContentBaseShelfUseCase.execute("baseShelfId")

        // Then
        result.collect {
            assertTrue(it.isEmpty())
        }
        verify(exactly = 1) { musicLandingRepository.getCmsShelfList(any(), any(), any(), any()) }
    }

    @Test
    fun execute_success_responseDataIsNotNull_returnEmptyList() = runTest {
        // Given
        val mockData = Data().apply {
            this.shelfList = null
        }
        every { localizationRepository.getAppCountryCode() } returns LocalizationRepository.Localization.TH.countryCode
        every { localizationRepository.getAppLanguageCode() } returns LocalizationRepository.Localization.TH.languageCode
        every { musicLandingRepository.getCmsShelfList(any(), any(), any(), any()) } returns
            flowOf(mockData)

        // When
        val result = getContentBaseShelfUseCase.execute("baseShelfId")

        // Then
        result.collect {
            assertTrue(it.isEmpty())
        }
        verify(exactly = 1) { musicLandingRepository.getCmsShelfList(any(), any(), any(), any()) }
    }

    @Test
    fun execute_success_responseDataIsNotNull_settingIsNull_returnEmptyList() = runTest {
        // Given
        val mockShelf = Shelf().apply {
            this.setting = null
        }
        val mockData = Data().apply {
            this.shelfList = listOf(mockShelf)
        }
        every { localizationRepository.getAppCountryCode() } returns LocalizationRepository.Localization.TH.countryCode
        every { localizationRepository.getAppLanguageCode() } returns LocalizationRepository.Localization.TH.languageCode
        every { musicLandingRepository.getCmsShelfList(any(), any(), any(), any()) } returns
            flowOf(mockData)

        // When
        val result = getContentBaseShelfUseCase.execute("baseShelfId")

        // Then
        result.collect {
            assertTrue(it.isEmpty())
        }
        verify(exactly = 1) { musicLandingRepository.getCmsShelfList(any(), any(), any(), any()) }
    }

    @Test
    fun execute_success_responseDataIsNotNull_shelfCodeIsNull_returnEmptyList() = runTest {
        // Given
        val mockSetting = Setting().apply {
            this.shelfCode = null
        }
        val mockShelf = Shelf().apply {
            this.setting = mockSetting
        }
        val mockData = Data().apply {
            this.shelfList = listOf(mockShelf)
        }
        every { localizationRepository.getAppCountryCode() } returns LocalizationRepository.Localization.TH.countryCode
        every { localizationRepository.getAppLanguageCode() } returns LocalizationRepository.Localization.TH.languageCode
        every { musicLandingRepository.getCmsShelfList(any(), any(), any(), any()) } returns
            flowOf(mockData)

        // When
        val result = getContentBaseShelfUseCase.execute("baseShelfId")

        // Then
        result.collect {
            assertTrue(it.isEmpty())
        }
        verify(exactly = 1) { musicLandingRepository.getCmsShelfList(any(), any(), any(), any()) }
    }

    @Test
    fun execute_success_responseDataIsNotNull_shelfCodeIsEmpty_returnEmptyList() = runTest {
        // Given
        val mockSetting = Setting().apply {
            this.shelfCode = ""
        }
        val mockShelf = Shelf().apply {
            this.setting = mockSetting
        }
        val mockData = Data().apply {
            this.shelfList = listOf(mockShelf)
        }
        every { localizationRepository.getAppCountryCode() } returns LocalizationRepository.Localization.TH.countryCode
        every { localizationRepository.getAppLanguageCode() } returns LocalizationRepository.Localization.TH.languageCode
        every { musicLandingRepository.getCmsShelfList(any(), any(), any(), any()) } returns
            flowOf(mockData)

        // When
        val result = getContentBaseShelfUseCase.execute("baseShelfId")

        // Then
        result.collect {
            assertTrue(it.isEmpty())
        }
        verify(exactly = 1) { musicLandingRepository.getCmsShelfList(any(), any(), any(), any()) }
    }

    @Test
    fun execute_success_responseDataIsNotNull_shelfCodeIsNotNullOrEmptyEmpty_viewTypeIsNotContainsFilter_returnEmptyList() =
        runTest {
            // Given
            val mockSetting = Setting().apply {
                this.shelfCode = "shelfCode"
                this.viewType = "viewType"
            }
            val mockShelf = Shelf().apply {
                this.setting = mockSetting
            }
            val mockData = Data().apply {
                this.shelfList = listOf(mockShelf)
            }
            every { localizationRepository.getAppCountryCode() } returns LocalizationRepository.Localization.TH.countryCode
            every { localizationRepository.getAppLanguageCode() } returns LocalizationRepository.Localization.TH.languageCode
            every { musicLandingRepository.getCmsShelfList(any(), any(), any(), any()) } returns
                flowOf(mockData)

            // When
            val result = getContentBaseShelfUseCase.execute("baseShelfId")

            // Then
            result.collect {
                assertTrue(it.isEmpty())
            }
            verify(exactly = 1) {
                musicLandingRepository.getCmsShelfList(
                    any(),
                    any(),
                    any(),
                    any()
                )
            }
        }

    @Test
    fun execute_success_responseDataIsNotNull_shelfListIsNotNull_viewTypeIsGrid2_returnShelfList() =
        runTest {
            // Given
            val mockSetting = Setting().apply {
                this.shelfCode = "shelfCode"
                this.viewType = MusicShelfType.GRID_2.value
                this.titleEn = "titleEn"
                this.titleTh = "titleTh"
            }
            val mockShelf = Shelf().apply {
                this.setting = mockSetting
            }
            val mockData = Data().apply {
                this.shelfList = listOf(mockShelf)
            }
            every { localizationRepository.getAppCountryCode() } returns LocalizationRepository.Localization.TH.countryCode
            every { localizationRepository.getAppLanguageCode() } returns LocalizationRepository.Localization.TH.languageCode
            every { musicLandingRepository.getCmsShelfList(any(), any(), any(), any()) } returns
                flowOf(mockData)

            // When
            val result = getContentBaseShelfUseCase.execute("baseShelfId")

            // Then
            result.collect {
                val shelf = it.first()
                assertEquals(0, shelf.index)
                assertEquals(0, shelf.shelfIndex)
                assertEquals(mockSetting.shelfCode, shelf.shelfId)
                assertEquals(mockSetting.titleTh, shelf.title)
                assertEquals(mockSetting.titleEn, shelf.titleFA)
                assertEquals(ProductListType.CONTENT, shelf.productListType)
                assertEquals(MusicShelfType.GRID_2, shelf.shelfType)
            }
            verify(exactly = 1) {
                musicLandingRepository.getCmsShelfList(any(), any(), any(), any())
            }
        }
}
