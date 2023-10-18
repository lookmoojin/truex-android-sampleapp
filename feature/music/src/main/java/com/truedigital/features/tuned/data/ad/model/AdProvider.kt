package com.truedigital.features.tuned.data.ad.model

import com.google.gson.annotations.SerializedName

enum class AdProvider {
    @SerializedName("None", alternate = ["NONE"])
    NONE,

    @SerializedName("Triton", alternate = ["TRITON"])
    TRITON,

    @SerializedName("Tuned", alternate = ["TUNED"])
    TUNED
}
