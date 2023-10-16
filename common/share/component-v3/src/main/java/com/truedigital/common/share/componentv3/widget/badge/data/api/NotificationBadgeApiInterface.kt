package com.truedigital.common.share.componentv3.widget.badge.data.api

import com.truedigital.common.share.componentv3.widget.badge.data.model.CountCategoriesInboxResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface NotificationBadgeApiInterface {
    @GET("notify-customer/v1/accounts/{ssoId}/messages/count")
    suspend fun getCountInboxMessage(
        @Path("ssoId") appId: String
    ): CountCategoriesInboxResponse
}
