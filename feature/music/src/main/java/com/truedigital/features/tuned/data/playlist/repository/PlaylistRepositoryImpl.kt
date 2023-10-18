package com.truedigital.features.tuned.data.playlist.repository

import com.truedigital.features.tuned.api.playlist.PlaylistMetadataApiInterface
import com.truedigital.features.tuned.api.playlist.PlaylistServicesApiInterface
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class PlaylistRepositoryImpl(
    private val playlistServiceApi: PlaylistServicesApiInterface,
    private val playlistMetadataApi: PlaylistMetadataApiInterface,
    private val musicRoomRepository: MusicRoomRepository
) : PlaylistRepository {

    override fun get(id: Int, refresh: Boolean): Single<Playlist> =
        Single.just(refresh)
            .subscribeOn(Schedulers.io())
            .flatMap {
                if (it) throw IllegalStateException("Refresh Required")
                else musicRoomRepository.getPlaylist(id)
            }
            .onErrorResumeNext {
                playlistServiceApi.playlist(id)
                    .flatMap { playlist ->
                        musicRoomRepository.insertPlaylist(playlist).map { playlist }
                    }
            }

    override fun getTrending(
        offset: Int,
        count: Int,
        type: TrackRequestType
    ): Single<List<Playlist>> =
        playlistMetadataApi.trendingPlaylists(offset, count, type.value)
            .map { it.results }
            .flatMap { playlists ->
                musicRoomRepository.insertPlaylists(playlists).map { playlists }
            }

    override fun getTracks(id: Int, offset: Int, count: Int): Single<List<Track>> {
        return playlistMetadataApi.getTracks(id, offset, count)
            .map { pagedResults ->
                pagedResults.results
            }
            .map { trackList ->
                trackList.map { track ->
                    track.copy(name = track.nameTranslations)
                }
            }
    }

    override fun isFavourited(id: Int): Single<Boolean> =
        playlistServiceApi.userContext(id).map { it.isFavourited }

    override fun removeFavourite(id: Int): Single<Any> =
        playlistServiceApi.unfavourite(id)

    override fun addFavourite(id: Int): Single<Any> =
        playlistServiceApi.favourite(id)

    override fun getFavouritedAndOwned(
        offset: Int,
        count: Int,
        currentUserId: Int?
    ): Single<List<Playlist>> =
        playlistServiceApi.favouritedAndOwned(offset, count).map { it.results }
            .flatMap { playlists ->
                musicRoomRepository.insertPlaylists(playlists).map { playlists }
            }
            .map { list ->
                list.map { playlist ->
                    if (playlist.creatorId == currentUserId) playlist.isOwner = true
                }
                list
            }

    override fun getFavourited(
        offset: Int,
        count: Int,
        currentUserId: Int?
    ): Single<List<Playlist>> =
        playlistServiceApi.favourited(offset, count).map { it.results }
            .flatMap { playlists ->
                musicRoomRepository.insertPlaylists(playlists).map { playlists }
            }
            .map { list ->
                list.map { playlist ->
                    if (playlist.creatorId == currentUserId) playlist.isOwner = true
                }
                list
            }

    override fun getFavouritedCount(): Single<Int> =
        playlistServiceApi.favourited(1, 1).map { it.total }
}
