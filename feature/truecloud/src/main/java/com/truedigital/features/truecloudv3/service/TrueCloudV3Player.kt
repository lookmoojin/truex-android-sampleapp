package com.truedigital.features.truecloudv3.service

import android.content.Context
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.truedigital.features.tuned.service.music.exoutil.TunedLoadControl
import com.truedigital.foundation.player.BasePlayer
import com.truedigital.foundation.player.model.MediaAsset

class TrueCloudV3Player(
    private val context: Context,
) : BasePlayer() {
    private val defaultTrackSelector = DefaultTrackSelector(
        context,
        DefaultTrackSelector.Parameters.Builder(context)
            .setForceHighestSupportedBitrate(true).build(),
        AdaptiveTrackSelection.Factory()
    )
    private var renderersFactory = DefaultRenderersFactory(context)

    override val supportsSeeking: Boolean = true
    override val renderers: Array<MediaCodecRenderer> = arrayOf()
    override val player: ExoPlayer = ExoPlayer.Builder(context, renderersFactory)
        .setTrackSelector(defaultTrackSelector)
        .setLoadControl(TunedLoadControl())
        .build()

    override fun prepare(mediaAsset: MediaAsset, playWhenReady: Boolean) {
        val url = mediaAsset.location
        if (url != null) {
            player.playWhenReady = playWhenReady

            assetId = mediaAsset.id
            val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context)
            val mediaSource: MediaSource = ProgressiveMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(url))
            player.setMediaSource(mediaSource)
            player.prepare()
        }
    }
}
