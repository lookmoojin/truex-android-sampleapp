package com.truedigital.features.tuned.api.steam

import com.truedigital.features.tuned.data.station.model.request.OfflinePlaybackState
import com.truedigital.features.tuned.data.station.model.request.PlaybackState
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface StreamApiInterface {

    @POST("plays/{deviceId}/{id}/stream")
    fun streamLocation(
        @Header("session_id") sessionId: String?,
        @Path("deviceId") deviceId: Int,
        @Path("id") trackId: Int
    ): Single<String>

    @GET("stakkars/{id}/streamlocation")
    fun stakkarStreamLocation(@Path("id") stakkarId: Int): Single<String>

    @POST("plays/{deviceId}")
    fun logPlay(@Path("deviceId") deviceId: Int, @Body log: PlaybackState): Single<Any>

    @POST("plays/{deviceId}/offline")
    fun logOfflinePlay(
        @Path("deviceId") deviceId: Int,
        @Body logs: List<OfflinePlaybackState>
    ): Single<Any>
}
