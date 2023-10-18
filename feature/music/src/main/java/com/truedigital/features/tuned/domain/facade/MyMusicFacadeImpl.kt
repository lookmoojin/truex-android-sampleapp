package com.truedigital.features.tuned.domain.facade

import com.truedigital.features.tuned.data.album.repository.AlbumRepository
import com.truedigital.features.tuned.data.artist.repository.ArtistRepository
import com.truedigital.features.tuned.data.playlist.repository.PlaylistRepository
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import com.truedigital.features.tuned.presentation.main.facade.MyMusicFacade
import javax.inject.Inject

class MyMusicFacadeImpl @Inject constructor(
    val albumRepository: AlbumRepository,
    val artistRepository: ArtistRepository,
    val playlistRepository: PlaylistRepository,
    val trackRepository: TrackRepository
) : MyMusicFacade {

    override fun getFollowedArtistCount() = artistRepository.getFollowedCount()

    override fun getFavouritedAlbumCount() = albumRepository.getFavouritedCount()

    override fun getFavouritedSongCount() = trackRepository.getFavouritedSongsCount()

    override fun getFavouritedPlaylistCount() = playlistRepository.getFavouritedCount()
}
