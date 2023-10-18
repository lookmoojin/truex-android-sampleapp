package com.truedigital.features.music.domain.queue.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.queue.repository.CacheTrackQueueRepository
import com.truedigital.features.music.domain.track.usecase.GetTrackListUseCase
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GetAllTrackQueueUseCaseTest {
    private lateinit var getAllTrackQueueUseCase: GetAllTrackQueueUseCase
    private val getTrackListUseCase: GetTrackListUseCase = mock()
    private val cacheTrackQueueRepository: CacheTrackQueueRepository = mock()

    @BeforeEach
    fun setup() {
        getAllTrackQueueUseCase = GetAllTrackQueueUseCaseImpl(
            getTrackListUseCase,
            cacheTrackQueueRepository
        )
    }

    @Test
    fun testGetAllTrackQueue_cacheTrackQueueNotEmpty_returnCache() = runTest {
        whenever(cacheTrackQueueRepository.getCacheTrackQueue(any())).thenReturn(
            listOf(MockDataModel.mockTrack)
        )

        getAllTrackQueueUseCase.execute(1)
            .collect {
                assertEquals(1, it.size)
            }
    }

    @Test
    fun testGetAllTrackQueue_cacheTrackQueueEmpty_returnTrackListApi() = runTest {
        val mockCacheTrackList = listOf(
            MockDataModel.mockTrack.copy(
                id = 1,
                name = "name1"
            ),
            MockDataModel.mockTrack.copy(
                id = 2,
                name = "name2"
            ),
            MockDataModel.mockTrack.copy(
                id = 3,
                name = "name3"
            )
        )
        val mockTrackList = listOf(
            MockDataModel.mockTrack.copy(
                id = 2,
                name = "name22"
            ),
            MockDataModel.mockTrack.copy(
                id = 3,
                name = "name33"
            ),
            MockDataModel.mockTrack.copy(
                id = 4,
                name = "name4"
            )
        )

        whenever(cacheTrackQueueRepository.getCacheTrackQueue(any())).thenReturn(emptyList())
        whenever(cacheTrackQueueRepository.getCachePlaylistTrackQueue(any()))
            .thenReturn(mockCacheTrackList)
        whenever(getTrackListUseCase.execute(any())).thenReturn(flowOf(mockTrackList))

        getAllTrackQueueUseCase.execute(1)
            .collect {
                assertEquals(4, it.size)
                assertEquals("name1", it[0].name)
                assertEquals("name22", it[1].name)
                assertEquals("name33", it[2].name)
                assertEquals("name4", it[3].name)
            }
        verify(cacheTrackQueueRepository, times(1)).saveCacheTrackQueue(any(), any())
    }
}
