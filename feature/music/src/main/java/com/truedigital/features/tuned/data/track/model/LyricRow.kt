package com.truedigital.features.tuned.data.track.model

data class LyricRow(
    var type: LyricContentType,
    var startTime: Long = 0L,
    var content: String
)
