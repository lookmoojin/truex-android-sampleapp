package com.truedigital.features.music.presentation.player

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.common.extensions.duration
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(InstantTaskExecutorExtension::class)
class MusicServiceConnectionViewModelTest {
    private lateinit var musicServiceConnectionViewModel: MusicServiceConnectionViewModel
    private val mediaSession: MediaSessionCompat = mock()
    private val mediaControllerCompat: MediaControllerCompat = mock()
    private val playbackStateCompat: PlaybackStateCompat = mock()
    private val mediaMetadataCompat: MediaMetadataCompat = mock()

    @BeforeEach
    fun setup() {
        musicServiceConnectionViewModel = MusicServiceConnectionViewModel(mediaSession)
    }

    @Test
    fun testRegister_whenControllerNull() {
        whenever(mediaSession.controller).thenReturn(
            null
        )

        musicServiceConnectionViewModel.register()

        assert(musicServiceConnectionViewModel.onHidePlayer().value == null)
    }

    @Test
    fun testRegister_whenIsPlayerInActiveStateTrue() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller?.metadata).thenReturn(
            null
        )

        musicServiceConnectionViewModel.register()

        assert(musicServiceConnectionViewModel.onHidePlayer().value == Unit)
    }

    @Test
    fun testRegister_whenIsPlayerInActiveStateFalse() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.playbackState).thenReturn(
            playbackStateCompat
        )
        whenever(mediaSession.controller.metadata).thenReturn(
            mediaMetadataCompat
        )

        musicServiceConnectionViewModel.register()

        assertNotNull(musicServiceConnectionViewModel.onUpdateView())
    }

    @Test
    fun testUnRegister_whenControllerNull() {
        whenever(mediaSession.controller).thenReturn(null)

        musicServiceConnectionViewModel.unRegister()

        verify(mediaControllerCompat, times(0)).unregisterCallback(any())
    }

    @Test
    fun testUnRegister_whenControllerNotNull() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)

        musicServiceConnectionViewModel.unRegister()

        verify(mediaControllerCompat, times(1)).unregisterCallback(any())
    }

    @Test
    fun testIsPlayerInActiveState_controllerIsNull_returnTrue() {
        whenever(mediaSession.controller).thenReturn(null)

        val result = musicServiceConnectionViewModel.isPlayerInActiveState()

        assertTrue(result)
    }

    @Test
    fun testIsPlayerInActiveState_controllerIsNotNull_playbackStateIsNull_metadataIsNull_returnTrue() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller?.playbackState).thenReturn(null)
        whenever(mediaSession.controller?.metadata).thenReturn(null)

        val result = musicServiceConnectionViewModel.isPlayerInActiveState()

        assertTrue(result)
    }

    @Test
    fun testIsPlayerInActiveState_controllerIsNotNull_playbackStateIsNull_metadataIsNotNull_returnTrue() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller?.playbackState).thenReturn(null)
        whenever(mediaSession.controller?.metadata).thenReturn(mediaMetadataCompat)

        val result = musicServiceConnectionViewModel.isPlayerInActiveState()

        assertTrue(result)
    }

    @Test
    fun testIsPlayerInActiveState_controllerIsNotNull_playbackStateIsNotNull_metadataIsNull_returnTrue() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller?.playbackState).thenReturn(playbackStateCompat)
        whenever(mediaSession.controller?.metadata).thenReturn(null)

        val result = musicServiceConnectionViewModel.isPlayerInActiveState()

        assertTrue(result)
    }

    @Test
    fun testIsPlayerInActiveState_controllerIsNotNull_playbackStateIsNotNull_metadataIsNotNull_returnFalse() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller?.playbackState).thenReturn(playbackStateCompat)
        whenever(mediaSession.controller?.metadata).thenReturn(mediaMetadataCompat)

        val result = musicServiceConnectionViewModel.isPlayerInActiveState()

        assertFalse(result)
    }

    @Test
    fun handlerPausePlayer_playerIsActiveState_returnTrue() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller?.playbackState).thenReturn(playbackStateCompat)
        whenever(mediaSession.controller?.metadata).thenReturn(mediaMetadataCompat)

        musicServiceConnectionViewModel.handlerPausePlayer()

        assertTrue(musicServiceConnectionViewModel.onCanPausePlayer().getOrAwaitValue())
    }

    @Test
    fun handlerPausePlayer_playerIsNotActiveState_returnFalse() {
        whenever(mediaSession.controller).thenReturn(null)

        musicServiceConnectionViewModel.handlerPausePlayer()

        assertFalse(musicServiceConnectionViewModel.onCanPausePlayer().getOrAwaitValue())
    }

    @Test
    fun testIsPlayerErrorState_returnTrue() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.playbackState).thenReturn(
            playbackStateCompat
        )
        whenever(mediaSession.controller.playbackState.state).thenReturn(
            PlaybackStateCompat.STATE_ERROR
        )
        assertTrue(musicServiceConnectionViewModel.isPlayerErrorState())
    }

    @Test
    fun testIsPlayerErrorState_returnFalse() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.playbackState).thenReturn(
            playbackStateCompat
        )
        whenever(mediaSession.controller.playbackState.state).thenReturn(
            PlaybackStateCompat.STATE_NONE
        )
        assertTrue(!musicServiceConnectionViewModel.isPlayerErrorState())
    }

    @Test
    fun testGetLastThumbUrl_beforeSave_returnDefault() {
        assertEquals("", musicServiceConnectionViewModel.getLastThumbUrl())
    }

    @Test
    fun testGetLastThumbUrl_afterSave_returnValue() {
        musicServiceConnectionViewModel.saveLastThumbUrl("url")
        assertEquals("url", musicServiceConnectionViewModel.getLastThumbUrl())
    }

    @Test
    fun testClearLastThumbUrl_afterSave_returnDefault() {
        musicServiceConnectionViewModel.saveLastThumbUrl("url")
        musicServiceConnectionViewModel.clearLastThumbUrl()
        assertEquals("", musicServiceConnectionViewModel.getLastThumbUrl())
    }

    @Test
    fun getRepeatMode_controllerIsNotNull_returnRepeatMode() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller?.repeatMode).thenReturn(PlaybackStateCompat.REPEAT_MODE_NONE)

        val result = musicServiceConnectionViewModel.getRepeatMode()
        assertEquals(result, PlaybackStateCompat.REPEAT_MODE_NONE)
    }

    @Test
    fun getRepeatMode_controllerIsNull_returnDefaultRepeatMode() {
        whenever(mediaSession.controller).thenReturn(null)

        val result = musicServiceConnectionViewModel.getRepeatMode()
        assertEquals(result, PlaybackStateCompat.REPEAT_MODE_INVALID)
    }

    @Test
    fun getShuffleMode_controllerIsNotNull_returnShuffleMode() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller?.shuffleMode).thenReturn(PlaybackStateCompat.SHUFFLE_MODE_ALL)

        val result = musicServiceConnectionViewModel.getShuffleMode()
        assertEquals(result, PlaybackStateCompat.SHUFFLE_MODE_ALL)
    }

    @Test
    fun getShuffleMode_controllerIsNull_returnDefaultShuffleMode() {
        whenever(mediaSession.controller).thenReturn(null)

        val result = musicServiceConnectionViewModel.getShuffleMode()
        assertEquals(result, PlaybackStateCompat.SHUFFLE_MODE_INVALID)
    }

    @Test
    fun getTrackId_controllerNull_returnTrackIdZero() {
        whenever(mediaSession.controller).thenReturn(null)

        val result = musicServiceConnectionViewModel.getTrackId()
        assertEquals(0, result)
    }

    @Test
    fun getTrackId_metadataNull_returnTrackIdZero() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.metadata).thenReturn(null)

        val result = musicServiceConnectionViewModel.getTrackId()
        assertEquals(0, result)
    }

    @Test
    fun getTrackId_trackIdIsNull_returnTrackIdZero() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.metadata).thenReturn(mediaMetadataCompat)
        whenever(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
            .thenReturn(null)

        val result = musicServiceConnectionViewModel.getTrackId()
        assertEquals(0, result)
    }

    @Test
    fun getTrackId_trackIdIsNotNull_returnTrackId() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.metadata).thenReturn(mediaMetadataCompat)
        whenever(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
            .thenReturn("1")

        val result = musicServiceConnectionViewModel.getTrackId()
        assertEquals(1, result)
    }

    @Test
    fun getPlayPosition_controllerNull_returnNull() {
        whenever(mediaSession.controller).thenReturn(null)

        val result = musicServiceConnectionViewModel.getPlayPosition()
        assertEquals(null, result)
    }

    @Test
    fun getPlayPosition_playbackStateNull_returnNull() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.playbackState).thenReturn(null)

        val result = musicServiceConnectionViewModel.getPlayPosition()
        assertEquals(null, result)
    }

    @Test
    fun getPlayPosition_returnPosition() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.playbackState).thenReturn(playbackStateCompat)
        whenever(playbackStateCompat.position).thenReturn(10L)

        val result = musicServiceConnectionViewModel.getPlayPosition()
        assertEquals(10L, result)
    }

    @Test
    fun isFirstTrack_controllerNull_returnFalse() {
        whenever(mediaSession.controller).thenReturn(null)

        val result = musicServiceConnectionViewModel.isFirstTrack()
        assertEquals(false, result)
    }

    @Test
    fun isFirstTrack_metadataNull_returnFalse() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.metadata).thenReturn(null)

        val result = musicServiceConnectionViewModel.isFirstTrack()
        assertEquals(false, result)
    }

    @Test
    fun isFirstTrack_isFirstTrackNotNull_returnIsFirstTrack() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.metadata).thenReturn(mediaMetadataCompat)
        whenever(mediaMetadataCompat.getLong(MusicPlayerController.METADATA_KEY_IS_FIRST_TRACK))
            .thenReturn(1L)

        val result = musicServiceConnectionViewModel.isFirstTrack()
        assertEquals(true, result)
    }

    @Test
    fun isLastTrack_controllerNull_returnFalse() {
        whenever(mediaSession.controller).thenReturn(null)

        val result = musicServiceConnectionViewModel.isLastTrack()
        assertEquals(false, result)
    }

    @Test
    fun isLastTrack_metadataNull_returnFalse() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.metadata).thenReturn(null)

        val result = musicServiceConnectionViewModel.isLastTrack()
        assertEquals(false, result)
    }

    @Test
    fun isLastTrack_isLastTrackNotNull_returnIsLastTrack() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.metadata).thenReturn(mediaMetadataCompat)
        whenever(mediaMetadataCompat.getLong(MusicPlayerController.METADATA_KEY_IS_LAST_TRACK))
            .thenReturn(1L)

        val result = musicServiceConnectionViewModel.isLastTrack()
        assertEquals(true, result)
    }

    @Test
    fun testNavigateToShare_whenControllerNull() {
        whenever(mediaSession.controller).thenReturn(
            null
        )
        musicServiceConnectionViewModel.navigateToShare()

        assertNull(musicServiceConnectionViewModel.onTrackFAShareSong().value)
        assertNotNull(musicServiceConnectionViewModel.onShareSong().value)
    }

    @Test
    fun testNavigateToShare_whenMetadataNull() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.metadata).thenReturn(
            null
        )
        musicServiceConnectionViewModel.navigateToShare()

        assertNull(musicServiceConnectionViewModel.onTrackFAShareSong().value)
        assertNotNull(musicServiceConnectionViewModel.onShareSong().value)
    }

    @Test
    fun testNavigateToShare_whenMetadataNotNull() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.metadata).thenReturn(
            mediaMetadataCompat
        )
        musicServiceConnectionViewModel.navigateToShare()

        assertNotNull(musicServiceConnectionViewModel.onTrackFAShareSong().value)
        assertNotNull(musicServiceConnectionViewModel.onShareSong().value)
    }

    @Test
    fun onResumePlayer_playerErrorStateIsTrue_metadataIsNotNull_replayState() {
        val mockTrackId = 10L
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.playbackState).thenReturn(playbackStateCompat)
        whenever(mediaSession.controller.playbackState.state).thenReturn(PlaybackStateCompat.STATE_ERROR)
        whenever(mediaSession.controller.metadata).thenReturn(mediaMetadataCompat)
        whenever(mediaSession.controller.metadata.duration).thenReturn(mockTrackId)

        musicServiceConnectionViewModel.onResumePlayer()

        assertEquals(
            musicServiceConnectionViewModel.onReplayState().getOrAwaitValue(),
            mockTrackId
        )
    }

    @Test
    fun onResumePlayer_playerErrorStateIsTrue_metadataIsNull_replayStateWithDefaultData() {
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.playbackState).thenReturn(playbackStateCompat)
        whenever(mediaSession.controller.playbackState.state).thenReturn(PlaybackStateCompat.STATE_ERROR)
        whenever(mediaSession.controller.metadata).thenReturn(null)

        musicServiceConnectionViewModel.onResumePlayer()

        assertEquals(musicServiceConnectionViewModel.onReplayState().getOrAwaitValue(), 0L)
    }

    @Test
    fun onResumePlayer_playerErrorStateIsFalse_replayState() {
        val mockTrackId = 10L
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.playbackState).thenReturn(playbackStateCompat)
        whenever(mediaSession.controller.playbackState.state).thenReturn(PlaybackStateCompat.STATE_PLAYING)
        whenever(mediaSession.controller.metadata).thenReturn(mediaMetadataCompat)
        whenever(mediaSession.controller.metadata.duration).thenReturn(mockTrackId)

        musicServiceConnectionViewModel.onResumePlayer()

        assertNull(musicServiceConnectionViewModel.onReplayState().value)
    }

    @Test
    fun testUpdatePlaybackState_whenStatePlay() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.playbackState).thenReturn(
            playbackStateCompat
        )
        whenever(mediaSession.controller.playbackState.state).thenReturn(
            PlaybackStateCompat.STATE_PLAYING
        )

        musicServiceConnectionViewModel.updatePlaybackState()

        assert(musicServiceConnectionViewModel.onPlayingState().value == Unit)
        assertNotNull(musicServiceConnectionViewModel.onUpdateSeekBar().value)
    }

    @Test
    fun testUpdatePlaybackState_whenStatePaused() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.playbackState).thenReturn(
            playbackStateCompat
        )
        whenever(mediaSession.controller.playbackState.state).thenReturn(
            PlaybackStateCompat.STATE_PAUSED
        )

        musicServiceConnectionViewModel.updatePlaybackState()

        assert(musicServiceConnectionViewModel.onPauseState().value == Unit)
        assertNotNull(musicServiceConnectionViewModel.onUpdateSeekBar().value)
    }

    @Test
    fun testUpdatePlaybackState_whenStateStop() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.playbackState).thenReturn(
            playbackStateCompat
        )
        whenever(mediaSession.controller.playbackState.state).thenReturn(
            PlaybackStateCompat.STATE_STOPPED
        )

        musicServiceConnectionViewModel.updatePlaybackState()

        assert(musicServiceConnectionViewModel.onPauseState().value == Unit)
        assertNotNull(musicServiceConnectionViewModel.onUpdateSeekBar().value)
    }

    @Test
    fun testUpdatePlaybackState_whenStateError() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.playbackState).thenReturn(
            playbackStateCompat
        )
        whenever(mediaSession.controller.playbackState.state).thenReturn(
            PlaybackStateCompat.STATE_ERROR
        )

        musicServiceConnectionViewModel.updatePlaybackState()

        assertNotNull(musicServiceConnectionViewModel.onReplayState().value)
    }

    @Test
    fun testUpdatePlaybackState_whenStateError_hasDuration() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.playbackState).thenReturn(
            playbackStateCompat
        )
        whenever(mediaSession.controller.playbackState.state).thenReturn(
            PlaybackStateCompat.STATE_ERROR
        )
        whenever(mediaSession.controller.metadata).thenReturn(
            mediaMetadataCompat
        )
        whenever(mediaSession.controller.metadata.duration).thenReturn(
            100
        )

        musicServiceConnectionViewModel.updatePlaybackState()

        assertNotNull(musicServiceConnectionViewModel.onReplayState().value)
    }

    @Test
    fun testUpdatePlaybackState_whenStateUnknown() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.playbackState).thenReturn(
            playbackStateCompat
        )
        whenever(mediaSession.controller.playbackState.state).thenReturn(
            PlaybackStateCompat.STATE_NONE
        )

        musicServiceConnectionViewModel.updatePlaybackState()

        assert(musicServiceConnectionViewModel.onReplayState().value == null)
    }

    @Test
    fun testUpdatePlaybackState_whenControllerNull() {
        whenever(mediaSession.controller).thenReturn(
            null
        )

        musicServiceConnectionViewModel.updatePlaybackState()

        assert(musicServiceConnectionViewModel.onReplayState().value == null)
    }

    @Test
    fun testUpdatePlaybackState_whenPlaybackStateNull() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.playbackState).thenReturn(
            null
        )

        musicServiceConnectionViewModel.updatePlaybackState()

        assert(musicServiceConnectionViewModel.onReplayState().value == null)
    }

    @Test
    fun testUpdatePlaybackState_whenPlaybackStateIsLast() {
        whenever(mediaSession.controller).thenReturn(
            mediaControllerCompat
        )
        whenever(mediaSession.controller.playbackState).thenReturn(
            playbackStateCompat
        )
        whenever(mediaSession.controller.playbackState.state).thenReturn(
            -1
        )

        musicServiceConnectionViewModel.updatePlaybackState()

        assert(musicServiceConnectionViewModel.onReplayState().value == null)
    }
}
