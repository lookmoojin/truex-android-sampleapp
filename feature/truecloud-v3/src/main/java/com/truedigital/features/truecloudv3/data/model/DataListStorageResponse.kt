package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class DataListStorageResponse(
    @SerializedName("uploaded")
    var uploaded: MutableList<TrueCloudV3StorageData>? = null,
    @SerializedName("storage")
    var storage: DataUsage? = null,
    @SerializedName("info")
    var info: DataListInfo? = null
)
