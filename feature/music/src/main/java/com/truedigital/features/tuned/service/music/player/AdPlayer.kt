package com.truedigital.features.tuned.service.music.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoRendererEventListener
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.service.music.exoutil.StreamExtractorFactory
import com.truedigital.foundation.player.BasePlayer
import com.truedigital.foundation.player.model.MediaAsset

class AdPlayer(val context: Context) : BasePlayer() {

    private val defaultTrackSelector = DefaultTrackSelector(
        DefaultTrackSelector.ParametersBuilder(context).setForceHighestSupportedBitrate(true)
            .build(),
        AdaptiveTrackSelection.Factory()
    )
    private var mediaSourceFactory: MediaSource.Factory = DefaultMediaSourceFactory(context)
    private var renderersFactory = RenderersFactory { _: Handler?, _: VideoRendererEventListener?,
        _: AudioRendererEventListener?, _: TextOutput?,
        _: MetadataOutput? ->
        renderers
    }

    override val supportsSeeking: Boolean = true
    override val renderers = arrayOf<MediaCodecRenderer>(
        MediaCodecAudioRenderer(
            context,
            MediaCodecSelector.DEFAULT,
            null,
            null
        )
    )
    override val player: ExoPlayer = ExoPlayer.Builder(context, renderersFactory)
        .setTrackSelector(defaultTrackSelector)
        .setMediaSourceFactory(mediaSourceFactory)
        .setPauseAtEndOfMediaItems(false)
        .setLooper(Looper.getMainLooper())
        .build()

    override fun prepare(mediaAsset: MediaAsset, playWhenReady: Boolean) {
        player.playWhenReady = playWhenReady

        assetId = mediaAsset.id
        val dataSourceFactory = DefaultDataSource.Factory(
            context,
            DefaultHttpDataSource.Factory()
                .setUserAgent(
                    Util.getUserAgent(
                        context,
                        context.getString(R.string.app_name)
                    ),
                )
        )

        val streamSource =
            ProgressiveMediaSource.Factory(dataSourceFactory, StreamExtractorFactory())
                .createMediaSource(MediaItem.fromUri(Uri.parse(mediaAsset.location)))
        player.setMediaSource(streamSource)
        player.prepare()
    }
}
