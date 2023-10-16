package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.api

import androidx.annotation.WorkerThread
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WeMallShelfApiInterface {
    @WorkerThread
    @GET("personalize-rcom/v1/trueid/external/wemall")
    suspend fun getWeMallShelfComponent(
        @Header("Authorization") authorization: String,
        @Query("category_name") categoryName: String,
        @Query("deviceId") deviceId: String,
        @Query("ssoId") ssoId: String,
        @Query("lang") lang: String,
        @Query("limit") limit: String
    ): WeMallShelfResponseModel
}
