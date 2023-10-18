package com.truedigital.features.tuned.data.album.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class VolumeTest {

    @Test
    fun testVolumeData_mapData() {
        val mockVolume = Volume(
            firstTrackIndex = 1,
            lastTrackIndex = 10
        )
        assertEquals(1, mockVolume.firstTrackIndex)
        assertEquals(10, mockVolume.lastTrackIndex)
    }
}
