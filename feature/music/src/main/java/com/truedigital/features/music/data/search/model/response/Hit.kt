package com.truedigital.features.music.data.search.model.response

import com.google.gson.annotations.SerializedName

data class Hit(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("_source")
    val source: Source? = null
)
