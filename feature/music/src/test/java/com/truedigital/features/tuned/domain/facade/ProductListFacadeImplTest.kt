package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.album.repository.AlbumRepository
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.repository.ArtistRepository
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.playlist.repository.PlaylistRepository
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.data.tag.repository.TagRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.domain.facade.productlist.ProductListFacade
import com.truedigital.features.tuned.domain.facade.productlist.ProductListFacadeImpl
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class ProductListFacadeImplTest {

    private lateinit var productListFacade: ProductListFacade
    private val artistRepository: ArtistRepository = mock()
    private val albumRepository: AlbumRepository = mock()
    private val playlistRepository: PlaylistRepository = mock()
    private val stationRepository: StationRepository = mock()
    private val trackRepository: TrackRepository = mock()
    private val tagRepository: TagRepository = mock()
    private val musicUserRepository: MusicUserRepository = mock()

    @BeforeEach
    fun setup() {
        productListFacade = ProductListFacadeImpl(
            artistRepository,
            albumRepository,
            playlistRepository,
            stationRepository,
            trackRepository,
            tagRepository,
            musicUserRepository
        )
    }

    @Test
    fun loadFavouriteStations_call_api() {
        whenever(stationRepository.getFavourited(any(), any())).thenReturn(
            Single.just(
                listOf(
                    getStation(), getStation()
                )
            )
        )

        productListFacade.loadFavouriteStations(1, 10)
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).getFavourited(any(), any())
    }

    @Test
    fun loadFollowedArtists_call_api() {
        whenever(artistRepository.getFollowed(any(), any())).thenReturn(
            Single.just(
                listOf(
                    getArtist(), getArtist()
                )
            )
        )

        productListFacade.loadFollowedArtists(1, 10)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getFollowed(any(), any())
    }

    @Test
    fun loadFavouriteAlbums_call_api() {
        whenever(albumRepository.getFavourited(any(), any())).thenReturn(
            Single.just(
                listOf(
                    getRelease(), getRelease()
                )
            )
        )

        productListFacade.loadFavouriteAlbums(1, 10)
            .test()
            .assertNoErrors()

        verify(albumRepository, times(1)).getFavourited(any(), any())
    }

    @Test
    fun loadFavouritePlaylists_call_api() {
        whenever(musicUserRepository.get()).thenReturn(
            Single.just(
                getUser()
            )
        )
        whenever(playlistRepository.getFavourited(any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    getPlaylist(), getPlaylist()
                )
            )
        )

        productListFacade.loadFavouritePlaylists(1, 10)
            .test()
            .assertNoErrors()
            .assertValue { playlist ->
                playlist.size == 2
            }

        verify(musicUserRepository, times(1)).get()
    }

    @Test
    fun loadFavouriteSongs_call_api() {
        whenever(trackRepository.getFavourited(any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    getTrack()
                )
            )
        )

        productListFacade.loadFavouriteSongs(1, 10)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).getFavourited(any(), any(), any())
    }

    @Test
    fun loadFavouriteVideos_call_api() {
        whenever(trackRepository.getFavourited(any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    getTrack()
                )
            )
        )

        productListFacade.loadFavouriteVideos(1, 10)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).getFavourited(any(), any(), any())
    }

    @Test
    fun loadStationsWithTag_call_api() {
        whenever(tagRepository.getStationsByTag(any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    getStation()
                )
            )
        )

        productListFacade.loadStationsWithTag("", 1, 10)
            .test()
            .assertNoErrors()

        verify(tagRepository, times(1)).getStationsByTag(any(), any(), any())
    }

    @Test
    fun loadArtistsWithTag_call_api() {
        whenever(tagRepository.getArtistsByTag(any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    getArtist()
                )
            )
        )

        productListFacade.loadArtistsWithTag("", 1, 10)
            .test()
            .assertNoErrors()

        verify(tagRepository, times(1)).getArtistsByTag(any(), any(), any())
    }

    @Test
    fun loadAlbumsWithTag_call_api() {
        whenever(tagRepository.getAlbumsByTag(any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    getAlbum()
                )
            )
        )

        productListFacade.loadAlbumsWithTag("", 1, 10)
            .test()
            .assertNoErrors()

        verify(tagRepository, times(1)).getAlbumsByTag(any(), any(), any())
    }

    @Test
    fun loadPlaylistsWithTag_call_api() {
        whenever(tagRepository.getPlaylistsByTag(any(), any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    getPlaylist()
                )
            )
        )

        productListFacade.loadPlaylistsWithTag("", 1, 10)
            .test()
            .assertNoErrors()

        verify(tagRepository, times(1)).getPlaylistsByTag(any(), any(), any(), any())
    }

    @Test
    fun loadTrendingStations_call_api() {
        whenever(stationRepository.getPopular()).thenReturn(
            Single.just(
                listOf(
                    getStation()
                )
            )
        )

        productListFacade.loadTrendingStations()
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).getPopular()
    }

    @Test
    fun loadTrendingArtists_call_api() {
        whenever(artistRepository.getTrending(any(), any())).thenReturn(
            Single.just(
                listOf(
                    getArtist()
                )
            )
        )

        productListFacade.loadTrendingArtists(1, 10)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getTrending(any(), any())
    }

    @Test
    fun loadTrendingAlbums_call_api() {
        whenever(albumRepository.getTrending(any(), any())).thenReturn(
            Single.just(
                listOf(
                    getAlbum()
                )
            )
        )

        productListFacade.loadTrendingAlbums(1, 10)
            .test()
            .assertNoErrors()

        verify(albumRepository, times(1)).getTrending(any(), any())
    }

    @Test
    fun loadTrendingPlaylists_call_api() {
        whenever(playlistRepository.getTrending(any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    getPlaylist()
                )
            )
        )

        productListFacade.loadTrendingPlaylists(1, 10)
            .test()
            .assertNoErrors()

        verify(playlistRepository, times(1)).getTrending(any(), any(), any())
    }

    @Test
    fun loadSuggestedStations_call_api() {
        whenever(stationRepository.getSuggested(any(), any())).thenReturn(
            Single.just(
                listOf(
                    getStation()
                )
            )
        )

        productListFacade.loadSuggestedStations(1, 10)
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).getSuggested(any(), any())
    }

    @Test
    fun loadRecommendedArtists_call_api() {
        whenever(artistRepository.getRecommendedArtists(any(), any())).thenReturn(
            Single.just(
                listOf(
                    getArtist()
                )
            )
        )

        productListFacade.loadRecommendedArtists(1, 10)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getRecommendedArtists(any(), any())
    }

    @Test
    fun loadNewReleases_call_api() {
        whenever(albumRepository.getNewReleases(any(), any())).thenReturn(
            Single.just(
                listOf(
                    getAlbum()
                )
            )
        )

        productListFacade.loadNewReleases(1, 10)
            .test()
            .assertNoErrors()

        verify(albumRepository, times(1)).getNewReleases(any(), any())
    }

    @Test
    fun loadMultipleTags_call_api() {
        whenever(tagRepository.getTags(any())).thenReturn(
            Single.just(
                listOf(
                    getTag()
                )
            )
        )

        productListFacade.loadMultipleTags("")
            .test()
            .assertNoErrors()

        verify(tagRepository, times(1)).getTags(any())
    }

    @Test
    fun loadStationFeaturedArtists_call_api() {
        whenever(artistRepository.getStationTrending(any())).thenReturn(
            Single.just(
                listOf(
                    getArtist()
                )
            )
        )

        productListFacade.loadStationFeaturedArtists(1)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getStationTrending(any())
    }

    @Test
    fun loadStationSimilar_call_api() {
        whenever(stationRepository.getSimilar(any())).thenReturn(
            Single.just(
                listOf(
                    getStation()
                )
            )
        )

        productListFacade.loadStationSimilar(1)
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).getSimilar(any())
    }

    @Test
    fun loadArtistPopularSongs_call_api() {
        whenever(
            artistRepository.getTracks(
                1,
                1,
                25,
                TrackRequestType.AUDIO,
                "popularity"
            )
        ).thenReturn(
            Single.just(
                listOf(
                    getTrack()
                )
            )
        )

        productListFacade.loadArtistPopularSongs(1)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getTracks(1, 1, 25, TrackRequestType.AUDIO, "popularity")
    }

    @Test
    fun loadArtistLatestSongs_call_api() {
        whenever(artistRepository.getTracks(any(), any(), any(), any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    getTrack()
                )
            )
        )

        productListFacade.loadArtistLatestSongs(1)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getTracks(any(), any(), any(), any(), any(), any())
    }

    @Test
    fun loadArtistVideoAppearsIn_call_api() {
        val artistId = 1
        val offset = 1
        val count = 10
        val sortType = ""
        whenever(
            artistRepository.getTracks(
                artistId,
                offset,
                count,
                TrackRequestType.VIDEO,
                sortType
            )
        ).thenReturn(
            Single.just(
                listOf(
                    getTrack()
                )
            )
        )

        productListFacade.loadArtistVideoAppearsIn(artistId, offset, count, sortType)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getTracks(
            artistId,
            offset,
            count,
            TrackRequestType.VIDEO,
            sortType
        )
    }

    @Test
    fun loadArtistVideoAppearsIn_sortTypeNull_verify() {
        val artistId = 1
        val offset = 1
        val count = 10
        whenever(
            artistRepository.getTracks(
                any(),
                any(),
                any(),
                any(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(
            Single.just(
                listOf(
                    getTrack()
                )
            )
        )

        productListFacade.loadArtistVideoAppearsIn(artistId, offset, count)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getTracks(
            any(),
            any(),
            any(),
            any(),
            anyOrNull(),
            anyOrNull()
        )
    }

    @Test
    fun loadArtistAppearsIn_call_api() {
        whenever(stationRepository.getContainingArtist(any())).thenReturn(
            Single.just(
                listOf(
                    getStation()
                )
            )
        )

        productListFacade.loadArtistAppearsIn(1)
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).getContainingArtist(any())
    }

    @Test
    fun loadArtistAlbums_call_api() {
        whenever(artistRepository.getAlbums(any(), any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    getAlbum()
                )
            )
        )

        productListFacade.loadArtistAlbums(1, 1, 10, "")
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getAlbums(any(), any(), any(), any())
    }

    @Test
    fun loadArtistAlbums_sortTypeNull_verify() {
        whenever(artistRepository.getAlbums(any(), anyOrNull(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    getAlbum()
                )
            )
        )

        productListFacade.loadArtistAlbums(1, 1, 10)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getAlbums(any(), anyOrNull(), any(), any())
    }

    @Test
    fun loadArtistAppearsOn_call_api() {
        whenever(artistRepository.getAppearsOn(any(), any())).thenReturn(
            Single.just(
                listOf(
                    getAlbum()
                )
            )
        )

        productListFacade.loadArtistAppearsOn(1, "")
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getAppearsOn(any(), any())
    }

    @Test
    fun loadArtistAppearsOn_sortTypeNull_verify() {
        whenever(artistRepository.getAppearsOn(any(), anyOrNull())).thenReturn(
            Single.just(
                listOf(
                    getAlbum()
                )
            )
        )

        productListFacade.loadArtistAppearsOn(1)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getAppearsOn(any(), anyOrNull())
    }

    @Test
    fun loadArtistSimilar_call_api() {
        whenever(artistRepository.getSimilar(any())).thenReturn(
            Single.just(
                listOf(
                    getArtist()
                )
            )
        )

        productListFacade.loadArtistSimilar(1)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getSimilar(any())
    }

    private fun getArtist(): Artist {
        return Artist(
            id = 1,
            name = "",
            image = ""
        )
    }

    private fun getStation(): Station {
        return Station(
            id = 1,
            type = Station.StationType.ARTIST,
            name = listOf(),
            description = listOf(),
            coverImage = listOf(),
            bannerImage = listOf(),
            bannerURL = "",
            isActive = true
        )
    }

    private fun getAlbum(): Album {
        return Album(
            id = 1,
            name = "",
            artists = listOf(),
            primaryRelease = null,
            releaseIds = listOf()
        )
    }

    private fun getPlaylist(isPublic: Boolean = false): Playlist {
        return Playlist(
            id = 2,
            name = listOf(),
            description = listOf(),
            creatorId = 1,
            creatorName = "creatorName",
            creatorImage = "creatorImage",
            trackCount = 10,
            publicTrackCount = 10,
            duration = 3000,
            createDate = Date(),
            updateDate = Date(),
            trackIds = listOf(),
            coverImage = listOf(),
            isVideo = true,
            isPublic = isPublic,
            typedTags = listOf(),
            isOwner = true
        )
    }

    private fun getTrack(): Track {
        return Track(
            id = 2,
            playlistTrackId = 1,
            songId = 1,
            releaseId = 1,
            artists = listOf(),
            name = "name",
            originalCredit = "originalCredit",
            isExplicit = false,
            trackNumber = 1,
            trackNumberInVolume = 1,
            volumeNumber = 1,
            releaseArtists = listOf(),
            sample = "sample",
            isOnCompilation = false,
            releaseName = "releaseName",
            allowDownload = false,
            allowStream = false,
            duration = 3L,
            image = "image",
            hasLyrics = false,
            video = null,
            isVideo = false,
            vote = null,
            isDownloaded = false,
            syncProgress = 1F,
            isCached = false
        )
    }

    private fun getTag(): Tag {
        return Tag(
            id = 1,
            name = "nameTag",
            image = "image",
            images = null,
            displayName = listOf(
                LocalisedString(
                    language = "languageDisplayName",
                    value = "valueDisplayName"
                )
            )
        )
    }

    private fun getRelease(): Release {
        return Release(
            id = 1, albumId = 1, artists = listOf(),
            name = "",
            isExplicit = true,
            numberOfVolumes = 1, trackIds = listOf(),
            duration = 1,
            volumes = listOf(),
            image = "image",
            webPath = "webPath",
            copyRight = "copyRight",
            label = null,
            originalReleaseDate = null,
            digitalReleaseDate = null,
            physicalReleaseDate = null,
            saleAvailabilityDateTime = null,
            streamAvailabilityDateTime = null,
            allowStream = true,
            allowDownload = true
        )
    }

    private fun getUser(): User {
        return User(
            userId = 1,
            displayName = "Test",
            firstName = "Test",
            lastName = "Test",
            primaryEmail = "Email",
            isPrimaryEmailValidated = true,
            image = "image",
            backgroundImage = "image",
            followers = listOf(),
            following = listOf(),
            isPublic = true,
            optedIn = true,
            blocked = listOf(),
            language = "th",
            subscriptions = listOf(),
            devices = listOf(),
            isFacebookUser = true,
            circle = "",
            birthYear = 1,
            gender = "",
            logins = listOf(),
            action = "action",
            audioQuality = "audioQuality",
            contentLanguages = listOf(),
            country = "",
            isVerified = true,
            isTwitterUser = false
        )
    }
}
