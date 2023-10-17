package com.truedigital.features.music.api

import androidx.annotation.WorkerThread
import com.truedigital.features.tuned.data.authentication.model.response.AccessToken
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MusicAuthenticationApiInterface {

    @WorkerThread
    @FormUrlEncoded
    @POST("oauth2/token")
    suspend fun refreshToken(
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String
    ): Response<AccessToken>
}
