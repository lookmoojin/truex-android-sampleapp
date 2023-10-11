package com.truedigital.features.truecloudv3.data.api

import com.truedigital.features.truecloudv3.data.model.InitialDownloadResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TrueCloudV3DownloadInterface {

    @GET("v1/users/{ssoid}/files/{objectId}")
    suspend fun initialDownload(
        @Path("ssoid") ssoid: String,
        @Path("objectId") objectId: String
    ): Response<InitialDownloadResponse>
}
