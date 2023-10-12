package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class SearchFileResponse(
    @SerializedName("code")
    val code: Int? = 0,
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("platform_module")
    val platformModule: Int? = 0,
    @SerializedName("report_dashboard")
    val reportDashBoard: Int? = 0,
    @SerializedName("data")
    var data: DataListStorageResponse? = null
)
