package com.truedigital.features.tuned.domain.facade.productlist

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.data.track.model.Track
import io.reactivex.Single

interface ProductListFacade {
    fun loadFavouriteStations(offset: Int, count: Int): Single<List<Station>>
    fun loadFollowedArtists(offset: Int, count: Int): Single<List<Artist>>
    fun loadFavouriteAlbums(offset: Int, count: Int): Single<List<Release>>
    fun loadFavouritePlaylists(offset: Int, count: Int): Single<List<Playlist>>
    fun loadFavouriteSongs(offset: Int, count: Int): Single<List<Track>>
    fun loadFavouriteVideos(offset: Int, count: Int): Single<List<Track>>

    fun loadStationsWithTag(tag: String, offset: Int, count: Int): Single<List<Station>>
    fun loadArtistsWithTag(tag: String, offset: Int, count: Int): Single<List<Artist>>
    fun loadAlbumsWithTag(tag: String, offset: Int, count: Int): Single<List<Album>>
    fun loadAlbumsByTagGroup(tag: String, offset: Int, count: Int): Single<List<Album>>
    fun loadPlaylistsWithTag(tag: String, offset: Int, count: Int): Single<List<Playlist>>

    fun loadTrendingStations(): Single<List<Station>>
    fun loadTrendingArtists(offset: Int, count: Int): Single<List<Artist>>
    fun loadTrendingAlbums(offset: Int, count: Int): Single<List<Album>>
    fun loadTrendingPlaylists(offset: Int, count: Int): Single<List<Playlist>>

    fun loadSuggestedStations(offset: Int, count: Int): Single<List<Station>>
    fun loadRecommendedArtists(offset: Int, count: Int): Single<List<Artist>>
    fun loadNewReleases(offset: Int, count: Int): Single<List<Album>>
    fun loadMultipleTags(tags: String): Single<List<Tag>>

    fun loadStationFeaturedArtists(stationId: Int): Single<List<Artist>>
    fun loadStationSimilar(stationId: Int): Single<List<Station>>

    fun loadArtistPopularSongs(artistId: Int): Single<List<Track>>
    fun loadArtistLatestSongs(artistId: Int): Single<List<Track>>
    fun loadArtistVideoAppearsIn(
        artistId: Int,
        offset: Int,
        count: Int,
        sortType: String? = null
    ): Single<List<Track>>

    fun loadArtistAppearsIn(artistId: Int): Single<List<Station>>
    fun loadArtistAlbums(
        artistId: Int,
        offset: Int,
        count: Int,
        sortType: String? = null
    ): Single<List<Album>>

    fun loadArtistAppearsOn(artistId: Int, sortType: String? = null): Single<List<Album>>
    fun loadArtistSimilar(artistId: Int): Single<List<Artist>>
}
