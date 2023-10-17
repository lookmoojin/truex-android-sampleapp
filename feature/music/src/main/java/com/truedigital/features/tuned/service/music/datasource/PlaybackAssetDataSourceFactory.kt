package com.truedigital.features.tuned.service.music.datasource

import com.google.android.exoplayer2.upstream.DataSource
import com.truedigital.foundation.player.model.MediaAsset

interface PlaybackAssetDataSourceFactory {
    fun createDataSource(mediaAsset: MediaAsset): DataSource
}
