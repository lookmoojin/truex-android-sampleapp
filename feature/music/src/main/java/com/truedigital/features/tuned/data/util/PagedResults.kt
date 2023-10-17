package com.truedigital.features.tuned.data.util

import com.google.gson.annotations.SerializedName

data class PagedResults<T>(
    @SerializedName("Offset") var offset: Int = 0,
    @SerializedName("Count") var count: Int = 0,
    @SerializedName("Total") var total: Int = 0,
    @SerializedName("Results") var results: List<T> = listOf()
)
