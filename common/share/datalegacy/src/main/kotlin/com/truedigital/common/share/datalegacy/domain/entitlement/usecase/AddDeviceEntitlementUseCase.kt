package com.truedigital.common.share.datalegacy.domain.entitlement.usecase

import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.request.add.AddDeviceEntitlementRequest
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.add.DeviceEntitlementDetails
import com.truedigital.common.share.datalegacy.data.repository.entitlement.repository.DeviceEntitlementRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.data.repository.DeviceRepository
import io.reactivex.Single
import javax.inject.Inject

interface AddDeviceEntitlementUseCase {
    fun execute(): Single<DeviceEntitlementDetails>
}

class AddDeviceEntitlementUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val deviceRepository: DeviceRepository,
    private val deviceEntitlementRepository: DeviceEntitlementRepository
) :
    AddDeviceEntitlementUseCase {

    companion object {
        private const val operatingSystem = "android"
    }

    override fun execute(): Single<DeviceEntitlementDetails> {
        val addDeviceEntitlementRequest = AddDeviceEntitlementRequest().apply {
            ssoId = userRepository.getSsoId()
            deviceId = deviceRepository.getAndroidId()
            os = operatingSystem
            nickName = deviceRepository.getDeviceName()
        }

        return deviceEntitlementRepository.addDeviceEntitlement(addDeviceEntitlementRequest)
            .map { response ->
                response.body()?.data
            }
    }
}
