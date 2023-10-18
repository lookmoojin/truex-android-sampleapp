package com.truedigital.features.tuned.api.track

import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.response.TrackContext
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TrackServiceApiInterface {
    @GET("collection/tracks")
    fun favourited(
        @Query("offset") offset: Int = 1,
        @Query("count") count: Int = 1,
        @Query("type") type: String
    ): Single<PagedResults<Track>>

    @GET("tracks/{id}/context")
    fun userContext(@Path("id") id: Int): Single<TrackContext>

    @PUT("collection/tracks/{id}")
    fun favourite(@Path("id") id: Int): Single<Any>

    @DELETE("collection/tracks/{id}")
    fun unfavourite(@Path("id") id: Int): Single<Any>
}
