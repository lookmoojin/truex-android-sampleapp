package com.truedigital.common.share.datalegacy.data.repository.profile.model.accounts
import com.google.gson.annotations.SerializedName

data class AccountGetAllProfilesBodyDataResponse(
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("cover")
    val cover: AccountGetAllProfilesCoverDataResponse,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("family_id")
    val familyId: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("nextusername_updatedt")
    val nextusernameUpdatedt: Long,
    @SerializedName("settings")
    val settings: AccountGetAllProfilesSettingsDataResponse,
    @SerializedName("ssoid")
    val ssoid: String,
    @SerializedName("username")
    val username: String
)
