package com.truedigital.features.tuned.service.model

import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.service.music.model.StakkarQueue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class StakkarQueueTest {

    @Test
    fun testStakkarQueue_defaultData_returnAttachedTrackIdNull() {
        val mockStakkar = Stakkar(
            id = 1,
            publisherImage = "publisherImage",
            publisherName = "publisherName",
            type = Stakkar.MediaType.AUDIO,
            links = listOf(),
            bannerUrl = null,
            bannerImage = null,
            hideDialog = false
        )
        val result = StakkarQueue(listOf(mockStakkar))
        assertEquals(null, result.attachedTrackId)
    }

    @Test
    fun testStakkarQueue_attachedTrackIdNotNull_returnAttachedTrackIdValue() {
        val mockStakkar = Stakkar(
            id = 1,
            publisherImage = "publisherImage",
            publisherName = "publisherName",
            type = Stakkar.MediaType.AUDIO,
            links = listOf(),
            bannerUrl = null,
            bannerImage = null,
            hideDialog = false
        )
        val result = StakkarQueue(listOf(mockStakkar), 1)
        assertEquals(1, result.attachedTrackId)
    }
}
