package com.truedigital.features.tuned.domain.facade.playlist

import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.cache.repository.CacheRepository
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.playlist.repository.PlaylistRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import io.reactivex.Single
import javax.inject.Inject

class PlaylistFacadeImpl @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val musicUserRepository: MusicUserRepository,
    private val authenticationTokenRepository: AuthenticationTokenRepository,
    private val cacheRepository: CacheRepository
) : PlaylistFacade {

    override fun getPlaylist(id: Int): Single<Playlist> = playlistRepository.get(id)

    override fun getPlaylistTracks(playlistId: Int, offset: Int, count: Int): Single<List<Track>> =
        playlistRepository.getTracks(playlistId, offset, count)
            .map { list ->
                list.map { track ->
                    track.isCached = cacheRepository.getTrackLocationIfExist(track.id) != null
                    track
                }
            }

    override fun loadFavourited(id: Int) = playlistRepository.isFavourited(id)

    override fun toggleFavourite(id: Int): Single<Any> =
        loadFavourited(id).flatMap { isFavourite ->
            if (isFavourite) {
                playlistRepository.removeFavourite(id)
            } else {
                playlistRepository.addFavourite(id)
            }
        }

    override fun isOwner(creatorId: Int): Single<Boolean> =
        musicUserRepository.get().map { user ->
            user.userId == creatorId
        }

    override fun hasPlaylistWriteRight() =
        authenticationTokenRepository.getCurrentToken()?.hasPlaylistWriteRight ?: false
}
