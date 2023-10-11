package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class MigrateResponse(
    @SerializedName("data")
    var data: DataResponse? = null,
    @SerializedName("error")
    val error: ErrorResponse? = null,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("platform_module")
    val platformModule: String? = null,
    @SerializedName("report_dashboard")
    val reportDashboard: String? = null
)

data class DataResponse(
    @SerializedName("migration")
    var migration: MigrationResponse? = null
)

data class MigrationResponse(
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("estimated_time_to_complete")
    var estimatedTimeToComplete: String? = null
)
