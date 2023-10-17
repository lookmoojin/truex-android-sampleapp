package com.truedigital.features.tuned.api.album

import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.album.model.response.AlbumContext
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AlbumReleaseApiInterface {
    @GET("releases/{id}/context")
    fun userContext(@Path("id") id: Int): Single<AlbumContext>

    @GET("collection/releases")
    fun favourited(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Release>>

    @PUT("collection/releases/{id}")
    fun favourite(@Path("id") id: Int): Single<Any>

    @DELETE("collection/releases/{id}")
    fun unFavourite(@Path("id") id: Int): Single<Any>
}
