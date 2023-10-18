package com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.get

import com.google.gson.annotations.SerializedName
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.add.DeviceEntitlementDetails

class GetDeviceEntitlementResponse {

    var code: Int? = null

    var data: GetDeviceEntitlementData? = null

    var message: String? = null
}

class GetDeviceEntitlementData {

    @SerializedName("max_entitlement")
    var maxEntitlement: Int? = null

    var active: List<DeviceEntitlementDetails?>? = null

    var inactive: List<DeviceEntitlementDetails?>? = null
}
