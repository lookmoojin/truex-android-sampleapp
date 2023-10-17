package com.truedigital.features.tuned.domain.facade.playerqueue

interface PlayerQueueFacade {
    fun isShuffleEnabled(): Boolean
    fun hasSequentialPlaybackRight(): Boolean
    fun hasPlaylistWriteRight(): Boolean
}
