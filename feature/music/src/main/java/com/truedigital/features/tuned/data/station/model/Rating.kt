package com.truedigital.features.tuned.data.station.model

import com.google.gson.annotations.SerializedName

enum class Rating(val type: String) {
    @SerializedName("Liked")
    LIKED("Liked"),

    @SerializedName("Disliked")
    DISLIKED("Disliked")
}
