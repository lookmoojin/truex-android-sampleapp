package com.tdg.onboarding.data.model

import com.google.gson.annotations.SerializedName

data class WhatNewResponse(
    @SerializedName("android")
    val android: AndroidModel? = null
)

data class AndroidModel(
    @SerializedName("enable")
    val enable: Boolean? = false,
    @SerializedName("image_url")
    val imageUrl: ImageUrlModel? = null,
    @SerializedName("timestamp")
    val timestamp: String? = "",
    @SerializedName("type")
    val type: String? = "",
    @SerializedName("url")
    val url: String? = ""
)

data class ImageUrlModel(
    @SerializedName("mobile")
    val mobile: String? = "",
    @SerializedName("tablet")
    val tablet: String? = ""
)
