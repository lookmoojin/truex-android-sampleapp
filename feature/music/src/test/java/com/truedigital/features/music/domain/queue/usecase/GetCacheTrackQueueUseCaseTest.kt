package com.truedigital.features.music.domain.queue.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.queue.repository.CacheTrackQueueRepository
import com.truedigital.features.utils.MockDataModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GetCacheTrackQueueUseCaseTest {
    private lateinit var getCacheTrackQueueUseCase: GetCacheTrackQueueUseCase
    private val cacheTrackQueueRepository: CacheTrackQueueRepository = mock()
    private val mockTrackList = listOf(MockDataModel.mockTrack)

    @BeforeEach
    fun setup() {
        getCacheTrackQueueUseCase = GetCacheTrackQueueUseCaseImpl(
            cacheTrackQueueRepository
        )
    }

    @Test
    fun testGetCacheTrackQueue_cacheTrackQueueNotEmpty_returnTrackQueueNotLoad() {
        whenever(cacheTrackQueueRepository.getCacheTrackQueue(any())).thenReturn(mockTrackList)

        val result = getCacheTrackQueueUseCase.execute(1)

        assertEquals(1, result.first.size)
        assertEquals(false, result.second)

        verify(cacheTrackQueueRepository, times(0)).getCachePlaylistTrackQueue(any())
    }

    @Test
    fun testGetCacheTrackQueue_cacheTrackQueueEmpty_returnPlaylistTrackQueueAndLoad() {
        whenever(cacheTrackQueueRepository.getCacheTrackQueue(any())).thenReturn(emptyList())
        whenever(cacheTrackQueueRepository.getCachePlaylistTrackQueue(any())).thenReturn(
            mockTrackList
        )

        val result = getCacheTrackQueueUseCase.execute(1)

        assertEquals(1, result.first.size)
        assertEquals(true, result.second)

        verify(cacheTrackQueueRepository, times(1)).getCachePlaylistTrackQueue(any())
    }
}
