package com.truedigital.features.music.data.search.model.response

import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("album_image")
    val albumImage: String? = null,
    @SerializedName("image")
    val image: String? = null
)
