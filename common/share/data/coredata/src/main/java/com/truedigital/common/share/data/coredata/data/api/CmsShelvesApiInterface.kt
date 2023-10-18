package com.truedigital.common.share.data.coredata.data.api

import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.CmsShelfListResponse
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.CmsShelfResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CmsShelvesApiInterface {
    @GET("cms-public-content/v2/shelves/{id}")
    suspend fun getCmsShelfList(
        @Path("id") shelfId: String,
        @Query("country") country: String,
        @Query("fields") fields: String,
        @Query("lang") lang: String?,
        @Query("page") page: String? = null,
        @Query("limit") limit: String? = null
    ): Response<CmsShelfResponse>

    @GET("cms-public-content/v2/shelves/{id}")
    suspend fun getCmsShelfListWithSlug(
        @Path("id") shelfId: String,
        @Query("country") country: String,
        @Query("fields") fields: String,
        @Query("lang") lang: String?,
        @Query("page") page: String?,
        @Query("limit") limit: String?,
        @Query("shelf_slug") shelfSlug: String? = null
    ): Response<CmsShelfResponse>

    @GET("cms-public-content/v2/shelves/{id}")
    suspend fun getCmsPublicContentShelfList(
        @Path("id") shelfId: String,
        @Query("country") country: String,
        @Query("fields") fields: String,
        @Query("lang") lang: String?,
        @Query("page") page: String?,
        @Query("limit") limit: String?
    ): Response<CmsShelfResponse>

    @GET("cms-public-content/v1/progressive-shelves/{id}")
    suspend fun getCmsProgressiveShelfList(
        @Path("id") shelfId: String,
        @Query("country") country: String,
        @Query("fields") fields: String,
        @Query("lang") lang: String?,
        @Query("limit") limit: String?,
        @Query("merchant_type") merchantType: String? = null,
        @Query("article_category") articleCategory: String? = null
    ): Response<CmsShelfResponse>

    @GET("cms-public-content/v1/progressive-shelves/{id}")
    suspend fun getCmsProgressiveShelfListWithExpand(
        @Path("id") shelfId: String,
        @Query("country") country: String,
        @Query("expand") expand: String,
        @Query("fields") fields: String,
        @Query("lang") lang: String?,
        @Query("limit") limit: String?
    ): Response<CmsShelfResponse>

    @GET("cms-public-content/v2/contents")
    suspend fun getCmsShelfContentsList(
        @Query("shelf_code") shelfId: String?,
        @Query("country") country: String,
        @Query("fields") fields: String?,
        @Query("lang") lang: String?,
        @Query("page") page: String?,
        @Query("limit") limit: String?,
        @Query("article_category") articleCategory: String?,
        @Query("content_type") contentType: String?,
        @Query("movie_type") movieType: String?,
        @Query("ep_master") epMaster: String?,
        @Query("is_vod_layer") isVodLayer: String?
    ): Response<CmsShelfListResponse>
}
