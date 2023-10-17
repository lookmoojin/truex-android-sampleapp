package com.truedigital.features.tuned.data.user.model

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("Type") var type: String,
    @SerializedName("Value") var value: String
)
