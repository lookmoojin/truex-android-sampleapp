package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.features.tuned.data.productlist.model.ProductListType
import javax.inject.Inject

interface MapProductListTypeUseCase {
    fun execute(type: String?): ProductListType
}

class MapProductListTypeUseCaseImpl @Inject constructor() : MapProductListTypeUseCase {

    override fun execute(type: String?): ProductListType {
        return when (type) {
            "stations_bytag" -> ProductListType.TAGGED_STATIONS
            "stations_trending" -> ProductListType.TRENDING_STATIONS
            "stations_suggested" -> ProductListType.SUGGESTED_STATIONS
            "artists_bytag" -> ProductListType.TAGGED_ARTISTS
            "artists_trending" -> ProductListType.TRENDING_ARTISTS
            "artists_recommended" -> ProductListType.RECOMMENDED_ARTISTS
            "albums_bytag" -> ProductListType.TAGGED_ALBUMS
            "albums_trending" -> ProductListType.TRENDING_ALBUMS
            "albums_newreleases" -> ProductListType.NEW_RELEASES
            "playlists_bytag" -> ProductListType.TAGGED_PLAYLISTS
            "playlists_trending" -> ProductListType.TRENDING_PLAYLISTS
            "playlist_tracks" -> ProductListType.TRACKS_PLAYLIST
            "discover_bytag" -> ProductListType.DISCOVER_BYTAG
            "users_bytag" -> ProductListType.TAGGED_USER
            else -> ProductListType.UNSUPPORTED
        }
    }
}
