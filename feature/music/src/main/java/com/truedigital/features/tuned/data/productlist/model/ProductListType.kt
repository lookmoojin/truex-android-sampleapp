package com.truedigital.features.tuned.data.productlist.model

enum class ProductListType {
    // Favourites
    FAV_STATIONS,
    FOLLOWED_ARTISTS,
    FAV_ALBUMS,
    FAV_PLAYLISTS,
    FAV_SONGS,
    FAV_VIDEOS,

    // Dynamic Content X Tag Shelf
    TAGGED_ADS,
    TAGGED_ALBUMS,
    TAGGED_ARTISTS,
    TAGGED_PLAYLISTS,
    TAGGED_RADIO,
    TAGGED_STATIONS,
    TAGGED_USER,

    // Dynamic Content
    DISCOVER_BYTAG,
    NEW_RELEASES,
    RECOMMENDED_ARTISTS,
    SUGGESTED_STATIONS,
    TRACKS_PLAYLIST,
    TRENDING_ALBUMS,
    TRENDING_ARTISTS,
    TRENDING_PLAYLISTS,
    TRENDING_STATIONS,

    // Product Shelf
    STATION_FEATURE_ARTIST,
    STATION_SIMILAR,
    ARTIST_VIDEO,
    ARTIST_STATION,
    ARTIST_ALBUM,
    ARTIST_APPEAR_ON,
    ARTIST_SIMILAR,

    // Product Tracks
    ARTIST_TRACKS,
    ARTIST_LATEST,

    CONTENT,

    // Else
    UNSUPPORTED
}
