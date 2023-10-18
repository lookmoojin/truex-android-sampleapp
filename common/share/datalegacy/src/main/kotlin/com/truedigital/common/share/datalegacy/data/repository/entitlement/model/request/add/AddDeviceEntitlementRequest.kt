package com.truedigital.common.share.datalegacy.data.repository.entitlement.model.request.add

import com.google.gson.annotations.SerializedName

class AddDeviceEntitlementRequest {

    @SerializedName("ssoid")
    var ssoId: String? = null

    @SerializedName("device_id")
    var deviceId: String? = null

    @SerializedName("os")
    var os: String? = null

    @SerializedName("nick_name")
    var nickName: String? = null
}
