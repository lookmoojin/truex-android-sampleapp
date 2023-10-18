package com.truedigital.features.tuned.service.model

import com.truedigital.features.tuned.service.music.model.TrackQueueInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TrackQueueInfoTest {

    @Test
    fun testTrackQueueInfo_defaultData() {
        val result = TrackQueueInfo(
            queueInDisplayOrder = listOf(),
            queueInPlayOrder = listOf(),
            indexInDisplayOrder = 1,
            indexInPlayOrder = 1,
            isRepeatAll = false
        )
        assertEquals(0, result.queueInDisplayOrder.size)
        assertEquals(0, result.queueInPlayOrder.size)
        assertEquals(1, result.indexInDisplayOrder)
        assertEquals(1, result.indexInPlayOrder)
        assertEquals(false, result.isRepeatAll)
    }
}
