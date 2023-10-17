package com.truedigital.features.tuned.data.user.model

import com.google.gson.annotations.SerializedName

/**
 * Device associated to a particular user, used for playback (I think)
 */

data class AssociatedDevice(
    @SerializedName("DeviceId") var deviceId: Int,
    @SerializedName("UniqueId") var uniqueId: String,
    @SerializedName("DisplayName") var displayName: String,
    @SerializedName("LastSeen") var lastSeen: String
)
