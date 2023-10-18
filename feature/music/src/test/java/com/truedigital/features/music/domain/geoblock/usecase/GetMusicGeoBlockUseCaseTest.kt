package com.truedigital.features.music.domain.geoblock.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.datalegacy.data.base.Failure
import com.truedigital.common.share.datalegacy.data.base.Success
import com.truedigital.features.music.data.geoblock.repository.CacheMusicGeoBlockRepository
import com.truedigital.share.data.geoinformation.data.model.response.ResultModel
import com.truedigital.share.data.geoinformation.domain.usecase.GetGeoInformationByClientUseCase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@InternalCoroutinesApi
class GetMusicGeoBlockUseCaseTest {

    private lateinit var getMusicGeoBlockUseCase: GetMusicGeoBlockUseCase
    private val cacheMusicGeoBlockRepository: CacheMusicGeoBlockRepository = mock()
    private val getGeoInformationByClientUseCase: GetGeoInformationByClientUseCase = mock()

    @BeforeEach
    fun setup() {
        getMusicGeoBlockUseCase = GetMusicGeoBlockUseCaseImpl(
            cacheMusicGeoBlockRepository,
            getGeoInformationByClientUseCase
        )
    }

    @Test
    fun getGeoBlockCache_cacheIsNotNull_returnCache() = runTest {
        val mockCache = true
        whenever(cacheMusicGeoBlockRepository.loadCache()).thenReturn(mockCache)

        getMusicGeoBlockUseCase.execute()
            .collect {
                assertEquals(mockCache, it)
            }
    }

    @Test
    fun getGeoBlockApiSuccess_countryCodeIsNotTh_returnTrue() = runTest {
        val mockResponse = ResultModel().apply {
            countryCode = "ab"
        }
        whenever(cacheMusicGeoBlockRepository.loadCache())
            .thenReturn(null)
        whenever(getGeoInformationByClientUseCase.execute())
            .thenReturn(flowOf(Success(mockResponse)))

        getMusicGeoBlockUseCase.execute()
            .collect {
                assertTrue(it)
            }
        verify(cacheMusicGeoBlockRepository, times(1)).saveCache(true)
    }

    @Test
    fun getGeoBlockApiSuccess_countryCodeIsEmpty_returnTrue() = runTest {
        val mockResponse = ResultModel().apply {
            countryCode = ""
        }
        whenever(cacheMusicGeoBlockRepository.loadCache())
            .thenReturn(null)
        whenever(getGeoInformationByClientUseCase.execute())
            .thenReturn(flowOf(Success(mockResponse)))

        getMusicGeoBlockUseCase.execute()
            .collect {
                assertTrue(it)
            }
        verify(cacheMusicGeoBlockRepository, times(1)).saveCache(true)
    }

    @Test
    fun getGeoBlockApiSuccess_countryCodeIsThLowerCase_returnFalse() = runTest {
        val mockResponse = ResultModel().apply {
            countryCode = "TH"
        }
        whenever(cacheMusicGeoBlockRepository.loadCache())
            .thenReturn(null)
        whenever(getGeoInformationByClientUseCase.execute())
            .thenReturn(flowOf(Success(mockResponse)))

        getMusicGeoBlockUseCase.execute()
            .collect {
                assertFalse(it)
            }
        verify(cacheMusicGeoBlockRepository, times(1)).saveCache(false)
    }

    @Test
    fun getGeoBlockApiSuccess_countryCodeIsThUpperCase_returnFalse() = runTest {
        val mockResponse = ResultModel().apply {
            countryCode = "TH"
        }
        whenever(cacheMusicGeoBlockRepository.loadCache())
            .thenReturn(null)
        whenever(getGeoInformationByClientUseCase.execute())
            .thenReturn(flowOf(Success(mockResponse)))

        getMusicGeoBlockUseCase.execute()
            .collect {
                assertFalse(it)
            }
        verify(cacheMusicGeoBlockRepository, times(1)).saveCache(false)
    }

    @Test
    fun getGeoBlockApiSuccess_countryCodeIsNull_returnFalse() = runTest {
        val mockResponse = ResultModel().apply {
            countryCode = null
        }
        whenever(cacheMusicGeoBlockRepository.loadCache())
            .thenReturn(null)
        whenever(getGeoInformationByClientUseCase.execute())
            .thenReturn(flowOf(Success(mockResponse)))

        getMusicGeoBlockUseCase.execute()
            .collect {
                assertFalse(it)
            }
        verify(cacheMusicGeoBlockRepository, times(1)).saveCache(false)
    }

    @Test
    fun getGeoBlockApiError_returnFalse() = runTest {
        whenever(cacheMusicGeoBlockRepository.loadCache())
            .thenReturn(null)
        whenever(getGeoInformationByClientUseCase.execute())
            .thenReturn(flowOf(Failure(Throwable("error"))))

        getMusicGeoBlockUseCase.execute()
            .collect {
                assertFalse(it)
            }
        verify(cacheMusicGeoBlockRepository, times(1)).saveCache(false)
    }
}
