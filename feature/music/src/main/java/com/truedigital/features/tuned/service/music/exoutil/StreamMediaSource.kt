package com.google.android.exoplayer2.source

import android.net.Uri
import android.os.Handler
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.analytics.PlayerId
import com.google.android.exoplayer2.drm.DrmSessionEventListener
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.upstream.Allocator
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.upstream.TransferListener
import com.truedigital.features.tuned.service.music.datasource.PlaybackAssetDataSourceFactory
import com.truedigital.foundation.player.model.MediaAsset
import java.io.IOException

class StreamMediaSource(
    val mediaAsset: MediaAsset,
    val dataSourceFactory: PlaybackAssetDataSourceFactory,
    val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory(),
    val mediaSourceEventListener: MediaSourceEventListener,
    val drmSessionEventListener: DrmSessionEventListener
) : MediaSource, MediaSource.MediaSourceCaller, ProgressiveMediaPeriod.Listener {

    private var period: Timeline.Period = Timeline.Period()
    private var sourceListener: MediaSource.MediaSourceCaller? = null
    private var mediaTimeline: Timeline? = null
    private var timelineHasDuration: Boolean = false
    private val drmSessionManager: DrmSessionManager by lazy {
        DrmSessionManager.getDummyDrmSessionManager()
    }

    override fun prepareSource(
        listener: MediaSource.MediaSourceCaller,
        mediaTransferListener: TransferListener?
    ) {
        customPrepareSource(listener)
    }

    override fun prepareSource(
        listener: MediaSource.MediaSourceCaller,
        mediaTransferListener: TransferListener?,
        playerId: PlayerId
    ) {
        customPrepareSource(listener)
    }

    private fun customPrepareSource(listener: MediaSource.MediaSourceCaller) {
        sourceListener = listener
        mediaTimeline = SinglePeriodTimeline(
            C.TIME_UNSET,
            false,
            false,
            false,
            null,
            this.mediaItem
        )

        listener.onSourceInfoRefreshed(this, mediaTimeline as SinglePeriodTimeline)
    }

    @Throws(IOException::class)
    override fun maybeThrowSourceInfoRefreshError() {
        // Do nothing.
    }

    override fun enable(caller: MediaSource.MediaSourceCaller) {
        Unit
    }

    override fun createPeriod(
        id: MediaSource.MediaPeriodId,
        allocator: Allocator,
        startPositionUs: Long
    ): MediaPeriod {
        val mediaSourceEventDispatcher = MediaSourceEventListener.EventDispatcher().withParameters(
            0, MediaSource.MediaPeriodId(0), 0
        )
        mediaSourceEventDispatcher.addEventListener(Handler(), mediaSourceEventListener)

        val drmSessionEventDispatcher = DrmSessionEventListener.EventDispatcher().withParameters(
            0, MediaSource.MediaPeriodId(0)
        )
        drmSessionEventDispatcher.addEventListener(Handler(), drmSessionEventListener)

        return ProgressiveMediaPeriod(
            Uri.parse(mediaAsset.location),
            dataSourceFactory.createDataSource(mediaAsset),
            BundledExtractorsAdapter(extractorsFactory),
            drmSessionManager,
            drmSessionEventDispatcher,
            DefaultLoadErrorHandlingPolicy(),
            mediaSourceEventDispatcher,
            this,
            allocator,
            null,
            0
        )
    }

    override fun releasePeriod(mediaPeriod: MediaPeriod) {
        (mediaPeriod as ProgressiveMediaPeriod).release()
    }

    override fun disable(caller: MediaSource.MediaSourceCaller) {
        Unit
    }

    override fun releaseSource(listener: MediaSource.MediaSourceCaller) {
        sourceListener = null
    }

    override fun removeEventListener(eventListener: MediaSourceEventListener) {
        Unit
    }

    override fun addEventListener(handler: Handler, eventListener: MediaSourceEventListener) {
        Unit
    }

    // region MediaSource.Listener implementation.

    override fun onSourceInfoRefreshed(source: MediaSource, timeline: Timeline) {
        val newTimelineDurationUs = timeline.getPeriod(0, period).getDurationUs()
        val newTimelineHasDuration = newTimelineDurationUs != C.TIME_UNSET
        if (timelineHasDuration && !newTimelineHasDuration) {
            // Suppress source info changes that would make the duration unknown when it is already known.
            return
        }
        mediaTimeline = timeline
        timelineHasDuration = newTimelineHasDuration
        sourceListener?.onSourceInfoRefreshed(source, timeline)
    }

    override fun addDrmEventListener(handler: Handler, eventListener: DrmSessionEventListener) {
        Unit
    }

    override fun removeDrmEventListener(eventListener: DrmSessionEventListener) {
        Unit
    }

    override fun getMediaItem(): MediaItem {
        return MediaItem.fromUri(Uri.parse(this.mediaAsset.location.orEmpty()))
    }

    override fun onSourceInfoRefreshed(durationUs: Long, isSeekable: Boolean, isLive: Boolean) {
        Unit
    }
}
