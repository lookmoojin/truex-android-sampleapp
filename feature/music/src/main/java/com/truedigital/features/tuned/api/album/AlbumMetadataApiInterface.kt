package com.truedigital.features.tuned.api.album

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AlbumMetadataApiInterface {
    @GET("albums/{id}")
    fun album(@Path("id") id: Int): Single<Album>

    @GET("artists/{id}/albums?sortType=newrelease")
    fun artistAlbums(@Path("id") id: Int): Single<PagedResults<Album>>

    @GET("albums/trending")
    fun trendingAlbums(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Album>>

    @GET("albums/new")
    fun newReleases(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Album>>

    @GET("releases/{id}/tracks")
    fun releaseTracks(@Path("id") id: Int, @Query("type") type: String): Single<List<Track>>
}
