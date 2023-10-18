package com.truedigital.foundation.player.model

open class MediaAsset(
    var id: Int,
    var location: String? = null,
    var cachePath: String? = null,
    var sessionId: String? = null
)
