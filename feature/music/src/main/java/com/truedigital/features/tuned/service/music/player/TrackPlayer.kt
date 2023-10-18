package com.truedigital.features.tuned.service.music.player

import android.content.Context
import android.os.Handler
import com.facebook.crypto.Crypto
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.drm.DrmSessionEventListener
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.source.StreamMediaSource
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.video.VideoRendererEventListener
import com.truedigital.features.tuned.service.music.datasource.StreamCachingDataSource
import com.truedigital.features.tuned.service.music.exoutil.StreamExtractorFactory
import com.truedigital.features.tuned.service.music.exoutil.TunedLoadControl
import com.truedigital.foundation.player.BasePlayer
import com.truedigital.foundation.player.model.MediaAsset
import okhttp3.OkHttpClient
import javax.inject.Provider

class TrackPlayer(
    val context: Context,
    private val httpClientBuilder: Provider<OkHttpClient.Builder>,
    val crypto: Crypto,
    private val mediaSourceEventListener: MediaSourceEventListener,
    private val drmSessionEventListener: DrmSessionEventListener
) : BasePlayer() {

    private val defaultTrackSelector = DefaultTrackSelector(
        DefaultTrackSelector.ParametersBuilder(context)
            .setForceHighestSupportedBitrate(true).build(),
        AdaptiveTrackSelection.Factory()
    )
    private var renderersFactory = RenderersFactory { _: Handler?, _: VideoRendererEventListener?,
        _: AudioRendererEventListener?, _: TextOutput?,
        _: MetadataOutput? ->
        renderers
    }

    override val supportsSeeking: Boolean = true
    override val renderers = arrayOf<MediaCodecRenderer>(
        MediaCodecAudioRenderer(context, MediaCodecSelector.DEFAULT, null, null)
    )
    override val player: ExoPlayer = ExoPlayer.Builder(context, renderersFactory)
        .setTrackSelector(defaultTrackSelector)
        .setLoadControl(TunedLoadControl())
        .build()

    override fun prepare(mediaAsset: MediaAsset, playWhenReady: Boolean) {
        player.playWhenReady = playWhenReady

        assetId = mediaAsset.id
        val streamSource = StreamMediaSource(
            mediaAsset,
            StreamCachingDataSource.Factory(httpClientBuilder, crypto),
            StreamExtractorFactory(),
            mediaSourceEventListener,
            drmSessionEventListener
        )
        player.setMediaSource(streamSource)
        player.prepare()
    }
}
