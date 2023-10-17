package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.features.tuned.data.productlist.model.ProductListType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MapProductListTypeUseCaseTest {

    private lateinit var mapProductListTypeUseCase: MapProductListTypeUseCase

    @BeforeEach
    fun setup() {
        mapProductListTypeUseCase = MapProductListTypeUseCaseImpl()
    }

    @Test
    fun testMapProductListType_null_returnUNSUPPORTED() {
        val result = mapProductListTypeUseCase.execute(null)
        assertEquals(result, ProductListType.UNSUPPORTED)
    }

    @Test
    fun testMapProductListType_isEmpty_returnUNSUPPORTED() {
        val result = mapProductListTypeUseCase.execute("")
        assertEquals(result, ProductListType.UNSUPPORTED)
    }

    @Test
    fun testMapProductListType_stationsByTag_returnTAGGED_STATIONS() {
        val result = mapProductListTypeUseCase.execute("stations_bytag")
        assertEquals(result, ProductListType.TAGGED_STATIONS)
    }

    @Test
    fun testMapProductListType_stationsTrending_returnTRENDING_STATIONS() {
        val result = mapProductListTypeUseCase.execute("stations_trending")
        assertEquals(result, ProductListType.TRENDING_STATIONS)
    }

    @Test
    fun testMapProductListType_stationsSuggested_returnSUGGESTED_STATIONS() {
        val result = mapProductListTypeUseCase.execute("stations_suggested")
        assertEquals(result, ProductListType.SUGGESTED_STATIONS)
    }

    @Test
    fun testMapProductListType_artistsBytag_returnTAGGED_ARTISTS() {
        val result = mapProductListTypeUseCase.execute("artists_bytag")
        assertEquals(result, ProductListType.TAGGED_ARTISTS)
    }

    @Test
    fun testMapProductListType_artists_trending_returnTRENDING_ARTISTS() {
        val result = mapProductListTypeUseCase.execute("artists_trending")
        assertEquals(result, ProductListType.TRENDING_ARTISTS)
    }

    @Test
    fun testMapProductListType_artistsRecommended_returnRECOMMENDED_ARTISTS() {
        val result = mapProductListTypeUseCase.execute("artists_recommended")
        assertEquals(result, ProductListType.RECOMMENDED_ARTISTS)
    }

    @Test
    fun testMapProductListType_albumsByTag_returnTAGGED_ALBUMS() {
        val result = mapProductListTypeUseCase.execute("albums_bytag")
        assertEquals(result, ProductListType.TAGGED_ALBUMS)
    }

    @Test
    fun testMapProductListType_albumsTrending_returnTRENDING_ALBUMS() {
        val result = mapProductListTypeUseCase.execute("albums_trending")
        assertEquals(result, ProductListType.TRENDING_ALBUMS)
    }

    @Test
    fun testMapProductListType_albumsNewReleases_returnNEW_RELEASES() {
        val result = mapProductListTypeUseCase.execute("albums_newreleases")
        assertEquals(result, ProductListType.NEW_RELEASES)
    }

    @Test
    fun testMapProductListType_playlistsByTag_returnTAGGED_PLAYLISTS() {
        val result = mapProductListTypeUseCase.execute("playlists_bytag")
        assertEquals(result, ProductListType.TAGGED_PLAYLISTS)
    }

    @Test
    fun testMapProductListType_playlistsTrending_returnTRENDING_PLAYLISTS() {
        val result = mapProductListTypeUseCase.execute("playlists_trending")
        assertEquals(result, ProductListType.TRENDING_PLAYLISTS)
    }

    @Test
    fun testMapProductListType_playlistTracks_returnTRACKS_PLAYLIST() {
        val result = mapProductListTypeUseCase.execute("playlist_tracks")
        assertEquals(result, ProductListType.TRACKS_PLAYLIST)
    }

    @Test
    fun testMapProductListType_discoverByTag_returnDISCOVER_BYTAG() {
        val result = mapProductListTypeUseCase.execute("discover_bytag")
        assertEquals(result, ProductListType.DISCOVER_BYTAG)
    }

    @Test
    fun testMapProductListType_TypeIsUserByTag_returnTAGGED_USER() {
        val result = mapProductListTypeUseCase.execute("users_bytag")
        assertEquals(result, ProductListType.TAGGED_USER)
    }
}
