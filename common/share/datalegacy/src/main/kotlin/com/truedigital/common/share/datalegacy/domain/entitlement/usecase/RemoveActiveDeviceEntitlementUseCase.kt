package com.truedigital.common.share.datalegacy.domain.entitlement.usecase

import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.entity.DeviceEntitlement
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.request.delete.DeleteDeviceEntitlementRequest
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.request.delete.DeviceDataItem
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.delete.RemoveDeviceEntitlementResponse
import com.truedigital.common.share.datalegacy.data.repository.entitlement.repository.DeviceEntitlementRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

interface RemoveActiveDeviceEntitlementUseCase {
    fun execute(deviceId: String): Single<DeviceEntitlement>
}

class RemoveActiveDeviceEntitlementUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val deviceEntitlementRepository: DeviceEntitlementRepository
) :
    RemoveActiveDeviceEntitlementUseCase {

    companion object {
        const val ERROR_BLANK_DEVICE_ID = "Device id must not be null"
    }

    override fun execute(deviceIdRemoved: String): Single<DeviceEntitlement> {
        return if (deviceIdRemoved.trim().isNotBlank()) {
            val deleteDeviceEntitlementRequest = DeleteDeviceEntitlementRequest().apply {
                deviceDataList.add(
                    DeviceDataItem().apply {
                        deviceId = deviceIdRemoved
                    }
                )
            }
            Single.zip(
                deviceEntitlementRepository.loadActiveDeviceCache(),
                deviceEntitlementRepository.removeDeviceEntitlementList(userRepository.getSsoId(), deleteDeviceEntitlementRequest),
                BiFunction { deviceEntitlementCache: DeviceEntitlement,
                    deviceEntitlementResponse: RemoveDeviceEntitlementResponse ->

                    val deviceDeletedList = deviceEntitlementResponse.data ?: listOf()
                    for (deviceDeletedItem in deviceDeletedList) {
                        for (activeDeviceItem in deviceEntitlementCache.activeDeviceEntitlementList) {
                            if (deviceDeletedItem?.deviceId == activeDeviceItem.deviceId) {
                                deviceEntitlementCache.activeDeviceEntitlementList.remove(activeDeviceItem)
                                break
                            }
                        }
                    }

                    deviceEntitlementCache
                }
            )
        } else {
            Single.error(Throwable(ERROR_BLANK_DEVICE_ID))
        }
    }
}
