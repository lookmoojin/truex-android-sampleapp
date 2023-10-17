package com.truedigital.features.music.api

import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.search.model.response.MusicSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchMusicApi {

    /*
    Get full path ({domain}/api/v2/search) from "music-searchall" node in firebase
    **** Not support @Path ****
    */
    @GET(MusicConstant.Api.PATH_MUSIC_SEARCH_ALL)
    suspend fun getSearchQuery(
        @Query("q") query: String,
        @Query("key") key: String
    ): Response<MusicSearchResponse>

    @GET(MusicConstant.Api.PATH_MUSIC_SEARCH_ALBUM)
    suspend fun getAlbumQuery(
        @Query("q") query: String,
        @Query("offset") offset: String,
        @Query("count") count: String
    ): Response<MusicSearchResponse>

    @GET(MusicConstant.Api.PATH_MUSIC_SEARCH_ARTIST)
    suspend fun getArtistQuery(
        @Query("q") query: String,
        @Query("offset") offset: String,
        @Query("count") count: String
    ): Response<MusicSearchResponse>

    @GET(MusicConstant.Api.PATH_MUSIC_SEARCH_SONG)
    suspend fun getSongQuery(
        @Query("q") query: String,
        @Query("offset") offset: String,
        @Query("count") count: String
    ): Response<MusicSearchResponse>

    @GET(MusicConstant.Api.PATH_MUSIC_SEARCH_PLAYLIST)
    suspend fun getPlaylistQuery(
        @Query("q") query: String,
        @Query("offset") offset: String,
        @Query("count") count: String
    ): Response<MusicSearchResponse>
}
