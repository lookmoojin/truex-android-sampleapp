package com.truedigital.features.tuned.service.music.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.service.music.exoutil.StreamExtractorFactory
import com.truedigital.features.tuned.service.music.exoutil.TunedVideoLoadControl
import com.truedigital.foundation.player.BasePlayer
import com.truedigital.foundation.player.model.MediaAsset

class VideoPlayer(val context: Context) : BasePlayer() {

    private val defaultTrackSelector =
        DefaultTrackSelector(context, AdaptiveTrackSelection.Factory())

    override val supportsSeeking: Boolean = true
    override val renderers = arrayOf<MediaCodecRenderer>()
    override val player: ExoPlayer =
        ExoPlayer.Builder(context, DefaultRenderersFactory(context))
            .setTrackSelector(defaultTrackSelector)
            .setBandwidthMeter(DefaultBandwidthMeter.Builder(context).build())
            .setLoadControl(TunedVideoLoadControl())
            .build()

    // set volume for ducking different for VideoPlayer because using SimpleExoPlayer
    override fun setVolume(volume: Float) {
        player.volume = volume
    }

    override fun getVolume(): Float = 1F

    override fun prepare(mediaAsset: MediaAsset, playWhenReady: Boolean) {
        player.playWhenReady = playWhenReady

        assetId = mediaAsset.id
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(Util.getUserAgent(context, context.getString(R.string.app_name)))

        // test video
//        val hlsMediaSource = HlsMediaSource
//                .Factory(dataSourceFactory)
//                .createMediaSource(Uri.parse(
//                 "https://tgprod-nonsecure-video-assets-x521qalfapfo.stackpathdns.com/
//                 hls/sea_processed/hls_streams/121/1_7/889/275/999/50/603_1211_788927599950_788927599950.m3u8
//                 ?st=kNTTtkiL7LwEhMNnGnz4rQ&u=2015641&o=1211&i=1211_788927599950_788927599950_V
//                 &e=1558411720&sts=5qSFwr1cRj5oFkUvxs2Hww&exp=1558411720"
//                ))
        if (mediaAsset.location == null) return

        val hlsMediaSource = if (mediaAsset.location?.contains(".m3u8", true) == true) {
            HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(mediaAsset.location)))
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory, StreamExtractorFactory())
                .createMediaSource(MediaItem.fromUri(Uri.parse(mediaAsset.location)))
        }

        player.setMediaSource(hlsMediaSource)
        player.prepare()
    }

    override fun isPlaying(): Boolean = playbackState == Player.STATE_READY && playWhenReady
}
