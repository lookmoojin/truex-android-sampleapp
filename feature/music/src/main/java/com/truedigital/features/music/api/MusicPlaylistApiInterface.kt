package com.truedigital.features.music.api

import androidx.annotation.WorkerThread
import com.truedigital.features.music.data.playlist.model.CreateNewPlaylistRequest
import com.truedigital.features.music.data.playlist.model.response.RemoveTrackResponse
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.util.PagedResults
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicPlaylistApiInterface {

    @WorkerThread
    @GET("music-services/playlists/{id}")
    suspend fun getPlaylist(
        @Path("id") id: String
    ): Response<Playlist>

    @WorkerThread
    @GET("music-services/playlists")
    suspend fun getMyPlaylists(
        @Query("Count") count: Int = 100,
        @Query("Offset") offset: Int = 1
    ): Response<PagedResults<Playlist>>

    @WorkerThread
    @POST("music-services/playlists")
    suspend fun postCreateNewPlaylist(
        @Body createNewPlaylistRequest: CreateNewPlaylistRequest
    ): Response<Playlist>

    @WorkerThread
    @POST("music-services/playlists/{id}/tracks")
    suspend fun addTracksToPlaylist(
        @Path("id") id: String,
        @Body trackIds: List<Int>
    ): Response<Playlist>

    @WorkerThread
    @Multipart
    @PUT("music-services/playlists/{id}/image/cover")
    suspend fun uploadCoverImage(
        @Path("id") id: Int,
        @Query("language") language: String,
        @Part image: MultipartBody.Part
    ): Response<Any>

    @WorkerThread
    @DELETE("music-services/playlists/{id}")
    suspend fun deletePlaylist(
        @Path("id") id: Int
    ): Response<Any>

    @WorkerThread
    @HTTP(method = "DELETE", path = "music-services/playlists/{id}/tracks", hasBody = true)
    suspend fun removeTrack(
        @Path("id") id: Int,
        @Body trackIds: List<Int>
    ): Response<RemoveTrackResponse>
}
