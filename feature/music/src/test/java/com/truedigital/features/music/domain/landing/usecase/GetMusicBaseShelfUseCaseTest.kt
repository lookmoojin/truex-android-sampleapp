package com.truedigital.features.music.domain.landing.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepository
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Data
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Shelf
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetMusicBaseShelfUseCaseTest {

    private lateinit var getMusicBaseShelfUseCase: GetMusicBaseShelfUseCase
    private var cacheMusicLandingRepository: CacheMusicLandingRepository = mock()
    private var cmsShelvesRepository: CmsShelvesRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()

    private val mockShelf = Shelf().apply {
        this.setting = null
    }

    private val mockData = Data().apply {
        this.shelfList = listOf(mockShelf)
    }

    @BeforeEach
    fun setup() {
        getMusicBaseShelfUseCase = GetMusicBaseShelfUseCaseImpl(
            cacheMusicLandingRepository,
            cmsShelvesRepository,
            localizationRepository
        )
    }

    @Test
    fun testLoadCache_cacheIsNotNull_notSaveCache_returnCache() = runTest {
        val mockCache = "home"
        whenever(cacheMusicLandingRepository.loadPathApiForYouShelf()).thenReturn(mockCache)

        getMusicBaseShelfUseCase.execute("shelfId")
            .collect { result ->
                assertEquals(mockCache, result)
            }
        verify(cacheMusicLandingRepository, times(1)).loadPathApiForYouShelf()
        verify(cacheMusicLandingRepository, times(0)).savePathApiForYouShelf(any())
    }

    @Test
    fun testLoadApi_settingIsNull_notSaveCache_returnError() = runTest {
        val mockCache = "home"
        val mockResponse = Data().apply {
            setting = null
        }
        whenever(cacheMusicLandingRepository.loadPathApiForYouShelf()).thenReturn(mockCache)
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(),
                any(),
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(flowOf(mockResponse))

        getMusicBaseShelfUseCase.execute("shelfId")
            .catch { exception ->
                assertEquals(
                    exception.message,
                    "music base shelf error api path is empty"
                )
            }.collect()
        verify(cacheMusicLandingRepository, times(0)).savePathApiForYouShelf(any())
    }

    @Test
    fun testLoadApi_shelfSlugIsNull_notSaveCache_returnError() = runTest {
        val mockCache = "home"
        mockData.shelfList?.first()?.setting = Setting().apply {
            apiName = null
            type = "by_api"
            api = "home"
        }
        whenever(cacheMusicLandingRepository.loadPathApiForYouShelf()).thenReturn(mockCache)
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(),
                any(),
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(flowOf(mockData))

        getMusicBaseShelfUseCase.execute("shelfId")
            .catch { exception ->
                assertEquals(
                    exception.message,
                    "music base shelf error api path is empty"
                )
            }.collect()
        verify(cacheMusicLandingRepository, times(0)).savePathApiForYouShelf(any())
    }

    @Test
    fun testLoadApi_shelfSlugIsNotTuneGlobal_notSaveCache_returnError() = runTest {
        val mockCache = "home"
        mockData.shelfList?.first()?.setting = Setting().apply {
            apiName = "tune"
            type = "by_api"
            api = "home"
        }
        whenever(cacheMusicLandingRepository.loadPathApiForYouShelf()).thenReturn(mockCache)
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(),
                any(),
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(flowOf(mockData))

        getMusicBaseShelfUseCase.execute("shelfId")
            .catch { exception ->
                assertEquals(
                    exception.message,
                    "music base shelf error api path is empty"
                )
            }.collect()
        verify(cacheMusicLandingRepository, times(0)).savePathApiForYouShelf(any())
    }

    @Test
    fun testLoadApi_typeIsNull_notSaveCache_returnError() = runTest {
        val mockCache = "home"
        mockData.shelfList?.first()?.setting = Setting().apply {
            apiName = "tuneglobal"
            type = null
            api = "home"
        }
        whenever(cacheMusicLandingRepository.loadPathApiForYouShelf()).thenReturn(mockCache)
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(),
                any(),
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(flowOf(mockData))

        getMusicBaseShelfUseCase.execute("shelfId")
            .catch { exception ->
                assertEquals(
                    exception.message,
                    "music base shelf error api path is empty"
                )
            }.collect()
        verify(cacheMusicLandingRepository, times(0)).savePathApiForYouShelf(any())
    }

    @Test
    fun testLoadApi_typeIsNotByApi_notSaveCache_returnError() = runTest {
        val mockCache = "home"
        mockData.shelfList?.first()?.setting = Setting().apply {
            apiName = "tuneglobal"
            type = "type"
            api = "home"
        }
        whenever(cacheMusicLandingRepository.loadPathApiForYouShelf()).thenReturn(mockCache)
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(),
                any(),
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(flowOf(mockData))

        getMusicBaseShelfUseCase.execute("shelfId")
            .catch { exception ->
                assertEquals(
                    exception.message,
                    "music base shelf error api path is empty"
                )
            }.collect()
        verify(cacheMusicLandingRepository, times(0)).savePathApiForYouShelf(any())
    }

    @Test
    fun testLoadApi_apiIsNull_notSaveCache_returnError() = runTest {
        val mockCache = "home"
        mockData.shelfList?.first()?.setting = Setting().apply {
            apiName = "tuneglobal"
            type = "by_api"
            api = null
        }
        whenever(cacheMusicLandingRepository.loadPathApiForYouShelf()).thenReturn(mockCache)
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(),
                any(),
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(flowOf(mockData))

        getMusicBaseShelfUseCase.execute("shelfId")
            .catch { exception ->
                assertEquals(
                    exception.message,
                    "music base shelf error api path is empty"
                )
            }.collect()
        verify(cacheMusicLandingRepository, times(0)).savePathApiForYouShelf(any())
    }

    @Test
    fun testLoadApi_apiIsEmpty_notSaveCache_returnError() = runTest {
        val mockCache = "home"
        mockData.shelfList?.first()?.setting = Setting().apply {
            apiName = "tuneglobal"
            type = "by_api"
            api = ""
        }
        whenever(cacheMusicLandingRepository.loadPathApiForYouShelf()).thenReturn(mockCache)
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(),
                any(),
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(flowOf(mockData))

        getMusicBaseShelfUseCase.execute("shelfId")
            .catch { exception ->
                assertEquals(
                    exception.message,
                    "music base shelf error api path is empty"
                )
            }.collect()
        verify(cacheMusicLandingRepository, times(0)).savePathApiForYouShelf(any())
    }

    @Test
    fun testLoadApi_success_saveCache_returnData() = runTest {
        val mockPathApi = "home"
        mockData.shelfList?.first()?.setting = Setting().apply {
            apiName = com.truedigital.features.listens.share.constant.MusicConstant.Key.SLUG_TUNED_GLOBAL
            type = "by_api"
            api = mockPathApi
        }
        whenever(cacheMusicLandingRepository.loadPathApiForYouShelf()).thenReturn(null)
        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(),
                any(),
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(flowOf(mockData))

        getMusicBaseShelfUseCase.execute("shelfId")
            .collect { result ->
                assertEquals(mockPathApi, result)
            }
        verify(cacheMusicLandingRepository, times(1)).savePathApiForYouShelf(any())
    }
}
