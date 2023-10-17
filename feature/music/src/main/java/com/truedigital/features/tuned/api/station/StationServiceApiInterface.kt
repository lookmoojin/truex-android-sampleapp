package com.truedigital.features.tuned.api.station

import com.truedigital.features.tuned.data.station.model.LikedTrack
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.StationTrack
import com.truedigital.features.tuned.data.station.model.StationVote
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.data.station.model.response.Stakkar
import com.truedigital.features.tuned.data.station.model.response.StationContext
import com.truedigital.features.tuned.data.station.model.response.TrackExtras
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface StationServiceApiInterface {
    @GET("stations/getmylastplayed")
    fun lastPlayed(): Single<List<Station>>

    @POST("stations/addbyseed")
    fun addBySeed(@Query("id") seedId: Int, @Query("type") seedType: String): Single<Station>

    @POST("stations/{id}/gettracks")
    fun stationTracks(
        @Path("id") stationId: Int,
        @Query("count") count: Int,
        @Query("trackHash") trackHash: String?
    ): Single<List<StationTrack>>

    @GET("stations/{id}/trackextras")
    fun stationTrackExtras(
        @Path("id") stationId: Int,
        @Query("deviceId") uniqueId: Int,
        @Query("nextTrackId") nextTrackId: Int?,
        @Query("previousTrackId") previousTrackId: Int?,
        @Query("includeAds") includeAds: Boolean,
        @Query("adProvider") adProvider: String,
        @Query("tritonLsid") lsid: String
    ): Single<TrackExtras>

    @GET("stations/{id}/tracks")
    fun stationUserVotes(
        @Path("id") stationId: Int,
        @Query("userId") userId: Int,
        @Query("voteType") voteType: String
    ): Single<List<LikedTrack>>

    @POST("stations/{id}/getsynctracks")
    fun stationSyncTracks(@Path("id") stationId: Int): Single<List<StationTrack>>

    @PUT("stations/{id}/vote")
    fun stationVote(
        @Path("id") stationId: Int,
        @Body userVotes: List<Vote>
    ): Single<List<StationVote>>

    @GET("stations/{id}/context")
    fun userContext(@Path("id") id: Int): Single<StationContext>

    @GET("collection/stations")
    fun favouritedStations(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Station>>

    @PUT("collection/stations/{id}")
    fun favouriteStation(@Path("id") stationId: Int): Single<Any>

    @DELETE("collection/stations/{id}")
    fun unfavouriteStation(@Path("id") stationId: Int): Single<Any>

    @GET("stakkars/{id}")
    fun stakkar(@Path("id") stakkarId: Int): Single<Stakkar>

    @DELETE("stations/{id}/votes")
    fun deleteVotes(@Path("id") stationId: Int): Single<Any>
}
