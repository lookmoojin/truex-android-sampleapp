package com.truedigital.common.share.datalegacy.data.repository.profile.model.accounts
import com.google.gson.annotations.SerializedName

data class AccountGetAllProfilesDataResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val data: List<AccountGetAllProfilesBodyDataResponse>,
    @SerializedName("message")
    val message: String,
    @SerializedName("platform_module")
    val platformModule: Int,
    @SerializedName("report_dashboard")
    val reportDashboard: Int,
    @SerializedName("error")
    val error: String
)
