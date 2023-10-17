package com.truedigital.features.music.api

import androidx.annotation.WorkerThread
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.PagedResults
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicTrackApiInterface {

    @WorkerThread
    @GET("api/v2/playlists/{id}/tracks")
    suspend fun getTrackList(
        @Path("id") id: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Response<PagedResults<Track>>
}
