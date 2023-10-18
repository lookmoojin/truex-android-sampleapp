package com.truedigital.features.tuned.domain.facade.artist

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.repository.ArtistRepository
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.cache.repository.CacheRepository
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import io.reactivex.Single
import javax.inject.Inject

class ArtistFacadeImpl @Inject constructor(
    private val artistRepository: ArtistRepository,
    private val stationRepository: StationRepository,
    private val cacheRepository: CacheRepository,
    private val musicUserRepository: MusicUserRepository,
    private val deviceRepository: DeviceRepository,
    private val authenticationTokenRepository: AuthenticationTokenRepository
) : ArtistFacade {

    companion object {
        private const val COUNT = 25
        private const val RELEASE_IN_DAY = 30
    }

    override fun loadArtist(artistId: Int): Single<Artist> = artistRepository.get(artistId)

    override fun loadArtistPlayCount(artistId: Int): Single<Int> =
        artistRepository.getPlayCount(artistId)

    override fun loadArtistAlbums(
        artistId: Int,
        offset: Int,
        count: Int,
        sortType: String?
    ): Single<List<Album>> =
        artistRepository.getAlbums(artistId, sortType, offset, count)

    override fun loadPopularSongs(artistId: Int): Single<List<Track>> =
        artistRepository.getTracks(
            artistId, 1, COUNT, TrackRequestType.AUDIO,
            "popularity"
        ).map { list ->
            list.map { track ->
                track.isCached = cacheRepository.getTrackLocationIfExist(track.id) != null
                track
            }
        }

    override fun loadLatestSongs(artistId: Int): Single<List<Track>> =
        artistRepository.getTracks(
            artistId,
            1,
            COUNT,
            TrackRequestType.AUDIO,
            "newreleases",
            RELEASE_IN_DAY
        )
            .map { list ->
                list.map { track ->
                    track.isCached = cacheRepository.getTrackLocationIfExist(track.id) != null
                    track
                }
            }

    override fun loadArtistAppearsOn(artistId: Int, sortType: String?): Single<List<Album>> =
        artistRepository.getAppearsOn(artistId, sortType)

    override fun loadStationsAppearsIn(artistId: Int): Single<List<Station>> =
        stationRepository.getContainingArtist(artistId)

    override fun loadSimilarArtists(artistId: Int): Single<List<Artist>> =
        artistRepository.getSimilar(artistId)

    override fun loadArtistAndSimilarStation(artistId: Int): Single<Station> =
        stationRepository.getByArtist(artistId)

    override fun loadFollowed(artistId: Int): Single<Boolean> =
        artistRepository.isFollowing(artistId)

    override fun toggleFavourite(artistId: Int): Single<Any> =
        loadFollowed(artistId).flatMap { isFollowed ->
            if (isFollowed) {
                artistRepository.removeFollow(artistId)
            } else {
                artistRepository.addFollow(artistId)
            }
        }

    override fun getHasArtistShuffleRight(): Boolean =
        authenticationTokenRepository.getCurrentToken()?.hasArtistShuffleRight ?: false

    override fun clearArtistVotes(artistId: Int, voteType: String): Single<Any> =
        artistRepository.clearArtistVotes(artistId, voteType)

    override fun getAlbumNavigationAllowed(): Boolean =
        musicUserRepository.getSettings()?.allowAlbumNavigation ?: false

    override fun getVideoAppearsIn(
        artistId: Int,
        offset: Int,
        count: Int,
        sortType: String?
    ): Single<List<Track>> =
        artistRepository.getTracks(artistId, offset, count, TrackRequestType.VIDEO, sortType)

    override fun isArtistHintShown() = deviceRepository.isArtistHintShown()

    override fun setArtistHintShown() = deviceRepository.setArtistHintStatus(true)

    override fun getIsDMCAEnabled() = musicUserRepository.getSettings()?.dmcaEnabled ?: false
}
