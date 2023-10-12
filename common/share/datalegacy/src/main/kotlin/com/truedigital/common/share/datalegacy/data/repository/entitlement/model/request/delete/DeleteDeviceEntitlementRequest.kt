package com.truedigital.common.share.datalegacy.data.repository.entitlement.model.request.delete

import com.google.gson.annotations.SerializedName

class DeleteDeviceEntitlementRequest {

    @SerializedName("data")
    var deviceDataList: MutableList<DeviceDataItem> = mutableListOf()
}
