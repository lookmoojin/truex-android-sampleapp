package com.truedigital.features.tuned.data.artist.repository

import com.truedigital.features.tuned.api.artist.ArtistMetadataApiInterface
import com.truedigital.features.tuned.api.artist.ArtistServicesApiInterface
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ArtistRepositoryImpl(
    private val artistServiceApi: ArtistServicesApiInterface,
    private val artistMetadataApi: ArtistMetadataApiInterface,
    private val musicRoomRepository: MusicRoomRepository
) : ArtistRepository {

    override fun get(id: Int, refresh: Boolean): Single<Artist> =
        Single.just(refresh)
            .subscribeOn(Schedulers.io())
            .flatMap {
                if (it) throw IllegalStateException("Refresh Required")
                else musicRoomRepository.getArtist(id)
            }
            .onErrorResumeNext {
                artistMetadataApi.artist(id).map {
                    Artist(it.identity.id, it.identity.name, it.image)
                }.flatMap { artist ->
                    musicRoomRepository.insertArtist(artist).map { artist }
                }
            }

    override fun getPlayCount(id: Int): Single<Int> =
        artistMetadataApi.playCount(id).map { it.globalTotal }

    override fun getTrending(offset: Int, count: Int): Single<List<Artist>> =
        artistMetadataApi.trendingArtists(offset, count)
            .map { it.results }
            .flatMap { artists ->
                musicRoomRepository.insertArtists(artists).map { artists }
            }

    override fun getStationTrending(stationId: Int): Single<List<Artist>> =
        artistMetadataApi.stationTrendingArtists(stationId)
            .flatMap { artists ->
                musicRoomRepository.insertArtists(artists).map { artists }
            }

    override fun getSimilar(artistId: Int): Single<List<Artist>> =
        artistMetadataApi.similarArtists(artistId)
            .flatMap { artists ->
                musicRoomRepository.insertArtists(artists).map { artists }
            }

    override fun getAlbums(
        artistId: Int,
        sortType: String?,
        offset: Int,
        count: Int
    ): Single<List<Album>> =
        artistMetadataApi.getAlbums(artistId, sortType, offset, count).map { it.results }

    override fun getAppearsOn(artistId: Int, sortType: String?): Single<List<Album>> =
        artistMetadataApi.getAppearsOn(artistId, sortType).map { it.results }

    override fun getTracks(
        artistId: Int,
        offset: Int,
        count: Int,
        type: TrackRequestType,
        sortType: String?,
        releasedInDays: Int?
    ): Single<List<Track>> {
        return artistMetadataApi.getTracks(
            artistId,
            offset,
            count,
            type.value,
            sortType,
            releasedInDays
        )
            .map { pagedResults ->
                pagedResults.results.map { _primaryTrack ->
                    _primaryTrack.primaryTrack
                }
            }
            .map { trackList ->
                trackList.map { track ->
                    track.copy(name = track.nameTranslations)
                }
            }
    }

    override fun getRecommendedArtists(offset: Int, count: Int): Single<List<Artist>> =
        artistServiceApi.recommendedArtists(offset, count)
            .map { it.results }
            .flatMap { artists ->
                musicRoomRepository.insertArtists(artists).map { artists }
            }

    override fun getFollowed(offset: Int, count: Int): Single<List<Artist>> =
        artistServiceApi.followedArtists(offset, count)
            .map { it.results }
            .flatMap { artists ->
                musicRoomRepository.insertArtists(artists).map { artists }
            }

    override fun isFollowing(artistId: Int): Single<Boolean> =
        artistServiceApi.userContext(artistId).map { it.isFollowing }

    override fun removeFollow(artistId: Int): Single<Any> =
        artistServiceApi.unfollowArtist(artistId)

    override fun addFollow(artistId: Int): Single<Any> = artistServiceApi.followArtist(artistId)

    override fun getFollowedCount(): Single<Int> =
        artistServiceApi.followedArtists(1, 1).map { it.total }

    override fun clearArtistVotes(artistId: Int, voteType: String): Single<Any> =
        artistServiceApi.clearArtistVotes(artistId, voteType)
}
