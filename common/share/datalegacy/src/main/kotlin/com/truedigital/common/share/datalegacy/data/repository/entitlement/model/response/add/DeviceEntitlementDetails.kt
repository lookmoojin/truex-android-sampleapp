package com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.add

import com.google.gson.annotations.SerializedName

class DeviceEntitlementDetails {

    @SerializedName("ssoid")
    var ssoId: String? = null

    @SerializedName("is_active")
    var isActive: Boolean? = null

    @SerializedName("nick_name")
    var nickName: String? = null

    @SerializedName("os")
    var os: String? = null

    @SerializedName("device_id")
    var deviceId: String? = null

    @SerializedName("device_model")
    var deviceModel: String? = null

    @SerializedName("registered_at")
    var registeredAt: Long = 0

    @SerializedName("latest_accessed_at")
    var latestAccessedAt: Long = 0

    @SerializedName("updated_at")
    var updatedAt: Long = 0

    @SerializedName("degistered_at")
    var degisteredAt: Long = 0

    @SerializedName("max_entitlement")
    var maxEntitlement: Int? = null
}
