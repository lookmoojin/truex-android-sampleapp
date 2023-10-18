package com.truedigital.features.music.data.search.model.response

import com.google.gson.annotations.SerializedName

data class MusicSearchResponseItem(
    @SerializedName("key")
    val key: String? = null,
    @SerializedName("results")
    val results: Results? = null
)
