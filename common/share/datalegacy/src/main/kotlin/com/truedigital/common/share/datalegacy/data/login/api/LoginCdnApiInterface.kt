package com.truedigital.common.share.datalegacy.data.login.api

import com.truedigital.common.share.datalegacy.data.login.model.DefaultUrlConfigResponse
import retrofit2.Response
import retrofit2.http.GET

interface LoginCdnApiInterface {
    @GET("default_url_config.json")
    suspend fun getCdnUrlConfig(): Response<DefaultUrlConfigResponse>
}
