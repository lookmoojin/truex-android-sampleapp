package com.truedigital.features.tuned.service.music.model

import com.truedigital.features.tuned.data.track.model.Track

data class TrackQueueInfo(
    val queueInDisplayOrder: List<Track>,
    val queueInPlayOrder: List<Track>,
    val indexInDisplayOrder: Int,
    val indexInPlayOrder: Int,
    val isRepeatAll: Boolean
)
