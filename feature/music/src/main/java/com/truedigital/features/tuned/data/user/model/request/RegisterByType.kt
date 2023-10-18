package com.truedigital.features.tuned.data.user.model.request

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.user.model.LoginType

data class RegisterByType(
    @SerializedName("DeviceType") var deviceType: String,
    @SerializedName("DisplayName") var displayName: String,
    @SerializedName("UniqueId") var uniqueId: String,
    @SerializedName("DeviceOS") var deviceOS: String,
    @SerializedName("LastOSVersion") var osVersion: String,
    @SerializedName("LastAppVersion") var appVersion: String,
    @SerializedName("Password") var password: String,
    @SerializedName("Country") var country: String,
    @SerializedName("Language") var language: String,
    @SerializedName("DeviceManufacturer") var deviceManufacturer: String,
    @SerializedName("ApplicationId") var applicationId: Int,
    @SerializedName("TimezoneOffset") var timezoneOffset: Int,
    @SerializedName("Carrier") var carrier: String,
    @SerializedName("Description") var description: String,
    @SerializedName("LoginType") var loginType: String = LoginType.DEVICE.value
)
