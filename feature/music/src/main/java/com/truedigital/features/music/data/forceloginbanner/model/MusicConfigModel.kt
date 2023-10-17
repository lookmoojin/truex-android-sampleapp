package com.truedigital.features.music.data.forceloginbanner.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicConfigModel(
    @SerializedName("login_banner")
    var loginBannerItemConfigModel: LoginBannerItemConfigModel = LoginBannerItemConfigModel(),

    @SerializedName("root_shelf")
    var rootShelf: MusicConfigValue = MusicConfigValue(),

    @SerializedName("ads_banner_player")
    var adsBannerPlayer: AdsBannerPlayerModel = AdsBannerPlayerModel()
) : Parcelable

@Parcelize
data class LoginBannerItemConfigModel(
    @SerializedName("image_th")
    var imageTH: String = "",
    @SerializedName("image_en")
    var imageEN: String = ""
) : Parcelable

@Parcelize
data class MusicConfigValue(
    @SerializedName("android")
    var android: String = ""
) : Parcelable

@Parcelize
data class AdsBannerPlayerModel(
    @SerializedName("enable")
    var enable: MusicEnableModel = MusicEnableModel(),
    @SerializedName("url_ads")
    var urlAds: String = ""
) : Parcelable

@Parcelize
data class MusicEnableModel(
    @SerializedName("android")
    var android: Boolean = false
) : Parcelable
