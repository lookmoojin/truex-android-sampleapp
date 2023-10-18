package com.truedigital.features.music.data.search.repository

import com.truedigital.features.music.api.SearchMusicApi
import com.truedigital.features.music.data.search.model.response.MusicSearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

interface MusicSearchRepository {
    fun getSearchQuery(query: String): Flow<MusicSearchResponse?>
    fun getSearchArtistQuery(
        query: String,
        offset: String,
        count: String
    ): Flow<MusicSearchResponse?>

    fun getSearchPlaylistQuery(
        query: String,
        offset: String,
        count: String
    ): Flow<MusicSearchResponse?>

    fun getSongQuery(
        query: String,
        offset: String,
        count: String
    ): Flow<MusicSearchResponse?>

    suspend fun getSongQueryStream(
        query: String,
        offset: String,
        count: String
    ): MusicSearchResponse?

    fun getAlbumQuery(
        query: String,
        offset: String,
        count: String
    ): Flow<MusicSearchResponse?>
}

@Suppress("UNREACHABLE_CODE")
class MusicSearchRepositoryImpl @Inject constructor(private val musicSearchApi: SearchMusicApi) :
    MusicSearchRepository {

    companion object {
        private const val KEY_MUSIC_ALL = "song,album,artist,playlist"
        private const val ERROR_MUSIC_API = "Retrieving Music data is fail or data 404 not found"
    }

    override fun getSearchQuery(query: String) = flow {
        try {
            val response = musicSearchApi.getSearchQuery(
                query = query,
                key = KEY_MUSIC_ALL
            )
            if ((!response.isSuccessful) || response.body() == null) {
                emit(error(Throwable(ERROR_MUSIC_API)))
            } else {
                val contentResponse = response.body()
                emit(contentResponse)
            }
        } catch (error: Exception) {
            emit(error(Throwable(error.localizedMessage)))
        }
    }

    override fun getSearchArtistQuery(query: String, offset: String, count: String) = flow {
        try {
            val response = musicSearchApi.getArtistQuery(
                query = query, offset = offset, count = count
            )
            if ((!response.isSuccessful) || response.body() == null) {
                emit(error(Throwable(ERROR_MUSIC_API)))
            } else {
                val contentResponse = response.body()
                emit(contentResponse)
            }
        } catch (error: Exception) {
            emit(error(Throwable(error.localizedMessage)))
        }
    }

    override fun getSearchPlaylistQuery(query: String, offset: String, count: String) =
        flow {
            try {
                val response = musicSearchApi.getPlaylistQuery(
                    query = query, offset = offset, count = count
                )
                if ((!response.isSuccessful) || response.body() == null) {
                    emit(error(Throwable(ERROR_MUSIC_API)))
                } else {
                    val contentResponse = response.body()
                    emit(contentResponse)
                }
            } catch (error: Exception) {
                emit(error(Throwable(error.localizedMessage)))
            }
        }

    override fun getSongQuery(query: String, offset: String, count: String) = flow {
        try {
            val response = musicSearchApi.getSongQuery(
                query = query, offset = offset, count = count
            )
            if ((!response.isSuccessful) || response.body() == null) {
                emit(error(Throwable(ERROR_MUSIC_API)))
            } else {
                val contentResponse = response.body()
                emit(contentResponse)
            }
        } catch (error: Exception) {
            emit(error(Throwable(error.localizedMessage)))
        }
    }

    override suspend fun getSongQueryStream(
        query: String,
        offset: String,
        count: String
    ): MusicSearchResponse? {
        return try {
            val response = musicSearchApi.getSongQuery(
                query = query, offset = offset, count = count
            )
            if ((response.isSuccessful) || response.body() != null) {
                response.body()
            } else {
                null
            }
        } catch (error: Exception) {
            Timber.e(error)
            null
        }
    }

    override fun getAlbumQuery(query: String, offset: String, count: String) = flow {
        try {
            val response = musicSearchApi.getAlbumQuery(
                query = query, offset = offset, count = count
            )
            if ((!response.isSuccessful) || response.body() == null) {
                emit(error(Throwable(ERROR_MUSIC_API)))
            } else {
                val contentResponse = response.body()
                emit(contentResponse)
            }
        } catch (error: Exception) {
            emit(error(Throwable(error.localizedMessage)))
        }
    }
}
