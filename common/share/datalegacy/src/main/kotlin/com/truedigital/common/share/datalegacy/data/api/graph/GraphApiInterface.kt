package com.truedigital.common.share.datalegacy.data.api.graph

import androidx.annotation.WorkerThread
import com.truedigital.common.share.datalegacy.data.repository.profile.model.request.UpdateSettingProfileRequest
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileResponse
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileSettingsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface GraphApiInterface {
    @WorkerThread
    @GET("profile/v2/accounts/{ssoid}/users/me")
    suspend fun getProfile(
        @Path("ssoid") ssoId: String,
        @Query("fields") fields: String
    ): Response<ProfileResponse>

    @GET("profile/v2/accounts")
    suspend fun getProfileByUsername(
        @Query("username") username: String,
        @Query("fields") fields: String?,
        @Query("country_cd") countryCode: String?
    ): Response<ProfileResponse>

    @GET("profile/v2/accounts/{ssoid}")
    suspend fun getOtherProfile(
        @Path("ssoid") ssoId: String,
        @Query("fields") fields: String?,
        @Query("country_cd") countryCode: String?
    ): Response<ProfileResponse>

    @WorkerThread
    @PATCH("profile/v2/accounts/{ssoid}/users/me")
    suspend fun updateProfileWatch(
        @Path("ssoid") ssoId: String,
        @Body updateProfileSettingsRequest: UpdateSettingProfileRequest
    ): Response<ProfileSettingsResponse>
}
