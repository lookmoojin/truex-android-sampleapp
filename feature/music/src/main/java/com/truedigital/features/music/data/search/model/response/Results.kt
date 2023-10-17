package com.truedigital.features.music.data.search.model.response

import com.google.gson.annotations.SerializedName

data class Results(
    @SerializedName("hits")
    val hits: Hits? = null
)
