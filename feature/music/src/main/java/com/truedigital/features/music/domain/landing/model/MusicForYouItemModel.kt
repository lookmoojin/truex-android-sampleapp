package com.truedigital.features.music.domain.landing.model

import com.truedigital.features.music.constant.MusicShelfType

sealed class MusicForYouItemModel {
    abstract val id: Int

    data class AdsBannerShelfItem(
        val itemId: Int,
        val adsId: String?,
        val mobileSize: String?,
        val tabletSize: String?
    ) : MusicForYouItemModel() {
        override val id: Int = itemId
    }

    data class AlbumShelfItem(
        val albumId: Int,
        val releaseId: Int,
        val coverImage: String?,
        val albumName: String?,
        val artistName: String?
    ) : MusicForYouItemModel() {
        override val id: Int = albumId
    }

    data class ArtistShelfItem(
        val artistId: Int,
        val coverImage: String?,
        val name: String?
    ) : MusicForYouItemModel() {
        override val id: Int = artistId
    }

    data class PlaylistShelfItem(
        val playlistId: Int,
        val coverImage: String?,
        val name: String?,
        val nameEn: String?
    ) : MusicForYouItemModel() {
        override val id: Int = playlistId
    }

    data class TrackPlaylistShelf(
        val index: Int = 0,
        val playlistId: Int = 0,
        val playlistTrackId: Int,
        val trackId: Int,
        val trackIdList: List<Int> = emptyList(),
        val artist: String?,
        val coverImage: String?,
        val name: String?,
        val position: String?
    ) : MusicForYouItemModel() {
        override val id: Int = playlistTrackId
    }

    data class MusicHeroBannerShelfItem(
        val index: Int,
        val heroBannerId: String,
        val coverImage: String?,
        val deeplinkPair: Pair<MusicHeroBannerDeeplinkType, String>,
        val title: String,
        val contentType: String
    ) : MusicForYouItemModel() {
        override val id: Int = index
    }

    data class RadioShelfItem(
        val mediaAssetId: Int,
        val index: Int,
        val radioId: String,
        val contentType: String,
        val description: String,
        val thumbnail: String,
        val titleEn: String,
        val titleTh: String,
        val title: String,
        val viewType: String,
        val shelfType: MusicShelfType,
        val streamUrl: String
    ) : MusicForYouItemModel() {
        override val id: Int = mediaAssetId
    }
}
