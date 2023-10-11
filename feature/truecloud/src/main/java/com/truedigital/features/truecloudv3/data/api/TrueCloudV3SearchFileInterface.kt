package com.truedigital.features.truecloudv3.data.api

import com.truedigital.features.truecloudv3.data.model.SearchFileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TrueCloudV3SearchFileInterface {
    @GET("v1/users/{ssoid}/objects/search")
    suspend fun searchFile(
        @Path("ssoid") ssoid: String,
        @Query("skip") skip: Int,
        @Query("take") take: Int,
        @Query("sort") sort: String,
        @Query("name") name: String
    ): Response<SearchFileResponse>

    @GET("v1/users/{ssoid}/objects/search")
    suspend fun searchFileWithCategory(
        @Path("ssoid") ssoid: String,
        @Query("skip") skip: Int,
        @Query("take") take: Int,
        @Query("sort") sort: String,
        @Query("name") name: String,
        @Query("category") category: String
    ): Response<SearchFileResponse>
}
