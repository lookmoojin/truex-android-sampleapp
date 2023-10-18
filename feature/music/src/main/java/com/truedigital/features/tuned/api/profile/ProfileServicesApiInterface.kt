package com.truedigital.features.tuned.api.profile

import com.truedigital.features.tuned.data.profile.model.response.ProfileContext
import io.reactivex.Single
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileServicesApiInterface {
    @GET("users/{id}")
    fun userContext(@Path("id") id: Int): Single<ProfileContext>

    @PUT("users/me/following/{id}")
    fun followProfile(@Path("id") id: Int): Single<Any>

    @DELETE("users/me/following/{id}")
    fun unfollowProfile(@Path("id") id: Int): Single<Any>
}
