package com.truedigital.features.tuned.api.station

import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StationMetadataApiInterface {
    @GET("stations/{id}")
    fun station(@Path("id") stationId: Int): Single<Station>

    @GET("stations")
    fun presetStations(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Station>>

    @GET("artists/{id}/stations")
    fun containingArtist(@Path("id") artistId: Int): Single<List<Station>>

    @GET("stations/{id}/similar")
    fun similarStations(@Path("id") stationId: Int): Single<List<Station>>

    @GET("stations/trending")
    fun trendingStations(): Single<List<Station>>
}
