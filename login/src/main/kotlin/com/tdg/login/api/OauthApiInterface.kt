package com.tdg.login.api

import com.tdg.login.data.model.OauthRequest
import com.tdg.login.data.model.OauthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OauthApiInterface {
    @POST("auth/v4/oauth2/token?jwt_version=2")
    suspend fun login(
        @Body request: OauthRequest
    ): Response<OauthResponse>
}
