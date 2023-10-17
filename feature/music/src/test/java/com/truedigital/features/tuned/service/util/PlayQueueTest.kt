package com.truedigital.features.tuned.service.util

import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.tuned.data.track.model.Track
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PlayQueueTest {

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
    fun testPlayQueueInitial_setData_returnData() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(0, mockPlayQueue.shuffleIndexList.size)
        assertEquals(-1, mockPlayQueue.currentIndex)
        assertEquals(PlayQueue.SHUFFLE_MODE_NONE, mockPlayQueue.shuffleMode)
        assertEquals(PlayQueue.REPEAT_MODE_NONE, mockPlayQueue.repeatMode)
    }

    @Test
    fun testGetTrackListSize_setData_returnTrackListSize() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        assertEquals(2, mockPlayQueue.getTrackListSize())
    }

    @Test
    fun testEnableShuffle_defaultCurrentIndex_returnShuffleIndexes() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.enableShuffle()

        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(2, mockPlayQueue.shuffleIndexList.size)
        assertEquals(-1, mockPlayQueue.currentIndex)
        assertEquals(PlayQueue.SHUFFLE_MODE_ALL, mockPlayQueue.shuffleMode)
    }

    @Test
    fun testEnableShuffle_returnShuffleIndexes() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.enableShuffle(1)

        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(2, mockPlayQueue.shuffleIndexList.size)
        assertEquals(1, mockPlayQueue.shuffleIndexList.first())
        assertEquals(0, mockPlayQueue.currentIndex)
        assertEquals(PlayQueue.SHUFFLE_MODE_ALL, mockPlayQueue.shuffleMode)
    }

    @Test
    fun testDisableShuffle_returnEmptyShuffleIndexes() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.enableShuffle(1)
        mockPlayQueue.disableShuffle()

        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(0, mockPlayQueue.shuffleIndexList.size)
        assertNotEquals(-1, mockPlayQueue.currentIndex)
        assertEquals(PlayQueue.SHUFFLE_MODE_NONE, mockPlayQueue.shuffleMode)
    }

    @Test
    fun testDisableShuffle_CurrentIndexDefault_returnEmptyShuffleIndexes() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.enableShuffle(1)
        mockPlayQueue.currentIndex = -1
        mockPlayQueue.disableShuffle()

        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(0, mockPlayQueue.shuffleIndexList.size)
        assertEquals(-1, mockPlayQueue.currentIndex)
        assertEquals(PlayQueue.SHUFFLE_MODE_NONE, mockPlayQueue.shuffleMode)
    }

    @Test
    fun testDisableShuffle_shuffleIndexEmpty_returnEmptyShuffleIndexes() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.disableShuffle()

        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(0, mockPlayQueue.shuffleIndexList.size)
        assertEquals(0, mockPlayQueue.currentIndex)
        assertEquals(PlayQueue.SHUFFLE_MODE_NONE, mockPlayQueue.shuffleMode)
    }

    @Test
    fun testAddToEnd_shuffleModeNone_returnTrackList() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE
        mockPlayQueue.addToEnd(listOf(mockTrack(3)))

        assertEquals(3, mockPlayQueue.getTrackListSize())
    }

    @Test
    fun testAddToEnd_shuffleModeAll_returnTrackList() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.enableShuffle(1)
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockPlayQueue.addToEnd(listOf(mockTrack(3)))

        assertEquals(3, mockPlayQueue.getTrackListSize())
        assertEquals(0, mockPlayQueue.currentIndex)
    }

    @Test
    fun testAddToEnd_shuffleModeOther_returnSameTrackList() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.shuffleMode = 10
        mockPlayQueue.addToEnd(listOf(mockTrack(3)))

        assertEquals(2, mockPlayQueue.getTrackListSize())
    }

    @Test
    fun testRemoveTrack_returnTrackList() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.remove(mockTrack(1))

        assertEquals(1, mockPlayQueue.getTrackListSize())
    }

    @Test
    fun testRemovePosition_positionLessThanZero_returnTrackList() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.remove(-1)

        assertEquals(2, mockPlayQueue.getTrackListSize())
    }

    @Test
    fun testRemovePosition_shuffleModeOther_returnSameTrackList() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.shuffleMode = 10
        mockPlayQueue.remove(1)

        assertEquals(2, mockPlayQueue.getTrackListSize())
    }

    @Test
    fun testRemovePosition_shuffleModeNone_returnTrackList() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.remove(0)

        assertEquals(1, mockPlayQueue.getTrackListSize())
        assertEquals(-1, mockPlayQueue.currentIndex)
    }

    @Test
    fun testRemovePosition_shuffleModeNone_positionMoreThanCurrentIndex_returnTrackList() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.remove(1)

        assertEquals(1, mockPlayQueue.getTrackListSize())
        assertEquals(0, mockPlayQueue.currentIndex)
    }

    @Test
    fun testRemovePosition_shuffleModeAll_returnTrackList() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockPlayQueue.enableShuffle(0)
        mockPlayQueue.remove(0)

        assertEquals(1, mockPlayQueue.getTrackListSize())
        assertEquals(1, mockPlayQueue.shuffleIndexList.size)
        assertEquals(-1, mockPlayQueue.currentIndex)
    }

    @Test
    fun testRemovePosition_shuffleModeAll_positionMoreThanCurrentIndex_returnTrackList() {
        val mockPlayQueue = PlayQueue(listOf(mockTrack(1), mockTrack(2), mockTrack(3)))
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockPlayQueue.enableShuffle(0)
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.remove(2)

        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(2, mockPlayQueue.shuffleIndexList.size)
        assertEquals(0, mockPlayQueue.currentIndex)
    }

    @Test
    fun testMove_shuffleModeNone_currentIndexEqualsOldIndex_returnCurrentIndexIsNewIndex() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.move(0, 1)

        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(1, mockPlayQueue.currentIndex)
    }

    @Test
    fun testMove_shuffleModeNone_currentIndexInUntil_returnCurrentIndexPlus() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.move(1, 0)

        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(1, mockPlayQueue.currentIndex)
    }

    @Test
    fun testMove_shuffleModeNone_currentIndexInRange_returnCurrentIndexMinus() {
        val mockPlayQueue = PlayQueue(listOf(mockTrack(1), mockTrack(2), mockTrack(3)))
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE
        mockPlayQueue.currentIndex = 1
        mockPlayQueue.move(0, 2)

        assertEquals(3, mockPlayQueue.getTrackListSize())
        assertEquals(0, mockPlayQueue.currentIndex)
    }

    @Test
    fun testMove_shuffleModeNone_currentIndexDefault_returnCurrentIndexDefault() {
        val mockPlayQueue = PlayQueue(listOf(mockTrack(1), mockTrack(2), mockTrack(3)))
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE
        mockPlayQueue.currentIndex = -1
        mockPlayQueue.move(0, 2)

        assertEquals(3, mockPlayQueue.getTrackListSize())
        assertEquals(-1, mockPlayQueue.currentIndex)
    }

    @Test
    fun testMove_shuffleModeAll_isOldIndex_returnCurrentIndex() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.enableShuffle(0)
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.move(0, 1)

        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(1, mockPlayQueue.currentIndex)
    }

    @Test
    fun testMove_shuffleModeAll_currentIndexInUntil_returnCurrentIndexPlus() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.enableShuffle(0)
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.move(1, 0)

        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(1, mockPlayQueue.currentIndex)
    }

    @Test
    fun testMove_shuffleModeAll_currentIndexInRange_returnCurrentIndexMinus() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.enableShuffle(0)
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockPlayQueue.currentIndex = 1
        mockPlayQueue.move(0, 1)

        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(0, mockPlayQueue.currentIndex)
    }

    @Test
    fun testMove_shuffleModeOther_returnSameCurrentIndex() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.shuffleMode = 10
        mockPlayQueue.currentIndex = -1
        mockPlayQueue.move(0, 1)

        assertEquals(2, mockPlayQueue.getTrackListSize())
        assertEquals(-1, mockPlayQueue.currentIndex)
    }

    @Test
    fun testClear_returnEmptyList() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.enableShuffle(0)
        mockPlayQueue.clear()

        assertEquals(0, mockPlayQueue.getTrackListSize())
        assertEquals(0, mockPlayQueue.shuffleIndexList.size)
    }

    @Test
    fun testIsEmpty_trackListNotEmpty_returnFalse() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        assertFalse(mockPlayQueue.isEmpty())
    }

    @Test
    fun testIsEmpty_trackListEmpty_returnTrue() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.clear()

        assertTrue(mockPlayQueue.isEmpty())
    }

    @Test
    fun testIsShuffle_shuffleModeNone_returnFalse() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertFalse(mockPlayQueue.isShuffle())
    }

    @Test
    fun testIsShuffle_shuffleModeAll_returnTrue() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL

        assertTrue(mockPlayQueue.isShuffle())
    }

    @Test
    fun testIsRepeat_repeatModeNone_returnFalse() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_NONE

        assertFalse(mockPlayQueue.isRepeat())
    }

    @Test
    fun testIsRepeat_repeatModeAll_returnTrue() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ALL

        assertTrue(mockPlayQueue.isRepeat())
    }

    @Test
    fun testHasNext_trackListEmpty_returnFalse() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.clear()

        assertFalse(mockPlayQueue.hasNext())
    }

    @Test
    fun testHasNext_repeatModeAll_returnTrue() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ALL

        assertTrue(mockPlayQueue.hasNext())
    }

    @Test
    fun testHasNext_repeatModeOne_currentNotNull_returnTrue() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 1
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ONE
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertTrue(mockPlayQueue.hasNext())
    }

    @Test
    fun testHasNext_repeatModeOne_currentNull_returnFalse() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = -1
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ONE
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertFalse(mockPlayQueue.hasNext())
    }

    @Test
    fun testHasNext_repeatModeNone_currentIndexLessThanSize_returnTrue() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_NONE

        assertTrue(mockPlayQueue.hasNext())
    }

    @Test
    fun testHasNext_repeatModeNone_currentIndexMoreThanSize_returnFalse() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 1
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_NONE

        assertFalse(mockPlayQueue.hasNext())
    }

    @Test
    fun testHasPrevious_trackListEmpty_returnFalse() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.clear()

        assertFalse(mockPlayQueue.hasPrevious())
    }

    @Test
    fun testHasPrevious_repeatModeAll_returnTrue() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ALL

        assertTrue(mockPlayQueue.hasPrevious())
    }

    @Test
    fun testHasPrevious_repeatModeOne_currentNotNull_returnTrue() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 1
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ONE
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertTrue(mockPlayQueue.hasPrevious())
    }

    @Test
    fun testHasPrevious_repeatModeOne_currentNull_returnFalse() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = -1
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ONE
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertFalse(mockPlayQueue.hasPrevious())
    }

    @Test
    fun testHasPrevious_repeatModeNone_currentIndexEqualsZero_returnFalse() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_NONE

        assertFalse(mockPlayQueue.hasPrevious())
    }

    @Test
    fun testHasPrevious_repeatModeNone_currentIndexMoreThanZero_returnTrue() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 1
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_NONE

        assertTrue(mockPlayQueue.hasPrevious())
    }

    @Test
    fun testNext_hashNextFalse_returnNull() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.clear()

        assertNull(mockPlayQueue.next())
    }

    @Test
    fun testNext_hashNextTrue_repeatModeNoneAndShuffleModeNone_returnTrack() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_NONE
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertEquals(2, mockPlayQueue.next()?.id)
        assertEquals(1, mockPlayQueue.currentIndex)
    }

    @Test
    fun testNext_hashNextTrue_repeatModeOneAndShuffleModeAll_returnTrack() {
        val mockPlayQueue = PlayQueue(listOf(mockTrack(1)))
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ONE
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockPlayQueue.enableShuffle(0)

        assertEquals(1, mockPlayQueue.next()?.id)
        assertEquals(0, mockPlayQueue.currentIndex)
    }

    @Test
    fun testNext_hashNextTrue_repeatModeAllAndShuffleModeNone_returnTrack() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 1
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ALL
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertEquals(1, mockPlayQueue.next()?.id)
        assertEquals(0, mockPlayQueue.currentIndex)
    }

    @Test
    fun testNext_hashNextTrue_repeatModeAllAndCurrentIndexLessThanSize_returnTrack() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ALL
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertEquals(2, mockPlayQueue.next()?.id)
        assertEquals(1, mockPlayQueue.currentIndex)
    }

    @Test
    fun testCurrent_shuffleModeAll_returnTrack() {
        val mockPlayQueue = PlayQueue(listOf(mockTrack(1)))
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockPlayQueue.enableShuffle(0)

        assertEquals(1, mockPlayQueue.current()?.id)
    }

    @Test
    fun testCurrent_currentIndexEqualsTrackListSize_returnNull() {
        val mockPlayQueue = PlayQueue(listOf(mockTrack(1)))
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockPlayQueue.clear()

        assertEquals(null, mockPlayQueue.current())
    }

    @Test
    fun testPrevious_hasPreviousFalse_returnNull() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.clear()

        assertNull(mockPlayQueue.previous())
    }

    @Test
    fun testPrevious_hasPreviousTrue_repeatModeNoneAndShuffleModeNone_returnTrack() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 1
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_NONE
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertEquals(1, mockPlayQueue.previous()?.id)
        assertEquals(0, mockPlayQueue.currentIndex)
    }

    @Test
    fun testPrevious_hasPreviousTrue_repeatModeOneAndShuffleModeAll_returnTrack() {
        val mockPlayQueue = PlayQueue(listOf(mockTrack(1)))
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ONE
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockPlayQueue.enableShuffle(0)

        assertEquals(1, mockPlayQueue.previous()?.id)
        assertEquals(0, mockPlayQueue.currentIndex)
    }

    @Test
    fun testPrevious_hasPreviousTrue_repeatModeAllAndCurrentZero_returnTrack() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ALL
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertEquals(2, mockPlayQueue.previous()?.id)
        assertEquals(1, mockPlayQueue.currentIndex)
    }

    @Test
    fun testPrevious_hasPreviousTrue_repeatModeAllAndCurrentNotZero_returnTrack() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 1
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ALL
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertEquals(1, mockPlayQueue.previous()?.id)
        assertEquals(0, mockPlayQueue.currentIndex)
    }

    @Test
    fun testPeekNext_hashNextFalse_returnNull() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.clear()

        assertNull(mockPlayQueue.peekNext())
    }

    @Test
    fun testPeekNext_hashNextTrue_repeatModeNoneAndShuffleModeNone_returnTrack() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_NONE
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertEquals(2, mockPlayQueue.peekNext()?.id)
    }

    @Test
    fun testPeekNext_hashNextTrue_repeatModeOneAndShuffleModeAll_returnTrack() {
        val mockPlayQueue = PlayQueue(listOf(mockTrack(1)))
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ONE
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockPlayQueue.enableShuffle(0)

        assertEquals(1, mockPlayQueue.peekNext()?.id)
    }

    @Test
    fun testPeekNext_hashNextTrue_repeatModeAllAndShuffleModeNone_returnTrack() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 1
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ALL
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertEquals(1, mockPlayQueue.peekNext()?.id)
    }

    @Test
    fun testPeekNext_hashNextTrue_repeatModeAllAndCurrentIndexLessThanSize_returnTrack() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ALL
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertEquals(2, mockPlayQueue.peekNext()?.id)
    }

    @Test
    fun testPeekPrevious_hasPreviousFalse_returnNull() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.clear()

        assertNull(mockPlayQueue.peekPrevious())
    }

    @Test
    fun testPeekPrevious_hasPreviousTrue_repeatModeNoneAndShuffleModeNone_returnTrack() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 1
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_NONE
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertEquals(1, mockPlayQueue.peekPrevious()?.id)
    }

    @Test
    fun testPeekPrevious_hasPreviousTrue_repeatModeOneAndShuffleModeAll_returnTrack() {
        val mockPlayQueue = PlayQueue(listOf(mockTrack(1)))
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ONE
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_ALL
        mockPlayQueue.enableShuffle(0)

        assertEquals(1, mockPlayQueue.peekPrevious()?.id)
    }

    @Test
    fun testPeekPrevious_hasPreviousTrue_repeatModeAllAndCurrentZero_returnTrack() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 0
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ALL
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertEquals(2, mockPlayQueue.peekPrevious()?.id)
    }

    @Test
    fun testPeekPrevious_hasPreviousTrue_repeatModeAllAndCurrentNotZero_returnTrack() {
        val mockPlayQueue = PlayQueue(mockTrackList)
        mockPlayQueue.currentIndex = 1
        mockPlayQueue.repeatMode = PlayQueue.REPEAT_MODE_ALL
        mockPlayQueue.shuffleMode = PlayQueue.SHUFFLE_MODE_NONE

        assertEquals(1, mockPlayQueue.peekPrevious()?.id)
    }
}
