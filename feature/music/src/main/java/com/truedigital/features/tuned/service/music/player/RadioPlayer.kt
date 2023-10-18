package com.truedigital.features.tuned.service.music.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController
import com.truedigital.foundation.player.BasePlayer
import com.truedigital.foundation.player.model.MediaAsset

class RadioPlayer(
    val context: Context
) : BasePlayer() {
    override val supportsSeeking: Boolean = false

    override val renderers: Array<MediaCodecRenderer> = arrayOf<MediaCodecRenderer>(
        MediaCodecAudioRenderer(context, MediaCodecSelector.DEFAULT, null, null)
    )

    private val mediaSourceFactory: MediaSource.Factory = DefaultMediaSourceFactory(context)

    override val player: ExoPlayer = ExoPlayer
        .Builder(context)
        .setMediaSourceFactory(mediaSourceFactory)
        .build()

    override fun prepare(mediaAsset: MediaAsset, playWhenReady: Boolean) {
        if (mediaAsset is MusicPlayerController.RadioMediaAsset) {
            assetId = mediaAsset.id
            player.playWhenReady = playWhenReady
            setMediaSource(createHlsMediaSource(mediaAsset))
            seekToDefaultPosition()
            prepare()
        }
    }

    fun reInitializePlayer() {
        seekToDefaultPosition()
        prepare()
    }

    private fun createHlsMediaSource(mediaAsset: MusicPlayerController.RadioMediaAsset): HlsMediaSource {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        return HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(mediaAsset.radio.streamUrl)))
    }
}
