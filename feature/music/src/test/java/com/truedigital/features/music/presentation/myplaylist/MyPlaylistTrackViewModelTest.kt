package com.truedigital.features.music.presentation.myplaylist

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(InstantTaskExecutorExtension::class)
internal class MyPlaylistTrackViewModelTest {

    private lateinit var myPlaylistTrackViewModel: MyPlaylistTrackViewModel
    private val mediaSession: MediaSessionCompat = mock()
    private val mediaControllerCompat: MediaControllerCompat = mock()
    private val playbackStateCompat: PlaybackStateCompat = mock()
    private val mediaMetadataCompat: MediaMetadataCompat = mock()

    @BeforeEach
    fun setUp() {
        myPlaylistTrackViewModel = MyPlaylistTrackViewModel(mediaSession)
    }

    @Test
    fun register_mediaSessionControllerIsNotNull_registerCallback() {
        // Given
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)

        // When
        myPlaylistTrackViewModel.register()

        // Then
        verify(mediaControllerCompat, times(1)).registerCallback(any())
    }

    @Test
    fun register_mediaSessionControllerIsNull_doNothing() {
        // Given
        whenever(mediaSession.controller).thenReturn(null)

        // When
        myPlaylistTrackViewModel.register()

        // Then
        verify(mediaControllerCompat, times(0)).registerCallback(any())
    }

    @Test
    fun unRegister_mediaSessionControllerIsNotNull_unRegisterCallback() {
        // Given
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)

        // When
        myPlaylistTrackViewModel.unRegister()

        // Then
        verify(mediaControllerCompat, times(1)).unregisterCallback(any())
    }

    @Test
    fun unRegister_mediaSessionControllerIsNull_doNothing() {
        // Given
        whenever(mediaSession.controller).thenReturn(null)

        // When
        myPlaylistTrackViewModel.unRegister()

        // Then
        verify(mediaControllerCompat, times(0)).unregisterCallback(any())
    }

    @Test
    fun updateCurrentPlayingTrack_playerActiveStateIsTrue_lastPlayingTrackIdNotEqualsCurrentPlayingTrackId_updateTrack() {
        // Given
        val mockTrackId = 10
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.playbackState).thenReturn(playbackStateCompat)
        whenever(mediaSession.controller.metadata).thenReturn(mediaMetadataCompat)
        whenever(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).thenReturn(
            "20:$mockTrackId"
        )

        // When
        myPlaylistTrackViewModel.updateCurrentPlayingTrack()

        // Then
        assertEquals(myPlaylistTrackViewModel.onTrackChange().getOrAwaitValue(), mockTrackId)
    }

    @Test
    fun updateCurrentPlayingTrack_playerActiveStateIsFalse_doNothing() {
        // Given
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.playbackState).thenReturn(null)
        whenever(mediaSession.controller.metadata).thenReturn(null)

        // When
        myPlaylistTrackViewModel.updateCurrentPlayingTrack()

        // Then
        assertNull(myPlaylistTrackViewModel.onTrackChange().value)
    }

    @Test
    fun getCurrentPlayingTrackId_playerActiveStateIsTrue_returnTrackId() {
        // Given
        val mockTrackId = 10
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.playbackState).thenReturn(playbackStateCompat)
        whenever(mediaSession.controller.metadata).thenReturn(mediaMetadataCompat)
        whenever(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).thenReturn(
            "20:$mockTrackId"
        )

        // When
        val result = myPlaylistTrackViewModel.getCurrentPlayingTrackId()

        // Then
        assertEquals(result, mockTrackId)
    }

    @Test
    fun getCurrentPlayingTrackId_playerActiveStateIsFalse_returnNull() {
        // Given
        whenever(mediaSession.controller).thenReturn(mediaControllerCompat)
        whenever(mediaSession.controller.playbackState).thenReturn(null)
        whenever(mediaSession.controller.metadata).thenReturn(null)

        // When
        val result = myPlaylistTrackViewModel.getCurrentPlayingTrackId()

        // Then
        assertNull(result)
    }
}
