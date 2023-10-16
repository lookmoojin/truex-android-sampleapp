package com.truedigital.common.share.data.coredata.data.api

import com.truedigital.common.share.datalegacy.data.recommend.model.response.RecommendedResponse
import com.truedigital.common.share.datalegacy.data.similar.SimilarResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PersonalizeApiInterface {
    @GET("personalize-rcom/v1/trueid/personalize/{content_type}")
    suspend fun getRecommendedShelf(
        @Path("content_type") contentType: String,
        @Query("deviceId") deviceId: String,
        @Query("ssoId") ssoId: String,
        @Query("maxItems") maxItems: String,
        @Query("language") language: String,
        @Query("is_vod_layer") isVodLayer: String?
    ): Response<RecommendedResponse>

    @GET("personalize-rcom/v1/trueid/similar/movie")
    suspend fun getYouMightAlsoLikeShelf(
        @Query("ssoId") ssoId: String,
        @Query("deviceId") deviceId: String,
        @Query("globalItemId") globalItemId: String,
        @Query("maxItems") maxItems: String,
        @Query("content_rights") contentRights: String,
        @Query("language") language: String
    ): Response<RecommendedResponse>

    @GET("personalize-rcom/v1/trueid/similar/series")
    suspend fun getSimilarShow(
        @Query("ssoId") ssoId: String,
        @Query("deviceId") deviceId: String,
        @Query("globalItemId") globalItemId: String,
        @Query("maxItems") maxItems: String,
        @Query("content_rights") contentRights: String,
        @Query("language") language: String,
        @Query("is_vod_layer") isVodLayer: String?
    ): Response<SimilarResponse>

    @GET("personalize-rcom/v1/trueid/similar/movie")
    suspend fun getSimilarMovie(
        @Query("ssoId") ssoId: String,
        @Query("deviceId") deviceId: String,
        @Query("globalItemId") globalItemId: String,
        @Query("maxItems") maxItems: String,
        @Query("content_rights") contentRights: String,
        @Query("language") language: String,
        @Query("is_vod_layer") isVodLayer: String?
    ): Response<SimilarResponse>
}
