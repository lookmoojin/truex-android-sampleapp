package com.truedigital.features.music.domain.queue.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.truedigital.features.music.data.queue.repository.CacheTrackQueueRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ClearCacheTrackQueueUseCaseTest {

    private lateinit var clearCacheTrackQueueUseCase: ClearCacheTrackQueueUseCase
    private val cacheTrackQueueRepository: CacheTrackQueueRepository = mock()

    @BeforeEach
    fun setup() {
        clearCacheTrackQueueUseCase = ClearCacheTrackQueueUseCaseImpl(
            cacheTrackQueueRepository
        )
    }

    @Test
    fun testClearCacheTrackQueue_verifyClearCache() {
        clearCacheTrackQueueUseCase.execute()
        verify(cacheTrackQueueRepository, times(1)).clearCacheTrackQueue()
    }
}
