package com.truedigital.features.tuned.api.artist

import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.model.response.ArtistContext
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ArtistServicesApiInterface {

    @GET("artists/{id}/context")
    fun userContext(@Path("id") id: Int): Single<ArtistContext>

    @GET("collection/followedartists")
    fun followedArtists(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Artist>>

    @PUT("collection/followedartists/{id}")
    fun followArtist(@Path("id") id: Int): Single<Any>

    @DELETE("collection/followedartists/{id}")
    fun unfollowArtist(@Path("id") id: Int): Single<Any>

    @POST("stations/clearartistvotes")
    fun clearArtistVotes(
        @Query("artistId") id: Int,
        @Query("voteType") voteType: String
    ): Single<Any>

    @GET("users/me/recommendedartist")
    fun recommendedArtists(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Artist>>
}
