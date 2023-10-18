package com.truedigital.features.tuned.api.playlist

import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.playlist.model.response.PlaylistContext
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaylistServicesApiInterface {

    @GET("playlists/{id}")
    fun playlist(@Path("id") id: Int): Single<Playlist>

    @GET("playlists/{id}/context")
    fun userContext(@Path("id") id: Int): Single<PlaylistContext>

    @GET("collection/playlists")
    fun favouritedAndOwned(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Playlist>>

    @GET("collection/fav-playlists")
    fun favourited(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Playlist>>

    @PUT("collection/playlists/{id}")
    fun favourite(@Path("id") id: Int): Single<Any>

    @DELETE("collection/playlists/{id}")
    fun unfavourite(@Path("id") id: Int): Single<Any>
}
