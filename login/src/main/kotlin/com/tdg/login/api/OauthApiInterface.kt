package com.tdg.login.api

import com.tdg.login.data.model.OauthResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OauthApiInterface {

    @Multipart
    @POST("auth/v4/oauth2/token?jwt_version=2")
    suspend fun login(
        @Part("client_id") clientId: RequestBody,
        @Part("client_secret") clientSecret: RequestBody,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("grant_type") grantType: RequestBody,
        @Part("scope") scope: RequestBody,
        @Part("device_id") deviceId: RequestBody,
        @Part("device_model") deviceModel: RequestBody,
        @Part("latlong") latlong: RequestBody,
        @Part("ip_address") ipAddress: RequestBody
    ): Response<OauthResponse>
}
