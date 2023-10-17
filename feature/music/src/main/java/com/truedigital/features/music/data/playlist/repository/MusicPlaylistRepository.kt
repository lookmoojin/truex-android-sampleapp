package com.truedigital.features.music.data.playlist.repository

import com.truedigital.features.music.api.MusicPlaylistApiInterface
import com.truedigital.features.music.data.playlist.model.CreateNewPlaylistRequest
import com.truedigital.features.music.data.playlist.model.response.RemoveTrackResponse
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.util.PagedResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

interface MusicPlaylistRepository {
    fun getPlaylist(playlistId: String): Flow<Playlist>
    fun getMyPlaylists(): Flow<PagedResults<Playlist>?>
    fun postNewPlaylist(request: CreateNewPlaylistRequest): Flow<Playlist>
    fun uploadCoverImage(
        playlistId: Int,
        language: String,
        multiPart: MultipartBody.Part
    ): Flow<Any>

    fun deletePlaylist(playlistId: Int): Flow<Any>
    fun removeTrack(playlistId: Int, trackIds: List<Int>): Flow<RemoveTrackResponse>
}

class MusicPlaylistRepositoryImpl @Inject constructor(
    private val apiInterface: MusicPlaylistApiInterface
) : MusicPlaylistRepository {

    companion object {
        const val ERROR_LOAD_PLAYLIST = "Playlist is fail or data not found"
        const val ERROR_LOAD_PLAYLIST_SHELF = "Playlist shelf is fail or data not found"
        const val ERROR_CREATE_NEW_PLAYLIST = "Can't create new playlist"
        const val ERROR_UPLOAD_COVER_IMAGE = "Can't upload cover image"
        const val ERROR_DELETE_PLAYLIST = "Can't delete playlist"
        const val ERROR_REMOVE_TRACK = "Can't remove track"
    }

    override fun getPlaylist(playlistId: String): Flow<Playlist> {
        return flow {
            val response = apiInterface.getPlaylist(playlistId)

            val result = if (response.isSuccessful) {
                response.body() ?: error(ERROR_LOAD_PLAYLIST)
            } else {
                error(ERROR_LOAD_PLAYLIST)
            }
            emit(result)
        }
    }

    override fun getMyPlaylists(): Flow<PagedResults<Playlist>?> {
        return flow {
            val response = apiInterface.getMyPlaylists()
            val result = if (response.isSuccessful && response.body() != null) {
                response.body()
            } else {
                error(ERROR_LOAD_PLAYLIST_SHELF)
            }
            emit(result)
        }
    }

    override fun postNewPlaylist(request: CreateNewPlaylistRequest): Flow<Playlist> {
        return flow {
            val response = apiInterface.postCreateNewPlaylist(request)
            val result = if (response.isSuccessful) {
                response.body() ?: error(ERROR_CREATE_NEW_PLAYLIST)
            } else {
                error(ERROR_CREATE_NEW_PLAYLIST)
            }
            emit(result)
        }
    }

    override fun uploadCoverImage(
        playlistId: Int,
        language: String,
        multiPart: MultipartBody.Part
    ): Flow<Any> {
        return flow {
            val response = apiInterface.uploadCoverImage(playlistId, language, multiPart)
            val result = if (response.isSuccessful) {
                Any()
            } else {
                error(ERROR_UPLOAD_COVER_IMAGE)
            }
            emit(result)
        }
    }

    override fun deletePlaylist(playlistId: Int): Flow<Any> {
        return flow {
            val response = apiInterface.deletePlaylist(playlistId)
            val result = if (response.isSuccessful) {
                Any()
            } else {
                error(ERROR_DELETE_PLAYLIST)
            }
            emit(result)
        }
    }

    override fun removeTrack(
        playlistId: Int,
        trackIds: List<Int>
    ): Flow<RemoveTrackResponse> {
        return flow {
            val response = apiInterface.removeTrack(playlistId, trackIds)
            val result = if (response.isSuccessful) {
                response.body() ?: error(ERROR_REMOVE_TRACK)
            } else {
                error(ERROR_REMOVE_TRACK)
            }
            emit(result)
        }
    }
}
