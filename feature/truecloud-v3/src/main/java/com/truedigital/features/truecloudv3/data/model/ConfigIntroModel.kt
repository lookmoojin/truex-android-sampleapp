package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class ConfigIntroModel(
    @SerializedName("login")
    val login: IntroLoginModel
)

data class IntroLoginModel(
    @SerializedName("login_image")
    val introImage: IntroLanguageModel,
    @SerializedName("login_image_tablet")
    val introImageTablet: IntroLanguageModel
)

data class IntroLanguageModel(
    @SerializedName("en")
    val en: String,
    @SerializedName("my")
    val my: String,
    @SerializedName("th")
    val th: String,
)
