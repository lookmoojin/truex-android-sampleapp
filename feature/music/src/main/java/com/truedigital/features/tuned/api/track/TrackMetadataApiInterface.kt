package com.truedigital.features.tuned.api.track

import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.response.RawLyricString
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TrackMetadataApiInterface {
    @GET("tracks/{id}")
    fun get(@Path("id") id: Int): Single<Track>

    @POST("tracks/get")
    fun getMultiple(@Body ids: List<Int>): Single<List<Track>>

    @GET("songs/{id}/lyrics")
    fun getLyrics(@Path("id") id: Int): Single<RawLyricString>

    @POST("tracks/validatetracks")
    fun validateTracks(@Body ids: List<Int>): Single<List<Int>>
}
