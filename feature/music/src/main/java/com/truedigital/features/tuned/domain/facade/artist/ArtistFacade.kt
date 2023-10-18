package com.truedigital.features.tuned.domain.facade.artist

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.track.model.Track
import io.reactivex.Single

interface ArtistFacade {
    fun loadArtist(artistId: Int): Single<Artist>
    fun loadArtistPlayCount(artistId: Int): Single<Int>
    fun loadArtistAlbums(
        artistId: Int,
        offset: Int,
        count: Int,
        sortType: String? = null
    ): Single<List<Album>>

    fun loadArtistAppearsOn(artistId: Int, sortType: String? = null): Single<List<Album>>
    fun loadPopularSongs(artistId: Int): Single<List<Track>>
    fun loadLatestSongs(artistId: Int): Single<List<Track>>
    fun loadSimilarArtists(artistId: Int): Single<List<Artist>>
    fun loadStationsAppearsIn(artistId: Int): Single<List<Station>>
    fun loadArtistAndSimilarStation(artistId: Int): Single<Station>
    fun loadFollowed(artistId: Int): Single<Boolean>
    fun toggleFavourite(artistId: Int): Single<Any>
    fun getHasArtistShuffleRight(): Boolean

    // add enum VoteType if another type will be used
    fun clearArtistVotes(artistId: Int, voteType: String = "All"): Single<Any>

    fun getAlbumNavigationAllowed(): Boolean
    fun getVideoAppearsIn(
        artistId: Int,
        offset: Int,
        count: Int,
        sortType: String? = null
    ): Single<List<Track>>

    fun isArtistHintShown(): Boolean
    fun setArtistHintShown()

    fun getIsDMCAEnabled(): Boolean
}
