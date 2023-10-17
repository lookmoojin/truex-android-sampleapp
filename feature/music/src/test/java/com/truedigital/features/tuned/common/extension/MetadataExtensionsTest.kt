package com.truedigital.features.tuned.common.extension

import android.support.v4.media.MediaMetadataCompat
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.common.extensions.adVastXML
import com.truedigital.features.tuned.common.extensions.albumArtUri
import com.truedigital.features.tuned.common.extensions.artUri
import com.truedigital.features.tuned.common.extensions.artist
import com.truedigital.features.tuned.common.extensions.clickUri
import com.truedigital.features.tuned.common.extensions.duration
import com.truedigital.features.tuned.common.extensions.hasLyrics
import com.truedigital.features.tuned.common.extensions.hasQueue
import com.truedigital.features.tuned.common.extensions.hideDialog
import com.truedigital.features.tuned.common.extensions.isExplicit
import com.truedigital.features.tuned.common.extensions.isFirstTrack
import com.truedigital.features.tuned.common.extensions.isLastTrack
import com.truedigital.features.tuned.common.extensions.isStakkar
import com.truedigital.features.tuned.common.extensions.isVideo
import com.truedigital.features.tuned.common.extensions.mediaId
import com.truedigital.features.tuned.common.extensions.mediaType
import com.truedigital.features.tuned.common.extensions.skipLimitReached
import com.truedigital.features.tuned.common.extensions.sourceId
import com.truedigital.features.tuned.common.extensions.title
import com.truedigital.features.tuned.common.extensions.trackId
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

internal class MetadataExtensionsTest {

    private val mockBuilder = Mockito.mock(MediaMetadataCompat::class.java)

    @Test
    fun testGetSourceId_mediaIdNull_returnSourceIdNull() {
        whenever(mockBuilder.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).thenReturn(null)
        assertEquals(null, mockBuilder.sourceId)
    }

    @Test
    fun testGetSourceId_mediaIdEmpty_returnSourceIdNull() {
        whenever(mockBuilder.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).thenReturn("")
        assertEquals(null, mockBuilder.sourceId)
    }

    @Test
    fun testGetSourceId_split_returnSourceId() {
        whenever(mockBuilder.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).thenReturn("1:2")
        assertEquals(1, mockBuilder.sourceId)
    }

    @Test
    fun testGetTrackId_mediaIdNull_returnTrackIdNull() {
        whenever(mockBuilder.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).thenReturn(null)
        assertEquals(null, mockBuilder.trackId)
    }

    @Test
    fun testGetTrackId_split_returnTrackId() {
        whenever(mockBuilder.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).thenReturn("1:2")
        assertEquals(2, mockBuilder.trackId)
    }

    @Test
    fun testGetTrackId_splitEmpty_returnTrackIdNull() {
        whenever(mockBuilder.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).thenReturn("")
        assertEquals(null, mockBuilder.trackId)
    }

    @Test
    fun testGetMediaId_returnMediaId() {
        whenever(mockBuilder.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).thenReturn("1")
        assertEquals("1", mockBuilder.mediaId)
    }

    @Test
    fun testGetAlbumArtUri_returnAlbumArtUri() {
        whenever(mockBuilder.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)).thenReturn("uri")
        assertEquals("uri", mockBuilder.albumArtUri)
    }

    @Test
    fun testGetArtist_returnArtist() {
        whenever(mockBuilder.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)).thenReturn("artist")
        assertEquals("artist", mockBuilder.artist)
    }

    @Test
    fun testGetTitle_returnTitle() {
        whenever(mockBuilder.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)).thenReturn("title")
        assertEquals("title", mockBuilder.title)
    }

    @Test
    fun testGetClickUri_returnClickUri() {
        whenever(mockBuilder.getString(MusicPlayerController.METADATA_KEY_CLICK_URI)).thenReturn("clickUri")
        assertEquals("clickUri", mockBuilder.clickUri)
    }

    @Test
    fun testGetHideDialog_equal1L_returnTrue() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_HIDE_DIALOG)).thenReturn(1L)
        assertTrue(mockBuilder.hideDialog)
    }

    @Test
    fun testGetHideDialog_notEqual1L_returnFalse() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_HIDE_DIALOG)).thenReturn(2L)
        assertFalse(mockBuilder.hideDialog)
    }

    @Test
    fun testGetHideLyrics_equal1L_returnTrue() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_HAS_LYRICS)).thenReturn(1L)
        assertTrue(mockBuilder.hasLyrics)
    }

    @Test
    fun testGetHideLyrics_notEqual1L_returnFalse() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_HAS_LYRICS)).thenReturn(2L)
        assertFalse(mockBuilder.hasLyrics)
    }

    @Test
    fun testGetSkipLimitReached_equal1L_returnTrue() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_SKIP_LIMIT_REACHED)).thenReturn(
            1L
        )
        assertTrue(mockBuilder.skipLimitReached)
    }

    @Test
    fun testGetSkipLimitReached_notEqual1L_returnFalse() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_SKIP_LIMIT_REACHED)).thenReturn(
            2L
        )
        assertFalse(mockBuilder.skipLimitReached)
    }

    @Test
    fun testGetIsVideo_equal1L_returnTrue() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_IS_VIDEO)).thenReturn(1L)
        assertTrue(mockBuilder.isVideo)
    }

    @Test
    fun testGetIsVideo_notEqual1L_returnFalse() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_IS_VIDEO)).thenReturn(2L)
        assertFalse(mockBuilder.isVideo)
    }

    @Test
    fun testGetIsExplicit_equal1L_returnTrue() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_IS_EXPLICIT)).thenReturn(1L)
        assertTrue(mockBuilder.isExplicit)
    }

    @Test
    fun testGetIsExplicit_notEqual1L_returnFalse() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_IS_EXPLICIT)).thenReturn(2L)
        assertFalse(mockBuilder.isExplicit)
    }

    @Test
    fun testGetAdVastXML_returnAdVastXML() {
        whenever(mockBuilder.getString(MusicPlayerController.METADATA_KEY_AD_VAST_XML)).thenReturn("adVastXML")
        assertEquals("adVastXML", mockBuilder.adVastXML)
    }

    @Test
    fun testGetArtUri_returnArtUri() {
        whenever(mockBuilder.getString(MediaMetadataCompat.METADATA_KEY_ART_URI)).thenReturn("artUri")
        assertEquals("artUri", mockBuilder.artUri)
    }

    @Test
    fun testGetDuration_returnDuration() {
        whenever(mockBuilder.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)).thenReturn(5L)
        assertEquals(5L, mockBuilder.duration)
    }

    @Test
    fun testGetMediaType_matchEnum_returnMediaType() {
        val mockType = MediaType.ARTIST
        whenever(mockBuilder.getString(MusicPlayerController.METADATA_KEY_TYPE)).thenReturn(mockType.name)
        assertEquals(mockType, mockBuilder.mediaType)
    }

    @Test
    fun testGetMediaType_notMatchEnum_returnMediaTypeNull() {
        whenever(mockBuilder.getString(MusicPlayerController.METADATA_KEY_TYPE)).thenReturn("type")
        assertEquals(null, mockBuilder.mediaType)
    }

    @Test
    fun testGetHasQueue_mediaTypeAlbum_returnTrue() {
        whenever(mockBuilder.getString(MusicPlayerController.METADATA_KEY_TYPE)).thenReturn(
            MediaType.ALBUM.name
        )
        assertTrue(mockBuilder.hasQueue)
    }

    @Test
    fun testGetHasQueue_mediaTypeArtistShuffle_returnTrue() {
        whenever(mockBuilder.getString(MusicPlayerController.METADATA_KEY_TYPE)).thenReturn(
            MediaType.ARTIST_SHUFFLE.name
        )
        assertTrue(mockBuilder.hasQueue)
    }

    @Test
    fun testGetHasQueue_mediaTypePlaylist_returnTrue() {
        whenever(mockBuilder.getString(MusicPlayerController.METADATA_KEY_TYPE)).thenReturn(
            MediaType.PLAYLIST.name
        )
        assertTrue(mockBuilder.hasQueue)
    }

    @Test
    fun testGetHasQueue_mediaTypeSongs_returnTrue() {
        whenever(mockBuilder.getString(MusicPlayerController.METADATA_KEY_TYPE)).thenReturn(
            MediaType.SONGS.name
        )
        assertTrue(mockBuilder.hasQueue)
    }

    @Test
    fun testGetHasQueue_mediaTypeArtist_returnFalse() {
        whenever(mockBuilder.getString(MusicPlayerController.METADATA_KEY_TYPE)).thenReturn(
            MediaType.ARTIST.name
        )
        assertFalse(mockBuilder.hasQueue)
    }

    @Test
    fun testGetIsStakkar_mediaTypeAudioStakkar_returnTrue() {
        whenever(mockBuilder.getString(MusicPlayerController.METADATA_KEY_TYPE)).thenReturn(
            MediaType.AUDIO_STAKKAR.name
        )
        assertTrue(mockBuilder.isStakkar)
    }

    @Test
    fun testGetIsStakkar_mediaTypeVideoStakkar_returnTrue() {
        whenever(mockBuilder.getString(MusicPlayerController.METADATA_KEY_TYPE)).thenReturn(
            MediaType.VIDEO_STAKKAR.name
        )
        assertTrue(mockBuilder.isStakkar)
    }

    @Test
    fun testGetIsStakkar_mediaTypeArtist_returnFalse() {
        whenever(mockBuilder.getString(MusicPlayerController.METADATA_KEY_TYPE)).thenReturn(
            MediaType.ARTIST.name
        )
        assertFalse(mockBuilder.isStakkar)
    }

    @Test
    fun testGetIsFirstTrack_value1L_returnTrue() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_IS_FIRST_TRACK))
            .thenReturn(1L)
        assertTrue(mockBuilder.isFirstTrack)
    }

    @Test
    fun testGetIsFirstTrack_value0L_returnFalse() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_IS_FIRST_TRACK))
            .thenReturn(0L)
        assertFalse(mockBuilder.isFirstTrack)
    }

    @Test
    fun testGetIsLastTrack_value1L_returnTrue() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_IS_LAST_TRACK))
            .thenReturn(1L)
        assertTrue(mockBuilder.isLastTrack)
    }

    @Test
    fun testGetIsLastTrack_value0L_returnFalse() {
        whenever(mockBuilder.getLong(MusicPlayerController.METADATA_KEY_IS_LAST_TRACK))
            .thenReturn(0L)
        assertFalse(mockBuilder.isLastTrack)
    }
}
