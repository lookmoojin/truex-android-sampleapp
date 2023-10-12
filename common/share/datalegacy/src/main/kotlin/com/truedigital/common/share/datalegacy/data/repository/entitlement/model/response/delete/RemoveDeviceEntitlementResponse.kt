package com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.delete

import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.add.DeviceEntitlementDetails

class RemoveDeviceEntitlementResponse {

    var code: Int? = null

    var data: List<DeviceEntitlementDetails?>? = null

    var message: String? = null
}
