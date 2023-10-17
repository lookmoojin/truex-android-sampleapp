package com.truedigital.features.tuned.service.music.model

import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.service.util.PlayQueue

class TrackQueue(initialItems: Collection<Track>) : PlayQueue<Track>(initialItems) {

    // always keep at least 2 tracks in the list (including one is currently playing) for radio
    fun requireMoreTracks(): Boolean = currentIndex >= (trackList.size - 2)

    // get the index of the current track in the original queue (not shuffle queue)
    fun getIndexInDisplayOrder(): Int =
        when (shuffleMode) {
            SHUFFLE_MODE_ALL -> shuffleIndexList[currentIndex]
            else -> currentIndex
        }

    // get the queue in play sequence
    fun getTracksInPlayOrder(): List<Track> =
        when (shuffleMode) {
            SHUFFLE_MODE_ALL -> shuffleIndexList.map { trackList[it] }
            else -> trackList
        }

    fun generateTrackQueueInfo(): TrackQueueInfo {
        return TrackQueueInfo(
            trackList,
            getTracksInPlayOrder(),
            getIndexInDisplayOrder(),
            currentIndex,
            repeatMode == REPEAT_MODE_ALL
        )
    }

    fun updateTrackList(list: List<Track>) {
        trackList.clear()
        addToEnd(list)
    }
}
