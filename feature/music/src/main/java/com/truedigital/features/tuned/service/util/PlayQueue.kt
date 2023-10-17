package com.truedigital.features.tuned.service.util

open class PlayQueue<T>(initialItems: Collection<T>) {

    // this matches the value in PlaybackStateCompat
    companion object {
        const val REPEAT_MODE_NONE = 0
        const val REPEAT_MODE_ONE = 1
        const val REPEAT_MODE_ALL = 2

        const val SHUFFLE_MODE_NONE = 0
        const val SHUFFLE_MODE_ALL = 1
    }

    protected val trackList: MutableList<T> = mutableListOf()
    var currentIndex = -1
    val shuffleIndexList = mutableListOf<Int>()
    var shuffleMode = SHUFFLE_MODE_NONE
    var repeatMode = REPEAT_MODE_NONE

    init {
        trackList.addAll(initialItems)
    }

    fun getTrackListSize() = trackList.size

    fun enableShuffle(startIndex: Int = currentIndex) {
        shuffleIndexList.clear()
        trackList.forEachIndexed { index, _ -> shuffleIndexList.add(index) }
        shuffleIndexList.shuffle()
        if (shuffleIndexList.contains(startIndex)) {
            shuffleIndexList.remove(startIndex)
            shuffleIndexList.add(0, startIndex)
            currentIndex = 0
        }
        shuffleMode = SHUFFLE_MODE_ALL
    }

    fun disableShuffle() {
        shuffleMode = SHUFFLE_MODE_NONE
        if (shuffleIndexList.isNotEmpty() && currentIndex != -1) {
            currentIndex = shuffleIndexList[currentIndex]
        }
        shuffleIndexList.clear()
    }

    fun addToEnd(tracks: Collection<T>) {
        when (shuffleMode) {
            SHUFFLE_MODE_NONE ->
                this.trackList.addAll(tracks)
            SHUFFLE_MODE_ALL -> {
                this.trackList.addAll(tracks)
                enableShuffle(shuffleIndexList[currentIndex])
            }
        }
    }

    fun remove(track: T) = remove(trackList.indexOf(track))

    fun remove(position: Int) {
        if (position < 0) return
        when (shuffleMode) {
            SHUFFLE_MODE_NONE -> {
                trackList.removeAt(position)
                if (position <= currentIndex) --currentIndex
            }
            SHUFFLE_MODE_ALL -> {
                trackList.removeAt(position)
                val positionInShuffleIndexes = shuffleIndexList.indexOf(position)
                shuffleIndexList.removeAt(positionInShuffleIndexes)
                shuffleIndexList.forEachIndexed { index, i ->
                    if (i > position) shuffleIndexList[index] = i - 1
                }
                if (positionInShuffleIndexes <= currentIndex) --currentIndex
            }
        }
    }

    fun move(oldIndex: Int, newIndex: Int) {
        val track = trackList[oldIndex]
        trackList.removeAt(oldIndex)
        trackList.add(newIndex, track)
        when (shuffleMode) {
            SHUFFLE_MODE_NONE -> {
                when (currentIndex) {
                    oldIndex -> currentIndex = newIndex
                    in newIndex until oldIndex -> ++currentIndex
                    in (oldIndex + 1)..newIndex -> --currentIndex
                }
            }
            SHUFFLE_MODE_ALL -> {
                when (shuffleIndexList[currentIndex]) {
                    oldIndex -> currentIndex = shuffleIndexList.indexOf(newIndex)
                    in newIndex until oldIndex ->
                        currentIndex = shuffleIndexList.indexOf(shuffleIndexList[currentIndex] + 1)
                    in (oldIndex + 1)..newIndex ->
                        currentIndex = shuffleIndexList.indexOf(shuffleIndexList[currentIndex] - 1)
                }
            }
        }
    }

    fun clear() {
        shuffleIndexList.clear()
        trackList.clear()
    }

    fun isEmpty() = trackList.size == 0

    fun isShuffle() = shuffleMode != SHUFFLE_MODE_NONE

    fun isRepeat() = repeatMode != REPEAT_MODE_NONE

    fun hasNext(): Boolean {
        if (trackList.size == 0) return false
        return when (repeatMode) {
            REPEAT_MODE_ALL -> true
            REPEAT_MODE_ONE -> current() != null
            else -> currentIndex < (trackList.size - 1)
        }
    }

    fun hasPrevious(): Boolean {
        if (trackList.size == 0) return false
        return when (repeatMode) {
            REPEAT_MODE_ALL -> true
            REPEAT_MODE_ONE -> current() != null
            else -> currentIndex > 0
        }
    }

    fun next(): T? {
        return if (hasNext()) {
            when {
                repeatMode == REPEAT_MODE_ALL && currentIndex >= trackList.size - 1 -> {
                    currentIndex = 0
                }
                repeatMode == REPEAT_MODE_ONE -> { /* do nothing because currentIndex doesn't change here */
                }
                else -> ++currentIndex
            }
            if (shuffleMode == SHUFFLE_MODE_ALL) trackList[shuffleIndexList[currentIndex]]
            else trackList[currentIndex]
        } else null
    }

    fun current(): T? {
        return if (currentIndex > -1 && currentIndex < trackList.size) {
            if (shuffleMode == SHUFFLE_MODE_ALL) trackList[shuffleIndexList[currentIndex]]
            else trackList[currentIndex]
        } else null
    }

    fun previous(): T? {
        return if (hasPrevious()) {
            when {
                repeatMode == REPEAT_MODE_ALL && currentIndex == 0 -> {
                    currentIndex = trackList.size - 1
                }
                repeatMode == REPEAT_MODE_ONE -> { /* do nothing because currentIndex doesn't change here */
                }
                else -> --currentIndex
            }
            if (shuffleMode == SHUFFLE_MODE_ALL) trackList[shuffleIndexList[currentIndex]]
            else trackList[currentIndex]
        } else null
    }

    fun peekNext(): T? {
        return if (hasNext()) {
            val nextIndex = when {
                repeatMode == REPEAT_MODE_ALL && currentIndex >= trackList.size - 1 -> 0
                repeatMode == REPEAT_MODE_ONE -> currentIndex
                else -> currentIndex + 1
            }
            if (shuffleMode == SHUFFLE_MODE_ALL) trackList[shuffleIndexList[nextIndex]]
            else trackList[nextIndex]
        } else null
    }

    fun peekPrevious(): T? {
        return if (hasPrevious()) {
            val prevIndex = when {
                repeatMode == REPEAT_MODE_ALL && currentIndex == 0 -> trackList.size - 1
                repeatMode == REPEAT_MODE_ONE -> currentIndex
                else -> currentIndex - 1
            }
            if (shuffleMode == SHUFFLE_MODE_ALL) trackList[shuffleIndexList[prevIndex]]
            else trackList[prevIndex]
        } else null
    }
}
