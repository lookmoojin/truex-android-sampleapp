package com.truedigital.features.music.data.search.model.response

import com.google.gson.annotations.SerializedName

data class Hits(
    @SerializedName("hits")
    val hits: List<Hit>? = null
)
