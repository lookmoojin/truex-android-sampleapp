package com.truedigital.features.music.data.track.repository

import com.truedigital.features.music.api.MusicTrackApiInterface
import com.truedigital.features.tuned.data.track.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface MusicTrackRepository {
    fun getTrackList(playlistId: Int, offset: Int, count: Int): Flow<List<Track>>
    fun getMyPlaylistTrackList(playlistId: Int, offset: Int, count: Int): Flow<List<Track>>
}

class MusicTrackRepositoryImpl @Inject constructor(
    private val musicTrackApi: MusicTrackApiInterface
) : MusicTrackRepository {

    companion object {
        const val ERROR_LOAD_TRACK_LIST = "Track list is fail or data not found"
    }

    override fun getTrackList(playlistId: Int, offset: Int, count: Int): Flow<List<Track>> {
        return flow {
            val response = musicTrackApi.getTrackList(playlistId.toString(), offset, count)
            emit(
                if (response.isSuccessful && response.body()?.results.isNullOrEmpty().not()) {
                    response.body()?.results ?: emptyList()
                } else {
                    error(ERROR_LOAD_TRACK_LIST)
                }
            )
        }
    }

    override fun getMyPlaylistTrackList(
        playlistId: Int,
        offset: Int,
        count: Int
    ): Flow<List<Track>> {
        return flow {
            val response = musicTrackApi.getTrackList(playlistId.toString(), offset, count)
            val result = if (response.isSuccessful) {
                response.body()?.results ?: emptyList()
            } else {
                emptyList()
            }
            emit(result)
        }
    }
}
