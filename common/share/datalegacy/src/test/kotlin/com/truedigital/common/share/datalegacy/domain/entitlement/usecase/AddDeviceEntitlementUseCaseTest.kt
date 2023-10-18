package com.truedigital.common.share.datalegacy.domain.entitlement.usecase

import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.request.add.AddDeviceEntitlementRequest
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.add.AddDeviceEntitlementResponse
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.add.DeviceEntitlementDetails
import com.truedigital.common.share.datalegacy.data.repository.entitlement.repository.DeviceEntitlementRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.data.repository.DeviceRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import org.junit.jupiter.api.Test
import retrofit2.Response

internal class AddDeviceEntitlementUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private val deviceRepository = mockk<DeviceRepository>()
    private val deviceEntitlementRepository = mockk<DeviceEntitlementRepository>()

    private val addDeviceEntitlementUseCaseImpl =
        AddDeviceEntitlementUseCaseImpl(
            userRepository,
            deviceRepository,
            deviceEntitlementRepository
        )

    @Test
    fun `execute should return device entitlement details`() {
        // Given
        val ssoId = "user123"
        val androidId = "android123"
        val deviceName = "My Android Phone"
        val addDeviceEntitlementRequest = AddDeviceEntitlementRequest().apply {
            this.ssoId = ssoId
            deviceId = androidId
            os = "android"
            nickName = deviceName
        }
        val deviceEntitlementDetails = DeviceEntitlementDetails()

        every { userRepository.getSsoId() } returns ssoId
        every { deviceRepository.getAndroidId() } returns androidId
        every { deviceRepository.getDeviceName() } returns deviceName
        every { deviceEntitlementRepository.addDeviceEntitlement(any()) } returns Single.just(
            Response.success(
                AddDeviceEntitlementResponse().apply {
                    data = deviceEntitlementDetails
                }
            )
        )

        // When
        val testObserver = addDeviceEntitlementUseCaseImpl.execute().test()

        // Then
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValue(deviceEntitlementDetails)
    }
}
