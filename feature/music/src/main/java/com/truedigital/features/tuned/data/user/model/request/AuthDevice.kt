package com.truedigital.features.tuned.data.user.model.request

import com.google.gson.annotations.SerializedName

data class AuthDevice(
    @SerializedName("DeviceType") var deviceType: String,
    @SerializedName("DisplayName") var displayName: String,
    @SerializedName("UniqueId") var uniqueId: String,
    @SerializedName("DeviceOS") var deviceOS: String,
    @SerializedName("LastOSVersion") var osVersion: String,
    @SerializedName("LastAppVersion") var appVersion: String,
    @SerializedName("DeviceManufacturer") var deviceManufacturer: String,
    @SerializedName("ApplicationId") var applicationId: Int,
    @SerializedName("Carrier") var carrier: String
)
