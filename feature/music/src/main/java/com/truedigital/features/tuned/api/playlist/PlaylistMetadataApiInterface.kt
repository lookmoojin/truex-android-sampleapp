package com.truedigital.features.tuned.api.playlist

import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaylistMetadataApiInterface {

    @GET("playlists/{id}")
    fun playlist(@Path("id") id: Int): Single<Playlist>

    @GET("playlists/trending")
    fun trendingPlaylists(
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("type") type: String
    ): Single<PagedResults<Playlist>>

    @GET("playlists/{id}/tracks")
    fun getTracks(
        @Path("id") id: Int,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Track>>
}
