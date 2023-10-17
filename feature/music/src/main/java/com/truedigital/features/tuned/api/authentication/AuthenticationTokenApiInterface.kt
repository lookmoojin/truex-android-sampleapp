package com.truedigital.features.tuned.api.authentication

import com.truedigital.features.tuned.data.authentication.model.response.AccessToken
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface AuthenticationTokenApiInterface {

    @POST
    fun getTokenByJwt(
        @Url grantUrl: String,
        @Header("Authorization") userJwt: String,
        @Header("UniqueId") uniqueId: String,
        @Header("ApplicationId") applicationId: Int
    ): Single<AccessToken>

    @FormUrlEncoded
    @POST
    fun getTokenByLogin(
        @Url grantUrl: String,
        @Field("grant_type") grantType: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Header("UniqueId") uniqueId: String,
        @Header("ApplicationId") applicationId: Int
    ): Single<AccessToken>

    @FormUrlEncoded
    @POST
    fun refreshToken(
        @Url grantUrl: String,
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String
    ): Single<AccessToken>
}
