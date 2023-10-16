package com.truedigital.component.widget.livecommerce.data.api

import com.truedigital.component.widget.livecommerce.data.model.CommerceActiveLiveStreamResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AmityActiveLiveStreamApi {
    @GET("community-public/community/v1/lives")
    suspend fun getLiveGroup(
        @Query("userIds") userIds: String
    ): Response<CommerceActiveLiveStreamResponse>
}
