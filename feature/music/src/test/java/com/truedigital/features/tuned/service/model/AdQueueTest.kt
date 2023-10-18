package com.truedigital.features.tuned.service.model

import com.truedigital.features.tuned.data.ad.model.Ad
import com.truedigital.features.tuned.service.music.model.AdQueue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AdQueueTest {

    @Test
    fun testAdQueue_defaultData_returnAttachedTrackIdNull() {
        val mockAd = Ad(
            title = "title",
            impressionUrl = "impressionUrl",
            duration = "duration",
            mediaFile = "mediaFile",
            image = "image",
            clickUrl = "clickUrl",
            vast = "vast"
        )
        val result = AdQueue(listOf(mockAd))
        assertEquals(null, result.attachedTrackId)
    }

    @Test
    fun testAdQueue_attachedTrackIdNotNull_returnAttachedTrackIdValue() {
        val mockAd = Ad(
            title = "title",
            impressionUrl = "impressionUrl",
            duration = "duration",
            mediaFile = "mediaFile",
            image = "image",
            clickUrl = "clickUrl",
            vast = "vast"
        )
        val result = AdQueue(listOf(mockAd), 1)
        assertEquals(1, result.attachedTrackId)
    }
}
