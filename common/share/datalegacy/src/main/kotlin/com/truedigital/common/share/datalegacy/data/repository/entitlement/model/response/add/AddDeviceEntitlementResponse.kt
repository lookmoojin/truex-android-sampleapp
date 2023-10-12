package com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.add

import com.google.gson.annotations.SerializedName

class AddDeviceEntitlementResponse {

    @SerializedName("code")
    var code: Int? = null

    @SerializedName("data")
    var data: DeviceEntitlementDetails? = null

    @SerializedName("message")
    var message: String? = null
}
