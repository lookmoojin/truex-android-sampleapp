package com.truedigital.common.share.currentdate

import com.google.gson.annotations.SerializedName

data class ServerDateTimeModel(
    @SerializedName("date")
    val date: Long = 0
)
