package com.truedigital.common.share.currentdate.repository.nondmp

import com.truedigital.common.share.currentdate.ServerDateTimeModel
import retrofit2.Response
import retrofit2.http.GET

interface NonDmpCurrentDateTimeInterface {

    @GET("getFirebaseDateTime")
    suspend fun getCurrentDateTime(): Response<ServerDateTimeModel>
}
