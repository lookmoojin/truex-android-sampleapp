package com.truedigital.features.tuned.api.profile

import com.truedigital.features.tuned.data.profile.model.Profile
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileMetadataApiInterface {
    @GET("users/{id}/profile")
    fun getProfile(@Path("id") id: String): Single<Profile>
}
