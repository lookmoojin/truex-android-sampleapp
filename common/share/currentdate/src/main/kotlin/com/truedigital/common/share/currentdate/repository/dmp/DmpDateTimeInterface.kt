package com.truedigital.common.share.currentdate.repository.dmp

import androidx.annotation.WorkerThread
import com.truedigital.common.share.currentdate.ServerDateTimeModel
import retrofit2.Response
import retrofit2.http.GET

interface DmpDateTimeInterface {

    @WorkerThread
    @GET("apim-currenttime/")
    suspend fun getServerDateTime(): Response<ServerDateTimeModel>
}
