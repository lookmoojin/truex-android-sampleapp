package com.truedigital.features.tuned.common.extensions

import android.support.v4.media.MediaMetadataCompat
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController
import timber.log.Timber

val MediaMetadataCompat.sourceId: Int?
    get() = mediaId?.split(":")?.first()?.toIntOrNull()

val MediaMetadataCompat.trackId: Int?
    get() = mediaId?.split(":")?.last()?.toIntOrNull()

val MediaMetadataCompat.mediaId: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

val MediaMetadataCompat.albumArtUri: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)

val MediaMetadataCompat.artist: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

val MediaMetadataCompat.title: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)

val MediaMetadataCompat.clickUri: String?
    get() = getString(MusicPlayerController.METADATA_KEY_CLICK_URI)

val MediaMetadataCompat.hideDialog: Boolean
    get() = getLong(MusicPlayerController.METADATA_KEY_HIDE_DIALOG) == 1L

val MediaMetadataCompat.hasLyrics: Boolean
    get() = getLong(MusicPlayerController.METADATA_KEY_HAS_LYRICS) == 1L

val MediaMetadataCompat.skipLimitReached: Boolean
    get() = getLong(MusicPlayerController.METADATA_KEY_SKIP_LIMIT_REACHED) == 1L

val MediaMetadataCompat.isVideo: Boolean
    get() = getLong(MusicPlayerController.METADATA_KEY_IS_VIDEO) == 1L

val MediaMetadataCompat.isExplicit: Boolean
    get() = getLong(MusicPlayerController.METADATA_KEY_IS_EXPLICIT) == 1L

val MediaMetadataCompat.adVastXML: String?
    get() = getString(MusicPlayerController.METADATA_KEY_AD_VAST_XML)

val MediaMetadataCompat.artUri: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_ART_URI)

val MediaMetadataCompat.duration: Long
    get() = getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

val MediaMetadataCompat.mediaType: MediaType?
    get() {
        return try {
            MediaType.valueOf(getString(MusicPlayerController.METADATA_KEY_TYPE))
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

val MediaMetadataCompat.hasQueue: Boolean
    get() = mediaType == MediaType.ALBUM ||
        mediaType == MediaType.ARTIST_SHUFFLE ||
        mediaType == MediaType.PLAYLIST ||
        mediaType == MediaType.SONGS

val MediaMetadataCompat.isStakkar
    get() = mediaType == MediaType.AUDIO_STAKKAR ||
        mediaType == MediaType.VIDEO_STAKKAR

val MediaMetadataCompat.isFirstTrack: Boolean
    get() = getLong(MusicPlayerController.METADATA_KEY_IS_FIRST_TRACK) == 1L

val MediaMetadataCompat.isLastTrack: Boolean
    get() = getLong(MusicPlayerController.METADATA_KEY_IS_LAST_TRACK) == 1L
