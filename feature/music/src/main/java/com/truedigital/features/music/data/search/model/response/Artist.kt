package com.truedigital.features.music.data.search.model.response

import com.google.gson.annotations.SerializedName

data class Artist(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null
)
