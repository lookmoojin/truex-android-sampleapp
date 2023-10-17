package com.truedigital.features.tuned.data.user.model.request

import com.google.gson.annotations.SerializedName

data class UsernamePasswordType(
    @SerializedName("Username") var username: String,
    @SerializedName("Password") var password: String,
    @SerializedName("LoginType") var loginType: String
)
