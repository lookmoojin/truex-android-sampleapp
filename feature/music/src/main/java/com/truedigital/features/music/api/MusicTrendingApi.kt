package com.truedigital.features.music.api

import androidx.annotation.WorkerThread
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.trending.model.response.album.MusicTrendingAlbumResponse
import com.truedigital.features.music.data.trending.model.response.artist.MusicTrendingArtistResponse
import com.truedigital.features.music.data.trending.model.response.playlist.MusicTrendingPlaylistResponse
import retrofit2.Response
import retrofit2.http.GET

interface MusicTrendingApi {

    @WorkerThread
    @GET(MusicConstant.Api.PATH_MUSIC_TRENDING_ARTISTS)
    suspend fun getMusicTrendingArtists(): Response<MusicTrendingArtistResponse>

    @WorkerThread
    @GET(MusicConstant.Api.PATH_MUSIC_TRENDING_ALBUM)
    suspend fun getMusicTrendingAlbum(): Response<MusicTrendingAlbumResponse>

    @WorkerThread
    @GET(MusicConstant.Api.PATH_MUSIC_TRENDING_PLAYLIST)
    suspend fun getMusicTrendingPlaylist(): Response<MusicTrendingPlaylistResponse>
}
