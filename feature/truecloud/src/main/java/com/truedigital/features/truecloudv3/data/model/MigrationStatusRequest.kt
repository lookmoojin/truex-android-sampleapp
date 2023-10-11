package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class MigrationStatusRequest(
    @SerializedName("status")
    var status: String? = null
)
