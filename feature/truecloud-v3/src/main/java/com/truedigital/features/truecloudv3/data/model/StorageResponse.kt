package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class StorageResponse(
    @SerializedName("data")
    var data: DataStorage? = null,
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

data class DataStorage(
    @SerializedName("quota_size")
    var quota: Long = 0,
    @SerializedName("root_folder")
    var rootFolder: TrueCloudV3StorageData? = null,
    @SerializedName("data_usage")
    var dataUsage: DataUsage? = null,
    @SerializedName("data_migration")
    var migration: DataMigration? = null
)

data class DataUsage(
    @SerializedName("images")
    val images: Long = 0,
    @SerializedName("videos")
    val videos: Long = 0,
    @SerializedName("audio")
    val audio: Long = 0,
    @SerializedName("others")
    val others: Long = 0,
    @SerializedName("contacts")
    val contacts: Long = 0,
    @SerializedName("total")
    val total: Long = 0,
    @SerializedName("status")
    var status: String,
    @SerializedName("usage_percent")
    var usagePercent: Float = 0f,
    var sortedObj: MutableList<Pair<String, Long?>>? = null
)

data class DataMigration(
    @SerializedName("status")
    var status: String = "",
    @SerializedName("estimated_time_to_complete")
    var estimatedTimeToComplete: String? = null,
    @SerializedName("failed_display_time")
    var failedDisplayTime: Int? = null,
    @SerializedName("expires_at")
    var expiresAt: String? = null,
    @SerializedName("reminder")
    var reminder: Boolean? = false
)
