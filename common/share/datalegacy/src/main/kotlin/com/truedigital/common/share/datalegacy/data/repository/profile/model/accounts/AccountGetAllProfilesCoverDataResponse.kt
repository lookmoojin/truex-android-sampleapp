package com.truedigital.common.share.datalegacy.data.repository.profile.model.accounts
import com.google.gson.annotations.SerializedName

data class AccountGetAllProfilesCoverDataResponse(
    @SerializedName("large")
    val large: String,
    @SerializedName("medium")
    val medium: String,
    @SerializedName("small")
    val small: String
)
