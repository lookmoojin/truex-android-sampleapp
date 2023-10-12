package com.truedigital.foundation.player

import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DeviceInfo
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.PlayerMessage
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.SeekParameters
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.analytics.AnalyticsCollector
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.audio.AuxEffectInfo
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ShuffleOrder
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.text.CueGroup
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.util.Clock
import com.google.android.exoplayer2.util.PriorityTaskManager
import com.google.android.exoplayer2.video.VideoFrameMetadataListener
import com.google.android.exoplayer2.video.VideoSize
import com.google.android.exoplayer2.video.spherical.CameraMotionListener
import com.truedigital.foundation.player.model.MediaAsset

abstract class BasePlayer : ExoPlayer {
    abstract val supportsSeeking: Boolean
    protected abstract val renderers: Array<MediaCodecRenderer>
    protected abstract val player: ExoPlayer

    var assetId: Int? = null

    var surface: Surface? = null
        set(value) {
            val messages = renderers.filter { renderer ->
                renderer.trackType == C.TRACK_TYPE_VIDEO
            }.map { renderer ->
                player.createMessage(renderer)
                    .setType(Renderer.MSG_SET_VIDEO_OUTPUT)
                    .setPayload(value)
            }
            if (value != null && value != surface) {
                // We're replacing a surface. Block to ensure that it's not accessed after the method returns.
                try {
                    messages.forEach { message -> message.blockUntilDelivered() }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            } else {
                try {
                    messages.forEach { message ->
                        message.send()
                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
            field = value
        }

    abstract fun prepare(mediaAsset: MediaAsset, playWhenReady: Boolean = false)

    fun getPlaybackError() = player.playerError

    // region ExoPlayer implementation
    override fun setVolume(volume: Float) {
        renderers.forEach { renderer ->
            player.createMessage(renderer)
                .setType(Renderer.MSG_SET_VOLUME)
                .setPayload(volume)
                .send()
        }
    }

    override fun getVolume(): Float = 1F

    override fun addListener(listener: Player.Listener) = player.addListener(listener)

    override fun removeListener(listener: Player.Listener) = player.removeListener(listener)

    override fun getPlaybackState(): Int = player.playbackState

    override fun prepare(mediaSource: MediaSource) = player.prepare(mediaSource)

    override fun prepare(mediaSource: MediaSource, resetPosition: Boolean, resetState: Boolean) =
        player.prepare(mediaSource, resetPosition, resetState)

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        player.playWhenReady = playWhenReady
    }

    override fun getPlayWhenReady(): Boolean = player.playWhenReady

    override fun isLoading(): Boolean = player.isLoading

    override fun seekToDefaultPosition() = player.seekToDefaultPosition()

    override fun seekToDefaultPosition(windowIndex: Int) = player.seekToDefaultPosition(windowIndex)

    override fun seekTo(positionMs: Long) = player.seekTo(positionMs)

    override fun seekTo(windowIndex: Int, positionMs: Long) = player.seekTo(windowIndex, positionMs)

    override fun setPlaybackParameters(playbackParameters: PlaybackParameters) {
        player.playbackParameters = playbackParameters
    }

    override fun getPlaybackParameters(): PlaybackParameters = player.playbackParameters

    override fun stop() = player.stop()

    override fun release() = player.release()

    override fun getRendererCount(): Int = player.rendererCount

    override fun getRendererType(index: Int): Int = player.getRendererType(index)

    override fun getCurrentTrackGroups(): TrackGroupArray = player.currentTrackGroups

    override fun getCurrentTrackSelections(): TrackSelectionArray = player.currentTrackSelections

    override fun getCurrentTimeline(): Timeline = player.currentTimeline

    override fun getCurrentManifest(): Any? = player.currentManifest

    override fun getCurrentPeriodIndex(): Int = player.currentPeriodIndex

    override fun getCurrentWindowIndex(): Int = player.currentWindowIndex

    override fun getDuration(): Long = player.duration

    override fun getCurrentPosition(): Long = player.currentPosition

    override fun getBufferedPosition(): Long = player.bufferedPosition

    override fun getBufferedPercentage(): Int = player.bufferedPercentage

    override fun isCurrentWindowDynamic(): Boolean = player.isCurrentWindowDynamic

    override fun isCurrentWindowSeekable(): Boolean = player.isCurrentWindowSeekable

    override fun isCurrentWindowLive(): Boolean = player.isCurrentWindowLive

    override fun isPlayingAd(): Boolean = player.isPlayingAd

    override fun getContentPosition(): Long = player.contentPosition

    override fun getRepeatMode(): Int = player.repeatMode

    override fun getCurrentAdGroupIndex(): Int = player.currentAdGroupIndex

    override fun getCurrentAdIndexInAdGroup(): Int = player.currentAdIndexInAdGroup

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        player.shuffleModeEnabled = shuffleModeEnabled
    }

    override fun setRepeatMode(repeatMode: Int) {
        player.repeatMode = repeatMode
    }

    override fun getNextWindowIndex(): Int = player.nextWindowIndex

    override fun getPlaybackLooper(): Looper = player.playbackLooper

    override fun getShuffleModeEnabled(): Boolean = player.shuffleModeEnabled

    override fun getPreviousWindowIndex(): Int = player.previousWindowIndex

    override fun stop(reset: Boolean) {
        player.stop(reset)
    }

    override fun createMessage(target: PlayerMessage.Target): PlayerMessage =
        player.createMessage(target)

    override fun setSeekParameters(seekParameters: SeekParameters?) {
        player.setSeekParameters(seekParameters)
    }

    override fun getVideoComponent(): ExoPlayer.VideoComponent? = player.videoComponent

    override fun getTextComponent(): ExoPlayer.TextComponent? = player.textComponent

    override fun getContentDuration(): Long = player.contentDuration

    override fun getTotalBufferedDuration(): Long = player.totalBufferedDuration

    override fun previous() = player.previous()

    override fun retry() = player.prepare()

    override fun getContentBufferedPosition(): Long = player.contentBufferedPosition

    override fun hasNext(): Boolean = player.hasNext()

    override fun hasPrevious(): Boolean = player.hasPrevious()

    override fun next() = player.next()

    override fun getAudioComponent(): ExoPlayer.AudioComponent? = player.audioComponent

    override fun getApplicationLooper(): Looper = player.applicationLooper

    override fun getSeekParameters(): SeekParameters = player.seekParameters

    override fun isPlaying(): Boolean = player.isPlaying

    override fun getPlaybackSuppressionReason(): Int = player.playbackSuppressionReason

    override fun setForegroundMode(foregroundMode: Boolean) {
        player.setForegroundMode(foregroundMode)
    }

    override fun prepare() {
        player.prepare()
    }

    override fun getDeviceComponent(): ExoPlayer.DeviceComponent? {
        return player.deviceComponent
    }

    override fun setMediaItems(mediaItems: MutableList<MediaItem>) {
        player.setMediaItems(mediaItems)
    }

    override fun setMediaItems(mediaItems: MutableList<MediaItem>, resetPosition: Boolean) {
        player.setMediaItems(mediaItems, resetPosition)
    }

    override fun setMediaItems(
        mediaItems: MutableList<MediaItem>,
        startWindowIndex: Int,
        startPositionMs: Long
    ) {
        player.setMediaItems(mediaItems, startWindowIndex, startPositionMs)
    }

    override fun setMediaItem(mediaItem: MediaItem) {
        player.setMediaItem(mediaItem)
    }

    override fun setMediaItem(mediaItem: MediaItem, startPositionMs: Long) {
        player.setMediaItem(mediaItem, startPositionMs)
    }

    override fun setMediaItem(mediaItem: MediaItem, resetPosition: Boolean) {
        player.setMediaItem(mediaItem, resetPosition)
    }

    override fun addMediaItem(mediaItem: MediaItem) {
        player.addMediaItem(mediaItem)
    }

    override fun addMediaItem(index: Int, mediaItem: MediaItem) {
        player.addMediaItem(index, mediaItem)
    }

    override fun addMediaItems(mediaItems: MutableList<MediaItem>) {
        player.addMediaItems(mediaItems)
    }

    override fun addMediaItems(index: Int, mediaItems: MutableList<MediaItem>) {
        player.addMediaItems(index, mediaItems)
    }

    override fun moveMediaItem(currentIndex: Int, newIndex: Int) {
        player.moveMediaItem(currentIndex, newIndex)
    }

    override fun moveMediaItems(fromIndex: Int, toIndex: Int, newIndex: Int) {
        player.moveMediaItems(fromIndex, toIndex, newIndex)
    }

    override fun removeMediaItem(index: Int) {
        player.removeMediaItem(index)
    }

    override fun removeMediaItems(fromIndex: Int, toIndex: Int) {
        player.removeMediaItems(fromIndex, toIndex)
    }

    override fun clearMediaItems() {
        player.clearMediaItems()
    }

    override fun getPlayerError(): ExoPlaybackException? {
        return player.playerError
    }

    override fun play() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun getTrackSelector(): TrackSelector? {
        return player.trackSelector
    }

    override fun getCurrentMediaItem(): MediaItem? {
        return player.currentMediaItem
    }

    override fun getMediaItemCount(): Int {
        return player.mediaItemCount
    }

    override fun getMediaItemAt(index: Int): MediaItem {
        return player.getMediaItemAt(index)
    }

    override fun getCurrentLiveOffset(): Long {
        return player.currentLiveOffset
    }

    override fun setMediaSources(mediaSources: MutableList<MediaSource>) {
        return player.setMediaSources(mediaSources)
    }

    override fun setMediaSources(mediaSources: MutableList<MediaSource>, resetPosition: Boolean) {
        return player.setMediaSources(mediaSources, resetPosition)
    }

    override fun setMediaSources(
        mediaSources: MutableList<MediaSource>,
        startWindowIndex: Int,
        startPositionMs: Long
    ) {
        return player.setMediaSources(mediaSources, startWindowIndex, startPositionMs)
    }

    override fun setMediaSource(mediaSource: MediaSource) {
        player.setMediaSource(mediaSource)
    }

    override fun setMediaSource(mediaSource: MediaSource, startPositionMs: Long) {
        player.setMediaSource(mediaSource, startPositionMs)
    }

    override fun setMediaSource(mediaSource: MediaSource, resetPosition: Boolean) {
        player.setMediaSource(mediaSource, resetPosition)
    }

    override fun addMediaSource(mediaSource: MediaSource) {
        player.addMediaSource(mediaSource)
    }

    override fun addMediaSource(index: Int, mediaSource: MediaSource) {
        player.addMediaSource(index, mediaSource)
    }

    override fun addMediaSources(mediaSources: MutableList<MediaSource>) {
        player.addMediaSources(mediaSources)
    }

    override fun addMediaSources(index: Int, mediaSources: MutableList<MediaSource>) {
        player.addMediaSources(index, mediaSources)
    }

    override fun setShuffleOrder(shuffleOrder: ShuffleOrder) {
        player.setShuffleOrder(shuffleOrder)
    }

    override fun setPauseAtEndOfMediaItems(pauseAtEndOfMediaItems: Boolean) {
        player.pauseAtEndOfMediaItems = pauseAtEndOfMediaItems
    }

    override fun getPauseAtEndOfMediaItems(): Boolean {
        return player.pauseAtEndOfMediaItems
    }

    override fun experimentalSetOffloadSchedulingEnabled(offloadSchedulingEnabled: Boolean) {
        player.experimentalSetOffloadSchedulingEnabled(offloadSchedulingEnabled)
    }

    override fun getClock(): Clock = player.clock

    override fun experimentalIsSleepingForOffload(): Boolean {
        return player.experimentalIsSleepingForOffload()
    }

    override fun isCommandAvailable(command: Int): Boolean {
        return player.isCommandAvailable(command)
    }

    override fun canAdvertiseSession(): Boolean = player.canAdvertiseSession()

    override fun getAvailableCommands(): Player.Commands = player.availableCommands

    override fun getSeekBackIncrement(): Long = player.seekBackIncrement

    override fun seekBack() = player.seekBack()

    override fun getSeekForwardIncrement(): Long = player.seekForwardIncrement

    override fun seekForward() = player.seekForward()

    override fun hasPreviousWindow(): Boolean = player.hasPreviousWindow()

    override fun hasPreviousMediaItem(): Boolean = player.hasPreviousMediaItem()

    override fun seekToPreviousWindow() = player.seekToPreviousWindow()

    override fun seekToPreviousMediaItem() = player.seekToPreviousMediaItem()

    override fun getMaxSeekToPreviousPosition(): Long = player.maxSeekToPreviousPosition

    override fun seekToPrevious() = player.seekToPrevious()

    override fun hasNextWindow(): Boolean = player.hasNextWindow()

    override fun hasNextMediaItem(): Boolean = player.hasNextMediaItem()

    override fun seekToNextWindow() = player.seekToNextWindow()

    override fun seekToNextMediaItem() = player.seekToNextMediaItem()

    override fun seekToNext() = player.seekToNext()

    override fun setPlaybackSpeed(speed: Float) = player.setPlaybackSpeed(speed)

    override fun getCurrentTracks(): Tracks = player.currentTracks

    override fun getCurrentCues(): CueGroup = player.currentCues

    override fun getTrackSelectionParameters(): TrackSelectionParameters {
        return player.trackSelectionParameters
    }

    override fun setTrackSelectionParameters(parameters: TrackSelectionParameters) {
        player.trackSelectionParameters = parameters
    }

    override fun getMediaMetadata(): MediaMetadata = player.mediaMetadata

    override fun getPlaylistMetadata(): MediaMetadata = player.playlistMetadata

    override fun setPlaylistMetadata(mediaMetadata: MediaMetadata) {
        player.playlistMetadata = mediaMetadata
    }

    override fun getCurrentMediaItemIndex(): Int = player.currentMediaItemIndex

    override fun getNextMediaItemIndex(): Int = player.nextMediaItemIndex

    override fun getPreviousMediaItemIndex(): Int = player.previousMediaItemIndex

    override fun isCurrentMediaItemDynamic(): Boolean = player.isCurrentMediaItemDynamic

    override fun isCurrentMediaItemLive(): Boolean = player.isCurrentMediaItemLive

    override fun isCurrentMediaItemSeekable(): Boolean = player.isCurrentMediaItemSeekable

    override fun getAudioAttributes(): AudioAttributes = player.audioAttributes

    override fun clearVideoSurface() = player.clearVideoSurface()

    override fun clearVideoSurface(surface: Surface?) = player.clearVideoSurface(surface)

    override fun setVideoSurface(surface: Surface?) = player.setVideoSurface(surface)

    override fun setVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
        player.setVideoSurfaceHolder(surfaceHolder)
    }

    override fun clearVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
        player.clearVideoSurfaceHolder(surfaceHolder)
    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView?) {
        player.setVideoSurfaceView(surfaceView)
    }

    override fun clearVideoSurfaceView(surfaceView: SurfaceView?) {
        player.clearVideoSurfaceView(surfaceView)
    }

    override fun setVideoTextureView(textureView: TextureView?) {
        player.setVideoTextureView(textureView)
    }

    override fun clearVideoTextureView(textureView: TextureView?) {
        player.clearVideoTextureView(textureView)
    }

    override fun getVideoSize(): VideoSize = player.videoSize

    override fun getDeviceInfo(): DeviceInfo = player.deviceInfo

    override fun getDeviceVolume(): Int = player.deviceVolume

    override fun isDeviceMuted(): Boolean = player.isDeviceMuted

    override fun setDeviceVolume(volume: Int) {
        player.deviceVolume = volume
    }

    override fun increaseDeviceVolume() = player.increaseDeviceVolume()

    override fun decreaseDeviceVolume() = player.decreaseDeviceVolume()

    override fun setDeviceMuted(muted: Boolean) {
        player.isDeviceMuted = muted
    }

    override fun addAudioOffloadListener(listener: ExoPlayer.AudioOffloadListener) {
        player.addAudioOffloadListener(listener)
    }

    override fun removeAudioOffloadListener(listener: ExoPlayer.AudioOffloadListener) {
        player.removeAudioOffloadListener(listener)
    }

    override fun getAnalyticsCollector(): AnalyticsCollector = player.analyticsCollector

    override fun addAnalyticsListener(listener: AnalyticsListener) {
        player.addAnalyticsListener(listener)
    }

    override fun removeAnalyticsListener(listener: AnalyticsListener) {
        player.removeAnalyticsListener(listener)
    }

    override fun getRenderer(index: Int): Renderer = player.getRenderer(index)

    override fun setAudioAttributes(audioAttributes: AudioAttributes, handleAudioFocus: Boolean) {
        player.setAudioAttributes(audioAttributes, handleAudioFocus)
    }

    override fun setAudioSessionId(audioSessionId: Int) {
        player.audioSessionId = audioSessionId
    }

    override fun getAudioSessionId(): Int = player.audioSessionId

    override fun setAuxEffectInfo(auxEffectInfo: AuxEffectInfo) {
        player.setAuxEffectInfo(auxEffectInfo)
    }

    override fun clearAuxEffectInfo() = player.clearAuxEffectInfo()

    override fun setSkipSilenceEnabled(skipSilenceEnabled: Boolean) {
        player.skipSilenceEnabled = skipSilenceEnabled
    }

    override fun getSkipSilenceEnabled(): Boolean = player.skipSilenceEnabled

    override fun setVideoScalingMode(videoScalingMode: Int) {
        player.videoScalingMode = videoScalingMode
    }

    override fun getVideoScalingMode(): Int = player.videoScalingMode

    override fun setVideoChangeFrameRateStrategy(videoChangeFrameRateStrategy: Int) {
        player.videoChangeFrameRateStrategy = videoChangeFrameRateStrategy
    }

    override fun getVideoChangeFrameRateStrategy(): Int {
        return player.videoChangeFrameRateStrategy
    }

    override fun setVideoFrameMetadataListener(listener: VideoFrameMetadataListener) {
        player.setVideoFrameMetadataListener(listener)
    }

    override fun clearVideoFrameMetadataListener(listener: VideoFrameMetadataListener) {
        player.clearVideoFrameMetadataListener(listener)
    }

    override fun setCameraMotionListener(listener: CameraMotionListener) {
        player.setCameraMotionListener(listener)
    }

    override fun clearCameraMotionListener(listener: CameraMotionListener) {
        player.clearCameraMotionListener(listener)
    }

    override fun getAudioFormat(): Format? = player.audioFormat

    override fun getVideoFormat(): Format? = player.videoFormat

    override fun getAudioDecoderCounters(): DecoderCounters? = player.audioDecoderCounters

    override fun getVideoDecoderCounters(): DecoderCounters? = player.videoDecoderCounters

    override fun setHandleAudioBecomingNoisy(handleAudioBecomingNoisy: Boolean) {
        player.setHandleAudioBecomingNoisy(handleAudioBecomingNoisy)
    }

    override fun setHandleWakeLock(handleWakeLock: Boolean) {
        player.setHandleWakeLock(handleWakeLock)
    }

    override fun setWakeMode(wakeMode: Int) = player.setWakeMode(wakeMode)

    override fun setPriorityTaskManager(priorityTaskManager: PriorityTaskManager?) {
        player.setPriorityTaskManager(priorityTaskManager)
    }
}
