/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.truedigital.features.tuned.service.music.exoutil

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.ExoTrackSelection
import com.google.android.exoplayer2.upstream.Allocator
import com.google.android.exoplayer2.upstream.DefaultAllocator

/**
 * @param allocator The [DefaultAllocator] used by the loader.
 */
class TunedLoadControl(
    private val allocator: DefaultAllocator = DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE)
) : LoadControl {

    override fun onPrepared() {
        Unit
    }

    companion object {
        const val BUFFER_FOR_PLAYBACK_US = 2500 * 1000
        const val BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_US = 5000 * 1000
        private const val ILLEGAL_ARGUMENT = "unknown argument"

        /** A default size in bytes for a video buffer.  */
        const val DEFAULT_VIDEO_BUFFER_SIZE = 200 * C.DEFAULT_BUFFER_SEGMENT_SIZE

        /** A default size in bytes for an audio buffer.  */
        const val DEFAULT_AUDIO_BUFFER_SIZE = 54 * C.DEFAULT_BUFFER_SEGMENT_SIZE

        /** A default size in bytes for a text buffer.  */
        const val DEFAULT_TEXT_BUFFER_SIZE = 2 * C.DEFAULT_BUFFER_SEGMENT_SIZE

        /** A default size in bytes for a metadata buffer.  */
        const val DEFAULT_METADATA_BUFFER_SIZE = 2 * C.DEFAULT_BUFFER_SEGMENT_SIZE

        /** A default size in bytes for a camera motion buffer.  */
        const val DEFAULT_CAMERA_MOTION_BUFFER_SIZE = 2 * C.DEFAULT_BUFFER_SEGMENT_SIZE

        /** A default size in bytes for a muxed buffer (e.g. containing video, audio and text).  */
        const val DEFAULT_MUXED_BUFFER_SIZE =
            DEFAULT_VIDEO_BUFFER_SIZE + DEFAULT_AUDIO_BUFFER_SIZE + DEFAULT_TEXT_BUFFER_SIZE
    }

    override fun onTracksSelected(
        renderers: Array<out Renderer>,
        trackGroups: TrackGroupArray,
        trackSelections: Array<out ExoTrackSelection>
    ) {
        val targetBufferSize = renderers.indices
            .sumOf {
                when (renderers[it].trackType) {
                    C.TRACK_TYPE_DEFAULT -> DEFAULT_MUXED_BUFFER_SIZE
                    C.TRACK_TYPE_AUDIO -> DEFAULT_AUDIO_BUFFER_SIZE
                    C.TRACK_TYPE_VIDEO -> DEFAULT_VIDEO_BUFFER_SIZE
                    C.TRACK_TYPE_TEXT -> DEFAULT_TEXT_BUFFER_SIZE
                    C.TRACK_TYPE_METADATA -> DEFAULT_METADATA_BUFFER_SIZE
                    C.TRACK_TYPE_CAMERA_MOTION -> DEFAULT_CAMERA_MOTION_BUFFER_SIZE
                    C.TRACK_TYPE_NONE -> 0
                    else -> throw IllegalArgumentException(ILLEGAL_ARGUMENT)
                }
            }
        allocator.setTargetBufferSize(targetBufferSize)
    }

    override fun onStopped() {
        allocator.reset()
    }

    override fun onReleased() {
        allocator.reset()
    }

    override fun getAllocator(): Allocator = allocator

    override fun shouldStartPlayback(
        bufferedDurationUs: Long,
        playbackSpeed: Float,
        rebuffering: Boolean,
        targetLiveOffsetUs: Long
    ): Boolean {
        val minBufferDurationUs =
            if (rebuffering) BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_US else BUFFER_FOR_PLAYBACK_US
        return minBufferDurationUs <= 0 || bufferedDurationUs >= minBufferDurationUs
    }

    override fun shouldContinueLoading(
        playbackPositionUs: Long,
        bufferedDurationUs: Long,
        playbackSpeed: Float
    ): Boolean = true

    override fun getBackBufferDurationUs(): Long = Long.MAX_VALUE

    override fun retainBackBufferFromKeyframe(): Boolean = false
}
