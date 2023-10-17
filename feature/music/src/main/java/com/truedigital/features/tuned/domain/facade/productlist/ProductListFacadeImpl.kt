package com.truedigital.features.tuned.domain.facade.productlist

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.repository.AlbumRepository
import com.truedigital.features.tuned.data.artist.repository.ArtistRepository
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.playlist.repository.PlaylistRepository
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.tag.repository.TagRepository
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import io.reactivex.Single
import java.util.Calendar
import javax.inject.Inject

class ProductListFacadeImpl @Inject constructor(
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val playlistRepository: PlaylistRepository,
    private val stationRepository: StationRepository,
    private val trackRepository: TrackRepository,
    private val tagRepository: TagRepository,
    private val musicUserRepository: MusicUserRepository
) : ProductListFacade {
    // favourite lists

    companion object {
        private const val COUNT_25 = 25
        private const val RELEASE_IN_DAY = 30
        private const val TARGET_TIME = "TargetTime"
        private const val NEW_RELEASES_TYPE = "newreleases"
        private const val TARGET_TIME_AM = "#@TT#AM"
        private const val TARGET_TIME_PM = "#@TT#PM"
    }

    override fun loadFavouriteStations(offset: Int, count: Int) =
        stationRepository.getFavourited(offset, count)

    override fun loadFollowedArtists(offset: Int, count: Int) =
        artistRepository.getFollowed(offset, count)

    override fun loadFavouriteAlbums(offset: Int, count: Int) =
        albumRepository.getFavourited(offset, count)

    override fun loadFavouritePlaylists(offset: Int, count: Int): Single<List<Playlist>> =
        musicUserRepository.get().flatMap {
            playlistRepository.getFavourited(offset, count, it.userId)
        }

    override fun loadFavouriteSongs(offset: Int, count: Int) =
        trackRepository.getFavourited(offset, count, TrackRequestType.AUDIO)

    override fun loadFavouriteVideos(offset: Int, count: Int) =
        trackRepository.getFavourited(offset, count, TrackRequestType.VIDEO)

    // tagged lists

    override fun loadStationsWithTag(tag: String, offset: Int, count: Int) =
        tagRepository.getStationsByTag(tag, offset, count)

    override fun loadArtistsWithTag(tag: String, offset: Int, count: Int) =
        tagRepository.getArtistsByTag(tag, offset, count)

    override fun loadAlbumsWithTag(tag: String, offset: Int, count: Int) =
        tagRepository.getAlbumsByTag(tag, offset, count)

    override fun loadAlbumsByTagGroup(tag: String, offset: Int, count: Int): Single<List<Album>> =
        when (Calendar.getInstance().get(Calendar.AM_PM)) {
            Calendar.AM -> getAlbumsByTagGroup(TARGET_TIME_AM, tag, offset, count)
            Calendar.PM -> getAlbumsByTagGroup(TARGET_TIME_PM, tag, offset, count)
            else -> Single.error(IllegalStateException("Unable to determine am/pm"))
        }

    private fun getAlbumsByTagGroup(timeTag: String, tag: String, offset: Int, count: Int) =
        tagRepository.getAlbumsByTagGroup(
            "$timeTag:$tag",
            TARGET_TIME,
            offset,
            count
        )

    override fun loadPlaylistsWithTag(tag: String, offset: Int, count: Int) =
        tagRepository.getPlaylistsByTag(tag, offset, count, TrackRequestType.ALL)

    // trending lists

    override fun loadTrendingStations() = stationRepository.getPopular()

    override fun loadTrendingArtists(offset: Int, count: Int) =
        artistRepository.getTrending(offset, count)

    override fun loadTrendingAlbums(offset: Int, count: Int) =
        albumRepository.getTrending(offset, count)

    override fun loadTrendingPlaylists(offset: Int, count: Int) =
        playlistRepository.getTrending(offset, count, TrackRequestType.ALL)

    // other lists

    override fun loadSuggestedStations(offset: Int, count: Int) =
        stationRepository.getSuggested(offset, count)

    override fun loadRecommendedArtists(offset: Int, count: Int) =
        artistRepository.getRecommendedArtists(offset, count)

    override fun loadNewReleases(offset: Int, count: Int) =
        albumRepository.getNewReleases(offset, count)

    override fun loadMultipleTags(tags: String) = tagRepository.getTags(tags)

    // station lists

    override fun loadStationFeaturedArtists(stationId: Int) =
        artistRepository.getStationTrending(stationId)

    override fun loadStationSimilar(stationId: Int) = stationRepository.getSimilar(stationId)

    // artist lists
    override fun loadArtistPopularSongs(artistId: Int) =
        artistRepository.getTracks(artistId, 1, COUNT_25, TrackRequestType.AUDIO, "popularity")

    override fun loadArtistLatestSongs(artistId: Int) =
        artistRepository.getTracks(
            artistId,
            1,
            COUNT_25,
            TrackRequestType.AUDIO,
            NEW_RELEASES_TYPE,
            RELEASE_IN_DAY
        )

    override fun loadArtistVideoAppearsIn(
        artistId: Int,
        offset: Int,
        count: Int,
        sortType: String?
    ) = artistRepository.getTracks(artistId, offset, count, TrackRequestType.VIDEO, sortType)

    override fun loadArtistAppearsIn(artistId: Int) =
        stationRepository.getContainingArtist(artistId)

    override fun loadArtistAlbums(artistId: Int, offset: Int, count: Int, sortType: String?) =
        artistRepository.getAlbums(artistId, sortType, offset, count)

    override fun loadArtistAppearsOn(artistId: Int, sortType: String?) =
        artistRepository.getAppearsOn(artistId, sortType)

    override fun loadArtistSimilar(artistId: Int) = artistRepository.getSimilar(artistId)
}
