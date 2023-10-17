package com.truedigital.features.tuned.service.music.player

import android.net.Uri
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ShuffleOrder
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.mock.TunedPlayerMock
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TunedPlayerTest {

    private lateinit var tunedPlayer: TunedPlayerMock
    private val supportsSeeking: Boolean = false
    private val renderers: Array<MediaCodecRenderer> = arrayOf()
    private val player: ExoPlayer = mock()
    private val mediaSource: MediaSource = mock()

    private val mockEventListener = object : Player.Listener {}

    @BeforeEach
    fun setUp() {
        tunedPlayer = TunedPlayerMock(supportsSeeking, renderers, player)

        mockkStatic(Uri::class)
        every { Uri.parse(any()).lastPathSegment } returns "lastPath"
    }

    @Test
    fun addListener_addListenerIsCalled() {
        // When
        tunedPlayer.addListener(mockEventListener)

        // Then
        verify(player, times(1)).addListener(any())
    }

    @Test
    fun removeListener_removeListenerIsCalled() {
        // When
        tunedPlayer.removeListener(mockEventListener)

        // Then
        verify(player, times(1)).removeListener(any())
    }

    @Test
    fun getPlaybackState_returnPlaybackState() {
        // Given
        val mockPlaybackState = 1
        whenever(player.playbackState).thenReturn(mockPlaybackState)

        // When
        val result = tunedPlayer.getPlaybackState()

        // Then
        assertEquals(mockPlaybackState, result)
    }

    @Test
    fun prepare_onlyMediaSource_prepareIsCalled() {
        // When
        tunedPlayer.prepare(mediaSource = mediaSource)

        // Then
        verify(player, times(1)).prepare(any())
    }

    @Test
    fun prepare_multipleParams_prepareIsCalled() {
        // When
        tunedPlayer.prepare(mediaSource = mediaSource, resetPosition = true, resetState = true)

        // Then
        verify(player, times(1)).prepare(any(), any(), any())
    }

    @Test
    fun setPlayWhenReady_playerWhenReadyIsSet() {
        // Given
        tunedPlayer.setPlayWhenReady(false)

        // When
        val result = tunedPlayer.getPlayWhenReady()

        // Then
        assertFalse(result)
    }

    @Test
    fun isLoading_returnIsLoading() {
        // Given
        val mockIsLoading = true
        whenever(player.isLoading).thenReturn(mockIsLoading)

        // When
        val result = tunedPlayer.isLoading()

        // Then
        assertTrue(result)
    }

    @Test
    fun seekToDefaultPosition_seekToDefaultPositionIsCalled() {
        // When
        tunedPlayer.seekToDefaultPosition()

        // Then
        verify(player, times(1)).seekToDefaultPosition()
    }

    @Test
    fun seekToDefaultPosition_hasWindowIndexParam_seekToDefaultPositionIsCalled() {
        // When
        tunedPlayer.seekToDefaultPosition(windowIndex = 10)

        // Then
        verify(player, times(1)).seekToDefaultPosition(any())
    }

    @Test
    fun seekTo_hasPositionMsParam_seekToIsCalled() {
        // When
        tunedPlayer.seekTo(positionMs = 10)

        // Then
        verify(player, times(1)).seekTo(any())
    }

    @Test
    fun seekTo_hasWindowIndexAndPositionMsParam_seekToIsCalled() {
        // When
        tunedPlayer.seekTo(windowIndex = 20, positionMs = 10)

        // Then
        verify(player, times(1)).seekTo(any(), any())
    }

    @Test
    fun setPlaybackParameters_setPlaybackParametersIsCalled() {
        // When
        tunedPlayer.setPlaybackParameters(PlaybackParameters(10f))

        // Then
        verify(player, times(1)).setPlaybackParameters(any())
    }

    @Test
    fun getPlaybackParameters_returnPlaybackParameters() {
        // Given
        val mockPlaybackParameters = PlaybackParameters(20f)
        whenever(player.playbackParameters).thenReturn(mockPlaybackParameters)

        // When
        val result = tunedPlayer.getPlaybackParameters()

        // Then
        assertEquals(mockPlaybackParameters.speed, result.speed)
    }

    @Test
    fun stop_stopIsCalled() {
        // When
        tunedPlayer.stop()

        // Then
        verify(player, times(1)).stop()
    }

    @Test
    fun release_releaseIsCalled() {
        // When
        tunedPlayer.release()

        // Then
        verify(player, times(1)).release()
    }

    @Test
    fun getRendererCount_returnRendererCount() {
        // Given
        val mockRendererCount = 20
        whenever(player.rendererCount).thenReturn(mockRendererCount)

        // When
        val result = tunedPlayer.getRendererCount()

        // Then
        assertEquals(mockRendererCount, result)
    }

    @Test
    fun getRendererType_getRendererTypeIsCalled() {
        // When
        tunedPlayer.getRendererType(10)

        // Then
        verify(player, times(1)).getRendererType(any())
    }

    @Test
    fun getCurrentTrackGroups_returnCurrentTrackGroups() {
        // Given
        val mockTrackGroupArray = TrackGroupArray()
        whenever(player.currentTrackGroups).thenReturn(mockTrackGroupArray)

        // When
        val result = tunedPlayer.getCurrentTrackGroups()

        // Then
        assertEquals(mockTrackGroupArray, result)
    }

    @Test
    fun getCurrentTrackSelections_returnCurrentTrackSelection() {
        // Given
        val mockTrackSelectionArray = TrackSelectionArray()
        whenever(player.currentTrackSelections).thenReturn(mockTrackSelectionArray)

        // When
        val result = tunedPlayer.getCurrentTrackSelections()

        // Then
        assertEquals(mockTrackSelectionArray, result)
    }

    @Test
    fun getCurrentManifest_returnCurrentManifest() {
        // Given
        val mockCurrentManifest = Any()
        whenever(player.currentManifest).thenReturn(mockCurrentManifest)

        // When
        val result = tunedPlayer.getCurrentManifest()

        // Then
        assertEquals(mockCurrentManifest, result)
    }

    @Test
    fun getCurrentPeriodIndex_returnGetCurrentPeriodIndex() {
        // Given
        val mockCurrentPeriodIndex = 20
        whenever(player.currentPeriodIndex).thenReturn(mockCurrentPeriodIndex)

        // When
        val result = tunedPlayer.getCurrentPeriodIndex()

        // Then
        assertEquals(mockCurrentPeriodIndex, result)
    }

    @Test
    fun getCurrentPeriodIndex_returnCurrentWindowIndex() {
        // Given
        val mockCurrentWindowIndex = 20
        whenever(player.currentWindowIndex).thenReturn(mockCurrentWindowIndex)

        // When
        val result = tunedPlayer.getCurrentWindowIndex()

        // Then
        assertEquals(mockCurrentWindowIndex, result)
    }

    @Test
    fun getDuration_returnGetDuration() {
        // Given
        val mockGetDuration = 20L
        whenever(player.duration).thenReturn(mockGetDuration)

        // When
        val result = tunedPlayer.getDuration()

        // Then
        assertEquals(mockGetDuration, result)
    }

    @Test
    fun getCurrentPosition_returnGetCurrentPosition() {
        // Given
        val mockGetCurrentPosition = 20L
        whenever(player.currentPosition).thenReturn(mockGetCurrentPosition)

        // When
        val result = tunedPlayer.getCurrentPosition()

        // Then
        assertEquals(mockGetCurrentPosition, result)
    }

    @Test
    fun getBufferedPosition_returnGetBufferedPosition() {
        // Given
        val mockGetBufferedPosition = 20L
        whenever(player.bufferedPosition).thenReturn(mockGetBufferedPosition)

        // When
        val result = tunedPlayer.getBufferedPosition()

        // Then
        assertEquals(mockGetBufferedPosition, result)
    }

    @Test
    fun getBufferedPercentage_returnGetBufferedPercentage() {
        // Given
        val mockGetBufferedPercentage = 20
        whenever(player.bufferedPercentage).thenReturn(mockGetBufferedPercentage)

        // When
        val result = tunedPlayer.getBufferedPercentage()

        // Then
        assertEquals(mockGetBufferedPercentage, result)
    }

    @Test
    fun isCurrentWindowDynamic_returnIsCurrentWindowDynamic() {
        // Given
        whenever(player.isCurrentWindowDynamic).thenReturn(true)

        // When
        val result = tunedPlayer.isCurrentWindowDynamic()

        // Then
        assertTrue(result)
    }

    @Test
    fun isCurrentWindowSeekable_returnIsCurrentWindowSeekable() {
        // Given
        whenever(player.isCurrentWindowSeekable).thenReturn(true)

        // When
        val result = tunedPlayer.isCurrentWindowSeekable()

        // Then
        assertTrue(result)
    }

    @Test
    fun isCurrentWindowLive_returnIsCurrentWindowLive() {
        // Given
        whenever(player.isCurrentWindowLive).thenReturn(true)

        // When
        val result = tunedPlayer.isCurrentWindowLive()

        // Then
        assertTrue(result)
    }

    @Test
    fun isPlayingAd_returnIsPlayingAd() {
        // Given
        whenever(player.isPlayingAd).thenReturn(true)

        // When
        val result = tunedPlayer.isPlayingAd()

        // Then
        assertTrue(result)
    }

    @Test
    fun getContentPosition_returnContentPosition() {
        // Given
        val mockContentPosition = 2L
        whenever(player.contentPosition).thenReturn(mockContentPosition)

        // When
        val result = tunedPlayer.getContentPosition()

        // Then
        assertEquals(mockContentPosition, result)
    }

    @Test
    fun getRepeatMode_returnRepeatMode() {
        // Given
        val mockRepeatMode = 2
        whenever(player.repeatMode).thenReturn(mockRepeatMode)

        // When
        val result = tunedPlayer.getRepeatMode()

        // Then
        assertEquals(mockRepeatMode, result)
    }

    @Test
    fun getCurrentAdGroupIndex_returnCurrentAdGroupIndex() {
        // Given
        val mockCurrentAdGroupIndex = 20
        whenever(player.currentAdGroupIndex).thenReturn(mockCurrentAdGroupIndex)

        // When
        val result = tunedPlayer.getCurrentAdGroupIndex()

        // Then
        assertEquals(mockCurrentAdGroupIndex, result)
    }

    @Test
    fun getCurrentAdIndexInAdGroup_returnCurrentAdIndexInAdGroup() {
        // Given
        val mockCurrentAdIndexInAdGroup = 20
        whenever(player.currentAdIndexInAdGroup).thenReturn(mockCurrentAdIndexInAdGroup)

        // When
        val result = tunedPlayer.getCurrentAdIndexInAdGroup()

        // Then
        assertEquals(mockCurrentAdIndexInAdGroup, result)
    }

    @Test
    fun setShuffleModeEnabled_setShuffleModeEnabledIsCalled() {
        // When
        tunedPlayer.setShuffleModeEnabled(true)

        // Then
        verify(player, times(1)).setShuffleModeEnabled(any())
    }

    @Test
    fun setRepeatMode_setRepeatModeIsCalled() {
        // When
        tunedPlayer.setRepeatMode(10)

        // Then
        verify(player, times(1)).setRepeatMode(any())
    }

    @Test
    fun getNextWindowIndex_returnNextWindowIndex() {
        // Given
        val mockNextWindowIndex = 20
        whenever(player.nextWindowIndex).thenReturn(mockNextWindowIndex)

        // When
        val result = tunedPlayer.getNextWindowIndex()

        // Then
        assertEquals(mockNextWindowIndex, result)
    }

    @Test
    fun getShuffleModeEnabled_returnShuffleModeEnabled() {
        // Given
        whenever(player.shuffleModeEnabled).thenReturn(true)

        // When
        val result = tunedPlayer.getShuffleModeEnabled()

        // Then
        assertTrue(result)
    }

    @Test
    fun getPreviousWindowIndex_returnPreviousWindowIndex() {
        // Given
        val mockPreviousWindowIndex = 20
        whenever(player.previousWindowIndex).thenReturn(mockPreviousWindowIndex)

        // When
        val result = tunedPlayer.getPreviousWindowIndex()

        // Then
        assertEquals(mockPreviousWindowIndex, result)
    }

    @Test
    fun stop_resetIsTrue_stopIsCalled() {
        // When
        tunedPlayer.stop(true)

        // Then
        verify(player, times(1)).stop(any())
    }

    @Test
    fun setSeekParameters_setSeekParametersIsCalled() {
        // When
        tunedPlayer.setSeekParameters(SeekParameters.DEFAULT)

        // Then
        verify(player, times(1)).setSeekParameters(any())
    }

    @Test
    fun getVideoComponent_returnVideoComponent() {
        // Given
        val mockVideoComponent: ExoPlayer.VideoComponent = mock()
        whenever(player.videoComponent).thenReturn(mockVideoComponent)

        // When
        val result = tunedPlayer.getVideoComponent()

        // Then
        assertEquals(mockVideoComponent, result)
    }

    @Test
    fun getTextComponent_returnTextComponent() {
        // Given
        val mockTextComponent: ExoPlayer.TextComponent = mock()
        whenever(player.textComponent).thenReturn(mockTextComponent)

        // When
        val result = tunedPlayer.getTextComponent()

        // Then
        assertEquals(mockTextComponent, result)
    }

    @Test
    fun getContentDuration_returnContentDuration() {
        // Given
        val mockContentDuration = 10L
        whenever(player.contentDuration).thenReturn(mockContentDuration)

        // When
        val result = tunedPlayer.getContentDuration()

        // Then
        assertEquals(mockContentDuration, result)
    }

    @Test
    fun getTotalBufferedDuration_returnTotalBufferedDuration() {
        // Given
        val mockTotalBufferedDuration = 10L
        whenever(player.totalBufferedDuration).thenReturn(mockTotalBufferedDuration)

        // When
        val result = tunedPlayer.getTotalBufferedDuration()

        // Then
        assertEquals(mockTotalBufferedDuration, result)
    }

    @Test
    fun getPlaybackError_returnPlaybackError() {
        // Given
        val mockPlaybackError = ExoPlaybackException.createForRemote("Error")
        whenever(player.playerError).thenReturn(mockPlaybackError)

        // When
        val result = tunedPlayer.getPlaybackError()

        // Then
        assertEquals(mockPlaybackError, result)
    }

    @Test
    fun previous_previousIsCalled() {
        // When
        tunedPlayer.previous()

        // Then
        verify(player, times(1)).previous()
    }

    @Test
    fun retry_prepareIsCalled() {
        // When
        tunedPlayer.retry()

        // Then
        verify(player, times(1)).prepare()
    }

    @Test
    fun getContentBufferedPosition_returnContentBufferedPosition() {
        // Given
        val mockContentBufferedPosition = 20L
        whenever(player.contentBufferedPosition).thenReturn(mockContentBufferedPosition)

        // When
        val result = tunedPlayer.getContentBufferedPosition()

        // Then
        assertEquals(mockContentBufferedPosition, result)
    }

    @Test
    fun hasNext_returnNextStatus() {
        // Given
        whenever(player.hasNext()).thenReturn(true)

        // When
        val result = tunedPlayer.hasNext()

        // Then
        assertTrue(result)
    }

    @Test
    fun hasPrevious_returnPreviousStatus() {
        // Given
        whenever(player.hasPrevious()).thenReturn(true)

        // When
        val result = tunedPlayer.hasPrevious()

        // Then
        assertTrue(result)
    }

    @Test
    fun next_nextIsCalled() {
        // When
        tunedPlayer.next()

        // Then
        verify(player, times(1)).next()
    }

    @Test
    fun getAudioComponent_returnAudioComponent() {
        // Given
        val mockAudioComponent: ExoPlayer.AudioComponent = mock()
        whenever(player.audioComponent).thenReturn(mockAudioComponent)

        // When
        val result = tunedPlayer.getAudioComponent()

        // Then
        assertEquals(mockAudioComponent, result)
    }

    @Test
    fun isPlaying_returnPlayingStatus() {
        // Given
        whenever(player.isPlaying).thenReturn(true)

        // When
        val result = tunedPlayer.isPlaying()

        // Then
        assertTrue(result)
    }

    @Test
    fun getPlaybackSuppressionReason_returnPlaybackSuppressionReason() {
        // Given
        val mockPlaybackSuppressionReason = 10
        whenever(player.playbackSuppressionReason).thenReturn(mockPlaybackSuppressionReason)

        // When
        val result = tunedPlayer.getPlaybackSuppressionReason()

        // Then
        assertEquals(mockPlaybackSuppressionReason, result)
    }

    @Test
    fun setForegroundMode_setForegroundModeIsCalled() {
        // When
        tunedPlayer.setForegroundMode(true)

        // Then
        verify(player, times(1)).setForegroundMode(any())
    }

    @Test
    fun prepare_prepareIsCalled() {
        // When
        tunedPlayer.prepare()

        // Then
        verify(player, times(1)).prepare()
    }

    @Test
    fun getDeviceComponent_returnDeviceComponent() {
        // Given
        val mockDeviceComponent: ExoPlayer.DeviceComponent = mock()
        whenever(player.deviceComponent).thenReturn(mockDeviceComponent)

        // When
        val result = tunedPlayer.getDeviceComponent()

        // Then
        assertEquals(mockDeviceComponent, result)
    }

    @Test
    fun setMediaItems_hasOneParam_setMediaItemsIsCalled() {
        // When
        tunedPlayer.setMediaItems(mediaItems = mutableListOf())

        // Then
        verify(player, times(1)).setMediaItems(any())
    }

    @Test
    fun setMediaItems_hasTwoParams_setMediaItemsIsCalled() {
        // When
        tunedPlayer.setMediaItems(mediaItems = mutableListOf(), resetPosition = true)

        // Then
        verify(player, times(1)).setMediaItems(any(), any())
    }

    @Test
    fun setMediaItems_hasThreeParams_setMediaItemsIsCalled() {
        // When
        tunedPlayer.setMediaItems(
            mediaItems = mutableListOf(),
            startWindowIndex = 1,
            startPositionMs = 1L
        )

        // Then
        verify(player, times(1)).setMediaItems(any(), any(), any())
    }

    @Test
    fun setMediaItem_paramIsMediaItem_setMediaItemIsCalled() {
        // Given
        val mockMediaItem = MediaItem.fromUri(Uri.parse("uriString"))

        // When
        tunedPlayer.setMediaItem(mediaItem = mockMediaItem)

        // Then
        verify(player, times(1)).setMediaItem(any())
    }

    @Test
    fun setMediaItem_paramsAreMediaItemAndStartPositionMs_setMediaItemIsCalled() {
        // Given
        val mockMediaItem = MediaItem.fromUri(Uri.parse("uriString"))
        val startPositionMs = 10L

        // When
        tunedPlayer.setMediaItem(mediaItem = mockMediaItem, startPositionMs = startPositionMs)

        // Then
        verify(player, times(1)).setMediaItem(mockMediaItem, startPositionMs)
    }

    @Test
    fun setMediaItem_paramsAreMediaItemAndResetPosition_setMediaItemIsCalled() {
        // Given
        val mockMediaItem = MediaItem.fromUri(Uri.parse("uriString"))
        val resetPosition = true

        // When
        tunedPlayer.setMediaItem(mediaItem = mockMediaItem, resetPosition = resetPosition)

        // Then
        verify(player, times(1)).setMediaItem(mockMediaItem, resetPosition)
    }

    @Test
    fun addMediaItem_paramIsMediaItem_addMediaItemIsCalled() {
        // Given
        val mockMediaItem = MediaItem.fromUri(Uri.parse("uriString"))

        // When
        tunedPlayer.addMediaItem(mediaItem = mockMediaItem)

        // Then
        verify(player, times(1)).addMediaItem(any())
    }

    @Test
    fun addMediaItem_paramsAreIndexAndMediaItem_addMediaItemIsCalled() {
        // Given
        val mockMediaItem = MediaItem.fromUri(Uri.parse("uriString"))

        // When
        tunedPlayer.addMediaItem(index = 10, mediaItem = mockMediaItem)

        // Then
        verify(player, times(1)).addMediaItem(any(), any())
    }

    @Test
    fun addMediaItem_paramIsMediaItems_addMediaItemIsCalled() {
        // Given
        val mockMediaItem = MediaItem.fromUri(Uri.parse("uriString"))

        // When
        tunedPlayer.addMediaItems(mediaItems = mutableListOf(mockMediaItem))

        // Then
        verify(player, times(1)).addMediaItems(any())
    }

    @Test
    fun addMediaItem_paramsAreIndexAndMediaItems_addMediaItemIsCalled() {
        // Given
        val mockMediaItem = MediaItem.fromUri(Uri.parse("uriString"))

        // When
        tunedPlayer.addMediaItems(index = 10, mediaItems = mutableListOf(mockMediaItem))

        // Then
        verify(player, times(1)).addMediaItems(any(), any())
    }

    @Test
    fun moveMediaItem_moveMediaItemIsCalled() {
        // When
        tunedPlayer.moveMediaItem(currentIndex = 10, newIndex = 20)

        // Then
        verify(player, times(1)).moveMediaItem(any(), any())
    }

    @Test
    fun moveMediaItems_moveMediaItemIsCalled() {
        // When
        tunedPlayer.moveMediaItems(fromIndex = 10, toIndex = 20, newIndex = 30)

        // Then
        verify(player, times(1)).moveMediaItems(any(), any(), any())
    }

    @Test
    fun removeMediaItem_removeMediaItemIsCalled() {
        // When
        tunedPlayer.removeMediaItem(10)

        // Then
        verify(player, times(1)).removeMediaItem(any())
    }

    @Test
    fun removeMediaItems_removeMediaItemsIsCalled() {
        // When
        tunedPlayer.removeMediaItems(10, 20)

        // Then
        verify(player, times(1)).removeMediaItems(any(), any())
    }

    @Test
    fun clearMediaItems_clearMediaItemsIsCalled() {
        // When
        tunedPlayer.clearMediaItems()

        // Then
        verify(player, times(1)).clearMediaItems()
    }

    @Test
    fun play_playIsCalled() {
        // When
        tunedPlayer.play()

        // Then
        verify(player, times(1)).play()
    }

    @Test
    fun pause_pauseIsCalled() {
        // When
        tunedPlayer.pause()

        // Then
        verify(player, times(1)).pause()
    }

    @Test
    fun getCurrentMediaItem_returnCurrentMediaItem() {
        // Given
        val mockMediaItem = MediaItem.fromUri(Uri.parse("uriString"))
        whenever(player.currentMediaItem).thenReturn(mockMediaItem)

        // When
        val result = tunedPlayer.getCurrentMediaItem()

        // Then
        assertEquals(mockMediaItem, result)
    }

    @Test
    fun getMediaItemCount_returnMediaItemCount() {
        // Given
        val mockMediaItemCount = 30
        whenever(player.mediaItemCount).thenReturn(mockMediaItemCount)

        // When
        val result = tunedPlayer.getMediaItemCount()

        // Then
        assertEquals(mockMediaItemCount, result)
    }

    @Test
    fun getMediaItemAt_getMediaItemAtIsCalled() {
        // Given
        val mockMediaItem = MediaItem.fromUri(Uri.parse("uriString"))
        whenever(player.getMediaItemAt(any())).thenReturn(mockMediaItem)

        // When
        val result = tunedPlayer.getMediaItemAt(10)

        // Then
        assertEquals(mockMediaItem, result)
        verify(player, times(1)).getMediaItemAt(any())
    }

    @Test
    fun getCurrentLiveOffset_returnCurrentLiveOffset() {
        // Given
        val mockCurrentLiveOffset = 30L
        whenever(player.currentLiveOffset).thenReturn(mockCurrentLiveOffset)

        // When
        val result = tunedPlayer.getCurrentLiveOffset()

        // Then
        assertEquals(mockCurrentLiveOffset, result)
    }

    @Test
    fun setMediaSources_paramIsMediaSources_setMediaSourcesIsCalled() {
        // When
        tunedPlayer.setMediaSources(mediaSources = mutableListOf())

        // Then
        verify(player, times(1)).setMediaSources(any())
    }

    @Test
    fun setMediaSources_paramsAreMediaSourcesAndResetPosition_setMediaSourcesIsCalled() {
        // When
        tunedPlayer.setMediaSources(mediaSources = mutableListOf(), resetPosition = true)

        // Then
        verify(player, times(1)).setMediaSources(any(), any())
    }

    @Test
    fun setMediaSources_paramsAreMediaSourcesAndStartWindowIndexAndStartPositionMs_setMediaSourcesIsCalled() {
        // When
        tunedPlayer.setMediaSources(
            mediaSources = mutableListOf(),
            startWindowIndex = 10,
            startPositionMs = 20L
        )

        // Then
        verify(player, times(1)).setMediaSources(any(), any(), any())
    }

    @Test
    fun setMediaSources_paramIsMediaSource_setMediaSourcesIsCalled() {
        // When
        tunedPlayer.setMediaSource(mediaSource = mediaSource)

        // Then
        verify(player, times(1)).setMediaSource(any())
    }

    @Test
    fun setMediaSources_paramsAreMediaSourceAndStartPositionMs_setMediaSourcesIsCalled() {
        // Given
        val mockStartPositionMs = 10L

        // When
        tunedPlayer.setMediaSource(mediaSource = mediaSource, startPositionMs = mockStartPositionMs)

        // Then
        verify(player, times(1)).setMediaSource(mediaSource, mockStartPositionMs)
    }

    @Test
    fun setMediaSources_paramsAreMediaSourceAndResetPosition_setMediaSourcesIsCalled() {
        // Given
        val mockResetPosition = true

        // When
        tunedPlayer.setMediaSource(mediaSource = mediaSource, resetPosition = mockResetPosition)

        // Then
        verify(player, times(1)).setMediaSource(mediaSource, mockResetPosition)
    }

    @Test
    fun addMediaSource_paramIsMediaSource_addMediaSourceIsCalled() {
        // When
        tunedPlayer.addMediaSource(mediaSource = mediaSource)

        // Then
        verify(player, times(1)).addMediaSource(any())
    }

    @Test
    fun addMediaSource_paramsAreIndexAndMediaSource_addMediaSourceIsCalled() {
        // Given
        val mockIndex = 10

        // When
        tunedPlayer.addMediaSource(index = mockIndex, mediaSource = mediaSource)

        // Then
        verify(player, times(1)).addMediaSource(mockIndex, mediaSource)
    }

    @Test
    fun addMediaSources_paramIsMediaSources_addMediaSourceIsCalled() {
        // Given
        val mockMediaSources = mutableListOf(mediaSource)

        // When
        tunedPlayer.addMediaSources(mockMediaSources)

        // Then
        verify(player, times(1)).addMediaSources(mockMediaSources)
    }

    @Test
    fun addMediaSources_paramsAreIndexAndMediaSources_addMediaSourceIsCalled() {
        // Given
        val mockMediaSources = mutableListOf(mediaSource)
        val mockIndex = 10

        // When
        tunedPlayer.addMediaSources(mockIndex, mockMediaSources)

        // Then
        verify(player, times(1)).addMediaSources(mockIndex, mockMediaSources)
    }

    @Test
    fun setShuffleOrder_setShuffleOrderIsCalled() {
        // Given
        val mockShuffleOrder: ShuffleOrder = mock()

        // When
        tunedPlayer.setShuffleOrder(mockShuffleOrder)

        // Then
        verify(player, times(1)).setShuffleOrder(any())
    }

    @Test
    fun setPauseAtEndOfMediaItems_ssetPauseAtEndOfMediaItemsIsCalled() {
        // When
        tunedPlayer.setPauseAtEndOfMediaItems(true)

        // Then
        verify(player, times(1)).setPauseAtEndOfMediaItems(any())
    }

    @Test
    fun getPauseAtEndOfMediaItems_returnPauseAtEndOfMediaItems() {
        // Given
        whenever(player.pauseAtEndOfMediaItems).thenReturn(true)

        // When
        val result = tunedPlayer.getPauseAtEndOfMediaItems()

        // Then
        assertTrue(result)
    }

    @Test
    fun experimentalSetOffloadSchedulingEnabled_experimentalSetOffloadSchedulingEnabledIsCalled() {
        // When
        tunedPlayer.experimentalSetOffloadSchedulingEnabled(true)

        // Then
        verify(player, times(1)).experimentalSetOffloadSchedulingEnabled(any())
    }
}
