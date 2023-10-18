package com.truedigital.common.share.datalegacy.data.repository.entitlement.model.entity

class DeviceEntitlement {

    var limitDevice = 0
    var activeDeviceEntitlementList: MutableList<ActiveDeviceEntitlement> = mutableListOf()
}
