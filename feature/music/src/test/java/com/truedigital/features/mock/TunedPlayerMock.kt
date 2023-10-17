package com.truedigital.features.mock

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.truedigital.foundation.player.BasePlayer
import com.truedigital.foundation.player.model.MediaAsset

class TunedPlayerMock(
    private val supportsSeekingParam: Boolean,
    private val renderersParam: Array<MediaCodecRenderer>,
    private val playerParam: ExoPlayer
) : BasePlayer() {

    override val supportsSeeking: Boolean
        get() = supportsSeekingParam
    override val renderers: Array<MediaCodecRenderer>
        get() = renderersParam
    override val player: ExoPlayer
        get() = playerParam

    override fun prepare(mediaAsset: MediaAsset, playWhenReady: Boolean) {
        // Do nothing
    }
}
