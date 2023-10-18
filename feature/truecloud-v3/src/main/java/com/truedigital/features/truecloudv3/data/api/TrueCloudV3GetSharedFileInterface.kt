package com.truedigital.features.truecloudv3.data.api

import com.truedigital.features.truecloudv3.data.model.GetSharedFileResponseModel
import com.truedigital.features.truecloudv3.data.model.GetSharedObjectAccessTokenRequestModel
import com.truedigital.features.truecloudv3.data.model.SharedObjectAccessResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface TrueCloudV3GetSharedFileInterface {
    @GET("v1/shared-objects/{encryptedSharedObjectId}")
    suspend fun getPublicSharedFile(
        @Path("encryptedSharedObjectId") encryptedSharedObjectId: String
    ): Response<GetSharedFileResponseModel>

    @GET("v1/shared-objects/{encryptedSharedObjectId}")
    suspend fun getPrivateSharedFile(
        @Path("encryptedSharedObjectId") encryptedSharedObjectId: String,
        @Header("Authorization") sharedObjectAccessToken: String
    ): Response<GetSharedFileResponseModel>

    @POST("v1/auth/shared-objects/login")
    suspend fun getSharedObjectAccessToken(
        @Body obj: GetSharedObjectAccessTokenRequestModel
    ): Response<SharedObjectAccessResponseModel>
}
