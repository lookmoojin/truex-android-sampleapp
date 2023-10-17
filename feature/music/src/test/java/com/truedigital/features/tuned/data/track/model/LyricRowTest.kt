package com.truedigital.features.tuned.data.track.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LyricRowTest {

    @Test
    fun testInitialLyricRow_returnLyricRow() {
        val mockLyricRow = LyricRow(
            type = LyricContentType.LYRIC,
            content = "content"
        )
        assertEquals(LyricContentType.LYRIC, mockLyricRow.type)
        assertEquals(0L, mockLyricRow.startTime)
        assertEquals("content", mockLyricRow.content)
    }

    @Test
    fun testSetLyricRow_returnLyricRow() {
        val mockLyricRow = LyricRow(
            type = LyricContentType.LYRIC,
            content = "content"
        )
        mockLyricRow.type = LyricContentType.TITLE
        mockLyricRow.startTime = 1L
        mockLyricRow.content = "new_content"

        assertEquals(LyricContentType.TITLE, mockLyricRow.type)
        assertEquals(1L, mockLyricRow.startTime)
        assertEquals("new_content", mockLyricRow.content)
    }
}
