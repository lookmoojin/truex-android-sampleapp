package com.truedigital.common.share.datalegacy.domain.entitlement.usecase

import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.entity.ActiveDeviceEntitlement
import com.truedigital.core.data.repository.DeviceRepository
import io.reactivex.Single
import javax.inject.Inject

interface ContainDeviceIdInDeviceListUseCase {
    fun execute(activeDeviceEntitlementList: List<ActiveDeviceEntitlement>): Single<Boolean>
}

class ContainDeviceIdInDeviceListUseCaseImpl @Inject constructor(
    private val deviceRepository: DeviceRepository
) :
    ContainDeviceIdInDeviceListUseCase {

    override fun execute(activeDeviceEntitlementList: List<ActiveDeviceEntitlement>): Single<Boolean> {
        val isContainDeviceIdInDeviceEntitlementList = !activeDeviceEntitlementList.map {
            it.deviceId
        }.find {
            val ownDeviceId = deviceRepository.getAndroidId()
            it == ownDeviceId
        }.isNullOrEmpty()

        return Single.just(isContainDeviceIdInDeviceEntitlementList)
    }
}
