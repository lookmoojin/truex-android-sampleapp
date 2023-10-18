package com.truedigital.common.share.datalegacy.data.api.graph

import androidx.annotation.WorkerThread
import com.truedigital.common.share.datalegacy.data.followteam.model.FollowTeamRequest
import com.truedigital.common.share.datalegacy.data.repository.profile.model.request.UpdateSettingProfileRequest
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileResponse
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileSettingsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
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

    @GET("profile/v2/accounts/{ssoId}/users/me/activities")
    suspend fun getFollowingTeam(
        @Path("ssoId") ssoId: String,
        @Query("appid") appId: String,
        @Query("action_type") actionType: String,
        @Query("content_type") contentType: String,
        @Query("title") title: String
    ): Response<ProfileResponse>

    @POST("profile/v2/accounts/{ssoId}/users/me/activities")
    suspend fun followTeam(
        @Path("ssoId") ssoId: String,
        @Body followTeamRequest: FollowTeamRequest
    ): Response<ProfileResponse>

    @DELETE("profile/v2/accounts/{ssoId}/users/me/activities")
    suspend fun unfollowTeam(
        @Path("ssoId") ssoId: String,
        @Query("appid") appId: String,
        @Query("action_type") actionType: String,
        @Query("content_type") contentType: String,
        @Query("refid") refId: String
    ): Response<ProfileResponse>
}
