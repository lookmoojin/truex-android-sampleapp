package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.cache.repository.CacheRepository
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.playlist.repository.PlaylistRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.domain.facade.playlist.PlaylistFacade
import com.truedigital.features.tuned.domain.facade.playlist.PlaylistFacadeImpl
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date
import kotlin.test.assertFalse

class PlaylistFacadeImplTest {

    private lateinit var playlistFacade: PlaylistFacade
    private val playlistRepository: PlaylistRepository = mock()
    private val musicUserRepository: MusicUserRepository = mock()
    private val authenticationTokenRepository: AuthenticationTokenRepository = mock()
    private val cacheRepository: CacheRepository = mock()

    @BeforeEach
    fun setUp() {
        playlistFacade = PlaylistFacadeImpl(
            playlistRepository,
            musicUserRepository,
            authenticationTokenRepository,
            cacheRepository
        )
    }

    @Test
    fun testGetPlaylist_success_verifyPlaylistRepositoryRequest() {
        val mockId = 1
        whenever(playlistRepository.get(mockId)).thenReturn(Single.just(mockPlaylist()))

        playlistFacade.getPlaylist(mockId)
            .test()
            .assertNoErrors()

        verify(playlistRepository, times(1)).get(mockId)
    }

    @Test
    fun testGetPlaylist_fail_verifyPlaylistRepositoryRequest() {
        val mockId = 1
        whenever(playlistRepository.get(mockId)).thenReturn(Single.error(Throwable("error")))

        playlistFacade.getPlaylist(mockId)
            .test()
            .assertError { it.message == "error" }

        verify(playlistRepository, times(1)).get(mockId)
    }

    @Test
    fun testGetPlaylistTracks_getTrackLocationIfExistNotNull_returnIsCachedTrue() {
        val mockId = 1
        whenever(playlistRepository.getTracks(any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    mockTrack()
                )
            )
        )
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("location")

        playlistFacade.getPlaylistTracks(mockId, 0, 10)
            .test()
            .assertNoErrors()
            .assertValue {
                it.first().isCached
            }
    }

    @Test
    fun testGetPlaylistTracks_getTrackLocationIfExistIsNull_returnIsCachedFalse() {
        val mockId = 1
        whenever(playlistRepository.getTracks(any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    mockTrack()
                )
            )
        )
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn(null)

        playlistFacade.getPlaylistTracks(mockId, 0, 10)
            .test()
            .assertNoErrors()
            .assertValue {
                !it.first().isCached
            }
    }

    @Test
    fun testGetPlaylistTracks_getTrackLocationIfExistIsEmpty_returnIsCachedTrue() {
        val mockId = 1
        whenever(playlistRepository.getTracks(any(), any(), any())).thenReturn(
            Single.just(
                listOf(
                    mockTrack()
                )
            )
        )
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("")

        playlistFacade.getPlaylistTracks(mockId, 0, 10)
            .test()
            .assertNoErrors()
            .assertValue {
                it.first().isCached
            }
    }

    @Test
    fun testLoadFavourite_success_verifyPlaylistRepositoryRequest() {
        val mockId = 1
        whenever(playlistRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        playlistFacade.loadFavourited(mockId)
            .test()
            .assertNoErrors()
            .assertValue { it }

        verify(playlistRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testLoadFavourite_fail_verifyPlaylistRepositoryRequest() {
        val mockId = 1
        whenever(playlistRepository.isFavourited(mockId)).thenReturn(Single.error(Throwable("error")))

        playlistFacade.loadFavourited(mockId)
            .test()
            .assertError { it.message == "error" }

        verify(playlistRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testToggleFavourite_isFavouriteTrue_verifyRemoveFavouriteRequest() {
        val mockId = 1
        whenever(playlistRepository.isFavourited(mockId)).thenReturn(Single.just(true))
        whenever(playlistRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))
        whenever(playlistRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        playlistFacade.toggleFavourite(mockId)
            .test()
            .assertNoErrors()

        verify(playlistRepository, times(1)).removeFavourite(mockId)
        verify(playlistRepository, times(0)).addFavourite(mockId)
    }

    @Test
    fun testToggleFavourite_isFavouriteFalse_verifyAddFavouriteRequest() {
        val mockId = 1
        whenever(playlistRepository.isFavourited(mockId)).thenReturn(Single.just(false))
        whenever(playlistRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))
        whenever(playlistRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        playlistFacade.toggleFavourite(mockId)
            .test()
            .assertNoErrors()

        verify(playlistRepository, times(0)).removeFavourite(mockId)
        verify(playlistRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testIsOwner_sameId_returnTrue() {
        val mockId = 1
        whenever(musicUserRepository.get()).thenReturn(
            Single.just(
                User(
                    userId = mockId,
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
                    isVerified = false,
                    isTwitterUser = false
                )
            )
        )

        playlistFacade.isOwner(mockId)
            .test()
            .assertNoErrors()
            .assertValue {
                it
            }

        verify(musicUserRepository, times(1)).get()
    }

    @Test
    fun testIsOwner_notSameId_returnFalse() {
        val mockId = 1
        whenever(musicUserRepository.get()).thenReturn(
            Single.just(
                User(
                    userId = 2,
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
                    isVerified = false,
                    isTwitterUser = false
                )
            )
        )

        playlistFacade.isOwner(mockId)
            .test()
            .assertNoErrors()
            .assertValue {
                !it
            }

        verify(musicUserRepository, times(1)).get()
    }

    @Test
    fun testHasPlaylistWriteRight_hasPlaylistWriteRightIsFalse_returnFalse() {
        whenever(authenticationTokenRepository.getCurrentToken())
            .thenReturn(AuthenticationToken(refreshToken = "", expiration = 0L, accessToken = null))

        assertFalse(playlistFacade.hasPlaylistWriteRight())
    }

    @Test
    fun testHasPlaylistWriteRight_getCurrentTokenNull_returnFalse() {
        whenever(authenticationTokenRepository.getCurrentToken()).thenReturn(null)

        assertFalse(playlistFacade.hasPlaylistWriteRight())
    }

    private fun mockPlaylist() = Playlist(
        id = 1,
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
        isPublic = true,
        typedTags = listOf(),
        isOwner = true
    )

    private fun mockTrack() = Track(
        id = 1,
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
