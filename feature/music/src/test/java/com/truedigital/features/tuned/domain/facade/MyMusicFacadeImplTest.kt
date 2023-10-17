package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.album.repository.AlbumRepository
import com.truedigital.features.tuned.data.artist.repository.ArtistRepository
import com.truedigital.features.tuned.data.playlist.repository.PlaylistRepository
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MyMusicFacadeImplTest {

    private lateinit var myMusicFacadeImpl: MyMusicFacadeImpl

    private val albumRepository: AlbumRepository = mock()
    private val artistRepository: ArtistRepository = mock()
    private val playlistRepository: PlaylistRepository = mock()
    private val trackRepository: TrackRepository = mock()

    @BeforeEach
    fun setup() {
        myMusicFacadeImpl = MyMusicFacadeImpl(
            albumRepository,
            artistRepository,
            playlistRepository,
            trackRepository
        )
    }

    @Test
    fun getFollowedArtistCount_check_call() {
        whenever(artistRepository.getFollowedCount()).thenReturn(Single.just(2))

        myMusicFacadeImpl.getFollowedArtistCount()

        verify(artistRepository, times(1)).getFollowedCount()
    }

    @Test
    fun getFavouritedAlbumCount_check_call() {
        whenever(albumRepository.getFavouritedCount()).thenReturn(Single.just(1))

        myMusicFacadeImpl.getFavouritedAlbumCount()

        verify(albumRepository, times(1)).getFavouritedCount()
    }

    @Test
    fun getFavouritedSongCount_check_call() {
        whenever(trackRepository.getFavouritedSongsCount()).thenReturn(Single.just(1))

        myMusicFacadeImpl.getFavouritedSongCount()

        verify(trackRepository, times(1)).getFavouritedSongsCount()
    }

    @Test
    fun getFavouritedPlaylistCount_check_call() {
        whenever(playlistRepository.getFavouritedCount()).thenReturn(Single.just(1))

        myMusicFacadeImpl.getFavouritedPlaylistCount()

        verify(playlistRepository, times(1)).getFavouritedCount()
    }
}
