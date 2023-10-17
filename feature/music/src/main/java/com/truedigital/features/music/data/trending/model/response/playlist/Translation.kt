package com.truedigital.features.music.data.trending.model.response.playlist

import com.google.gson.annotations.SerializedName

data class Translation(

    @SerializedName(value = "Language", alternate = ["language"])
    val language: String? = null,

    @SerializedName(value = "Value", alternate = ["value"])
    val value: String? = null
)
