package com.truedigital.features.tuned.service.model

import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.service.music.model.TrackQueue
import com.truedigital.features.tuned.service.util.PlayQueue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TrackQueueTest {

    private fun mockTrack(id: Int) = Track(
        id = id,
        playlistTrackId = 1,
        songId = 1,
        releaseId = 1,
        artists = listOf(),
        name = "name",
        originalCredit = "originalCredit",
        isExplicit = false,
        trackNumber = 1,
        trackNumberInVolume = 1,
        volumeNumber = 1,
        releaseArtists = listOf(),
        sample = "sample",
        isOnCompilation = false,
        releaseName = "releaseName",
        allowDownload = false,
        allowStream = false,
        duration = 3L,
        image = "image",
        hasLyrics = false,
        video = null,
        isVideo = false,
        vote = null,
        isDownloaded = false,
        syncProgress = 1F,
        isCached = false,
        translationsList = listOf(
            Translation(
                language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH,
                value = "nameTh"
            )
        )
    )

    private val mockTrackList = listOf(mockTrack(1), mockTrack(2))

    @Test
    fun testRequireMoreTracks_currentIndexDefault_returnFalse() {
        val mockTrackQueue = TrackQueue(mockTrackList)
        assertFalse(mockTrackQueue.requireMoreTracks())
    }

    @Test
    fun testRequireMoreTracks_currentIndexEquals_returnTrue() {
        val mockTrackQueue = TrackQueue(mockTrackList)
        mockTrackQueue.currentIndex = 0

        assertTrue(mockTrackQueue.requireMoreTracks())
    }

    @Test
    fun testRequireMoreTracks_currentIndexMore_returnTrue() {
        val mockTrackQueue = TrackQueue(mockTrackList)
        mockTrackQueue.currentIndex = 1

        assertTrue(mockTrackQueue.requireMoreTracks())
    }

    @Test
    fun testGetIndexInDisplayOrder_shuffleModeNone_returnCurrentIndex() {
        val mockTrackQueue = TrackQueue(mockTrackList)
        mockTrackQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE
        mockTrackQueue.currentIndex = 1

        assertEquals(1, mockTrackQueue.getIndexInDisplayOrder())
    }

    @Test
    fun testGetIndexInDisplayOrder_shuffleModeAll_returnIndex() {
        val mockTrackQueue = TrackQueue(listOf(mockTrack(1)))
        mockTrackQueue.enableShuffle(0)
        mockTrackQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockTrackQueue.currentIndex = 0

        assertEquals(0, mockTrackQueue.getIndexInDisplayOrder())
    }

    @Test
    fun testGetTracksInPlayOrder_shuffleModeNone_returnTrackList() {
        val mockTrackQueue = TrackQueue(mockTrackList)
        mockTrackQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        val result = mockTrackQueue.getTracksInPlayOrder()
        assertEquals(2, result.size)
        assertEquals(1, result.first().id)
        assertEquals(2, result[1].id)
        assertEquals(0, mockTrackQueue.shuffleIndexList.size)
    }

    @Test
    fun testGetTracksInPlayOrder_shuffleModeAll_returnShuffleIndexList() {
        val mockTrackQueue = TrackQueue(mockTrackList)
        mockTrackQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockTrackQueue.enableShuffle(0)

        val result = mockTrackQueue.getTracksInPlayOrder()
        assertEquals(2, result.size)
        assertEquals(1, result.first().id)
        assertEquals(2, result[1].id)
        assertEquals(2, mockTrackQueue.shuffleIndexList.size)
    }

    @Test
    fun testGenerateTrackQueueInfo_repeatModeNone_returnTrackQueueInfo() {
        val mockTrackQueue = TrackQueue(mockTrackList)
        mockTrackQueue.repeatMode = PlayQueue.REPEAT_MODE_NONE

        val result = mockTrackQueue.generateTrackQueueInfo()
        assertEquals(2, result.queueInDisplayOrder.size)
        assertEquals(2, result.queueInPlayOrder.size)
        assertEquals(-1, result.indexInDisplayOrder)
        assertEquals(-1, result.indexInPlayOrder)
        assertEquals(false, result.isRepeatAll)
    }

    @Test
    fun testGenerateTrackQueueInfo_repeatModeAll_returnTrackQueueInfo() {
        val mockTrackQueue = TrackQueue(mockTrackList)
        mockTrackQueue.repeatMode = PlayQueue.REPEAT_MODE_ALL

        val result = mockTrackQueue.generateTrackQueueInfo()
        assertEquals(2, result.queueInDisplayOrder.size)
        assertEquals(2, result.queueInPlayOrder.size)
        assertEquals(-1, result.indexInDisplayOrder)
        assertEquals(-1, result.indexInPlayOrder)
        assertEquals(true, result.isRepeatAll)
    }

    @Test
    fun testUpdateTrackList_returnCurrentTrackList() {
        val mockTrackQueue = TrackQueue(mockTrackList)

        mockTrackQueue.updateTrackList(listOf(mockTrack(3)))

        assertEquals(1, mockTrackQueue.getTrackListSize())
    }
}
