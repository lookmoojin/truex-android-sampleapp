package com.truedigital.features.tuned.api.artist

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.model.ArtistPlayCount
import com.truedigital.features.tuned.data.artist.model.response.ArtistProfile
import com.truedigital.features.tuned.data.track.model.PrimaryTrack
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArtistMetadataApiInterface {

    @GET("artists/{id}")
    fun artist(@Path("id") id: Int): Single<ArtistProfile>

    @GET("artists/{id}/counts")
    fun playCount(@Path("id") id: Int): Single<ArtistPlayCount>

    @GET("artists/trending")
    fun trendingArtists(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Artist>>

    @GET("stations/stationtrendingartists")
    fun stationTrendingArtists(@Query("id") stationId: Int): Single<List<Artist>>

    @GET("artists/{id}/similar")
    fun similarArtists(@Path("id") id: Int): Single<List<Artist>>

    @GET("artists/{id}/appearson")
    fun getAppearsOn(
        @Path("id") id: Int,
        @Query("sortType") sortType: String?
    ): Single<PagedResults<Album>>

    @GET("artists/{id}/albums")
    fun getAlbums(
        @Path("id") id: Int,
        @Query("sortType") sortType: String?,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Album>>

    @GET("artists/{id}/songs")
    fun getTracks(
        @Path("id") id: Int,
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("type") type: String?,
        @Query("sortType") sortType: String?,
        @Query("releasedInDays") releasedInDays: Int?
    ): Single<PagedResults<PrimaryTrack>>
}
