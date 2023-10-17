package com.truedigital.features.music.data.trending.repository

import com.truedigital.features.music.api.MusicTrendingApi
import com.truedigital.features.music.data.trending.model.response.album.MusicTrendingAlbumResponse
import com.truedigital.features.music.data.trending.model.response.artist.MusicTrendingArtistResponse
import com.truedigital.features.music.data.trending.model.response.playlist.MusicTrendingPlaylistResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface MusicTrendingRepository {
    fun getMusicTrendingArtists(): Flow<MusicTrendingArtistResponse?>
    fun getMusicTrendingAlbum(): Flow<MusicTrendingAlbumResponse?>
    fun getMusicTrendingPlaylist(): Flow<MusicTrendingPlaylistResponse?>
}

@Suppress("UNREACHABLE_CODE")
class MusicTrendingRepositoryImpl @Inject constructor(
    val musicTrendingApi: MusicTrendingApi
) : MusicTrendingRepository {

    companion object {
        const val ERROR_TRENDING_ARTIST_API =
            "Retrieving Trending artists data is fail or data 404 not found"
        const val ERROR_TRENDING_PLAYLIST_API =
            "Retrieving Trending playlist data is fail or data 404 not found"
        const val ERROR_TRENDING_ALBUM_API =
            "Retrieving Trending album data is fail or data 404 not found"
    }

    override fun getMusicTrendingArtists() = flow {
        try {
            val response = musicTrendingApi.getMusicTrendingArtists()

            if ((!response.isSuccessful) || response.body() == null) {
                emit(error(Throwable(ERROR_TRENDING_ARTIST_API)))
            } else {
                val contentResponse = response.body()
                emit(contentResponse)
            }
        } catch (error: Exception) {
            emit(error(Throwable(error.localizedMessage)))
        }
    }

    override fun getMusicTrendingAlbum() = flow {
        try {
            val response = musicTrendingApi.getMusicTrendingAlbum()

            if ((!response.isSuccessful) || response.body() == null) {
                emit(error(Throwable(ERROR_TRENDING_ALBUM_API)))
            } else {
                val contentResponse = response.body()
                emit(contentResponse)
            }
        } catch (error: Exception) {
            emit(error(Throwable(error.localizedMessage)))
        }
    }

    override fun getMusicTrendingPlaylist(): Flow<MusicTrendingPlaylistResponse?> = flow {
        try {
            val response = musicTrendingApi.getMusicTrendingPlaylist()

            if (!response.isSuccessful || response.body() == null) {
                emit(error(Throwable(ERROR_TRENDING_PLAYLIST_API)))
            } else {
                val contentResponse = response.body()
                emit(contentResponse)
            }
        } catch (error: Exception) {
            emit(error(Throwable(error.localizedMessage)))
        }
    }
}
