package com.truedigital.features.truecloudv3.data.api

import com.truedigital.features.truecloudv3.data.model.CompleteUploadRequest
import com.truedigital.features.truecloudv3.data.model.CompleteUploadResponse
import com.truedigital.features.truecloudv3.data.model.InitUploadRequest
import com.truedigital.features.truecloudv3.data.model.InitialUploadResponse
import com.truedigital.features.truecloudv3.data.model.ReplaceUploadRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TrueCloudV3UploadInterface {

    @POST("v1/users/{ssoid}/file")
    suspend fun initialUpload(
        @Path("ssoid") ssoid: String,
        @Body request: InitUploadRequest
    ): Response<InitialUploadResponse>

    @PATCH("v1/users/{ssoid}/files/{id}")
    suspend fun completeUpload(
        @Path("ssoid") ssoid: String,
        @Path("id") id: String,
        @Body request: CompleteUploadRequest
    ): Response<CompleteUploadResponse>

    @PATCH("v1/users/{ssoid}/files/{id}")
    suspend fun completeReplaceUpload(
        @Path("ssoid") ssoid: String,
        @Path("id") id: String,
        @Body request: ReplaceUploadRequest
    ): Response<CompleteUploadResponse>
}
