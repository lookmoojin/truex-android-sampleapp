package com.tdg.login.data.model

import com.google.gson.annotations.SerializedName

data class PayloadResponse(
    @SerializedName("sub")
    val sub: String? = null,
    @SerializedName("device_id")
    val deviceId: String? = null
)
