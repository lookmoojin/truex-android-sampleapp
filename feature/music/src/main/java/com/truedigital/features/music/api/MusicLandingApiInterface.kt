package com.truedigital.features.music.api

import androidx.annotation.WorkerThread
import com.truedigital.features.music.data.landing.model.response.MusicForYouShelfResponse
import com.truedigital.features.music.data.landing.model.response.playlisttrack.PlaylistTrackResponse
import com.truedigital.features.music.data.landing.model.response.tagalbum.TagAlbumResponse
import com.truedigital.features.music.data.landing.model.response.tagartist.TagArtistResponse
import com.truedigital.features.music.data.landing.model.response.tagname.TagNameResponse
import com.truedigital.features.music.data.landing.model.response.tagplaylist.TagPlaylistResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicLandingApiInterface {

    @WorkerThread
    @GET("api/v2/contents/{path}")
    suspend fun getMusicForYouShelf(
        @Path("path") apiPath: String
    ): Response<MusicForYouShelfResponse>

    @WorkerThread
    @GET("api/v2.1/tags/albums")
    suspend fun getTagAlbum(
        @Query("tag") tag: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Response<TagAlbumResponse>

    @WorkerThread
    @GET("api/v2.1/tags/artists")
    suspend fun getTagArtist(
        @Query("tag") tag: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Response<TagArtistResponse>

    @WorkerThread
    @GET("api/v2.1/tags/playlists")
    suspend fun getTagPlaylist(
        @Query("tag") tag: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("type") type: String
    ): Response<TagPlaylistResponse>

    @WorkerThread
    @GET("api/v2/playlists/{id}/tracks")
    suspend fun getPlaylistTrack(
        @Path("id") id: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Response<PlaylistTrackResponse>

    @WorkerThread
    @GET("api/v2/tags")
    suspend fun getTagByName(
        @Query("name") name: String
    ): Response<TagNameResponse>
}
