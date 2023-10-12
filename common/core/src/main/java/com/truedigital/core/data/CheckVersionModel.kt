package com.truedigital.core.data

import com.google.gson.annotations.SerializedName

data class CheckVersionModel(
    @SerializedName("min_version")
    var minVersion: String? = null,
    @SerializedName("max_version")
    var maxVersion: String? = null
)
