package com.truedigital.common.share.data.coredata.data.api

import com.truedigital.common.share.data.coredata.data.model.response.ContentDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CmsContentApiInterface {

    @GET("cms-public-content/v2/contents/{cmsId}")
    suspend fun getCmsContentDetails(
        @Path("cmsId") cmsId: String,
        @Query("country") country: String,
        @Query("lang") lang: String,
        @Query("fields") fields: String,
        @Query("expand") expand: String
    ): Response<ContentDetailResponse>
}
