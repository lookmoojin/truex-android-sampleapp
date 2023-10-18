package com.truedigital.common.share.data.coredata.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CmsFnCounterApiInterface {

    @GET("cms-fncounter/v1/count")
    suspend fun getCountView(@Query("id") id: String): Response<Unit>
}
