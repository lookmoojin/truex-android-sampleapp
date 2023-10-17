package com.truedigital.features.music.data.addsong.repository

import com.truedigital.features.music.api.MusicPlaylistApiInterface
import com.truedigital.features.tuned.data.playlist.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface AddSongRepository {
    fun addTracksToPlaylist(
        playlistId: String,
        trackIds: List<Int>
    ): Flow<Playlist>
}

class AddSongRepositoryImpl @Inject constructor(
    private val apiInterface: MusicPlaylistApiInterface
) : AddSongRepository {

    companion object {
        const val ERROR_ADD_SONGS_TO_PLAYLIST = "Can't add songs to playlist"
    }

    override fun addTracksToPlaylist(
        playlistId: String,
        trackIds: List<Int>
    ): Flow<Playlist> {
        return flow {
            val response = apiInterface.addTracksToPlaylist(
                id = playlistId,
                trackIds = trackIds
            )
            val result = if (response.isSuccessful && response.body() != null) {
                response.body() ?: error(ERROR_ADD_SONGS_TO_PLAYLIST)
            } else {
                error(ERROR_ADD_SONGS_TO_PLAYLIST)
            }
            emit(result)
        }
    }
}
