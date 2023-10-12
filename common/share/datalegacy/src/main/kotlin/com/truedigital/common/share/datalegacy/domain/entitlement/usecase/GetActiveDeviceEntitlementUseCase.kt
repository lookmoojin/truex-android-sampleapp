package com.truedigital.common.share.datalegacy.domain.entitlement.usecase

import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.entity.ActiveDeviceEntitlement
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.entity.DeviceEntitlement
import com.truedigital.common.share.datalegacy.data.repository.entitlement.repository.DeviceEntitlementRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.data.repository.DeviceRepository
import io.reactivex.Single
import javax.inject.Inject

interface GetActiveDeviceEntitlementUseCase {
    fun execute(currentTime: Long): Single<DeviceEntitlement>
}

class GetActiveDeviceEntitlementUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val deviceRepository: DeviceRepository,
    private val deviceEntitlementRepository: DeviceEntitlementRepository
) : GetActiveDeviceEntitlementUseCase {

    companion object {
        private const val DEFAULT_LIMIT_MAX_DEVICE = 5
        private const val DEFAULT_DEVICE_NAME = "Unknown"
        private const val HOURS_IN_DAY = 24
        private const val MINUTES_IN_HOUR = 60
        private const val SECONDS_IN_MINUTE = 60
        private const val MILLISECONDS_IN_SECOND = 1000
        private const val ERROR_SSOID_BLANK = "SsoId is required"
    }

    override fun execute(currentTime: Long): Single<DeviceEntitlement> {
        val ssoId = userRepository.getSsoId()

        return if (ssoId.isNotEmpty()) {
            val deviceEntitlement = DeviceEntitlement()
            deviceEntitlementRepository.getDeviceEntitlementList(ssoId)
                .map { deviceEntitlementResponse ->
                    deviceEntitlement.limitDevice = deviceEntitlementResponse.data?.maxEntitlement ?: DEFAULT_LIMIT_MAX_DEVICE
                    deviceEntitlementResponse.data?.active ?: listOf()
                }
                .toObservable()
                .flatMapIterable { it }
                .filter { it.isActive ?: false }
                .map { deviceEntitlementDetails ->
                    val deviceName = if (deviceEntitlementDetails.nickName.isNullOrBlank()) {
                        if (deviceEntitlementDetails.os.isNullOrBlank()) {
                            DEFAULT_DEVICE_NAME
                        } else {
                            deviceEntitlementDetails.os
                        }
                    } else {
                        deviceEntitlementDetails.nickName
                    } ?: ""

                    ActiveDeviceEntitlement().apply {
                        os = deviceEntitlementDetails.os ?: ""
                        deviceId = deviceEntitlementDetails.deviceId ?: ""
                        this.deviceName = deviceName
                        isOwnDevice = deviceRepository.getAndroidId().equals(deviceEntitlementDetails.deviceId, ignoreCase = true)
                        registerDate = deviceEntitlementDetails.registeredAt
                        numberDateAfterRegisterDate = (currentTime - registerDate) / MILLISECONDS_IN_SECOND / SECONDS_IN_MINUTE / MINUTES_IN_HOUR / HOURS_IN_DAY
                        isShowRemoveDevice = deviceEntitlementDetails.deviceId?.trim()?.isNotBlank() == true
                    }
                }
                .toList()
                .doOnSuccess { activeDeviceEntitlementList ->
                    deviceEntitlement.activeDeviceEntitlementList = activeDeviceEntitlementList
                    deviceEntitlementRepository.saveActiveDeviceCache((deviceEntitlement))
                }
                .map {
                    deviceEntitlement
                }
        } else {
            Single.error(Throwable(ERROR_SSOID_BLANK))
        }
    }
}
