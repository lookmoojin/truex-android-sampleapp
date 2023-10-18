package com.truedigital.common.share.datalegacy.domain.entitlement.usecase

import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.entity.ActiveDeviceEntitlement
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.entity.DeviceEntitlement
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.add.DeviceEntitlementDetails
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.delete.RemoveDeviceEntitlementResponse
import com.truedigital.common.share.datalegacy.data.repository.entitlement.repository.DeviceEntitlementRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

internal class RemoveActiveDeviceEntitlementUseCaseTest {

    private val userRepository: UserRepository = mockk()
    private val deviceEntitlementRepository: DeviceEntitlementRepository = mockk()
    private lateinit var useCase: RemoveActiveDeviceEntitlementUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase =
            RemoveActiveDeviceEntitlementUseCaseImpl(userRepository, deviceEntitlementRepository)
    }

    @Test
    fun `execute with valid device id should remove device entitlement and return updated device entitlement`() {
        // given
        val deviceId = "abc123"
        val ssoId = "user1"
        every { userRepository.getSsoId() } returns ssoId
        every { deviceEntitlementRepository.loadActiveDeviceCache() } returns Single.just(
            DeviceEntitlement()
        )
        every {
            deviceEntitlementRepository.removeDeviceEntitlementList(
                any(),
                any()
            )
        } returns Single.just(
            RemoveDeviceEntitlementResponse().apply {
                data = listOf(
                    DeviceEntitlementDetails().apply {
                        this.deviceId = deviceId
                    }
                )
            }
        )

        // when
        val testValues = useCase.execute(deviceId).blockingGet()
        val testObserver = useCase.execute(deviceId).test()

        // then
        val expectedDeviceEntitlement = DeviceEntitlement().apply {
            ActiveDeviceEntitlement().apply {
                listOf(
                    ActiveDeviceEntitlement().apply {
                        os = "Android"
                        deviceName = "My Device"
                        isOwnDevice = false
                        registerDate = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10)
                        numberDateAfterRegisterDate = 10
                        isShowRemoveDevice = true
                    }
                )
            }
        }
        testObserver.assertValue(testValues)
    }

    @Test
    fun `execute with blank device id should throw an error`() {
        // given
        val deviceId = ""

        // when
        val testObserver = useCase.execute(deviceId).test()

        // then
        testObserver.assertError { it.message == RemoveActiveDeviceEntitlementUseCaseImpl.Companion.ERROR_BLANK_DEVICE_ID }
        verify(exactly = 0) { userRepository.getSsoId() }
        verify(exactly = 0) { deviceEntitlementRepository.loadActiveDeviceCache() }
        verify(exactly = 0) {
            deviceEntitlementRepository.removeDeviceEntitlementList(
                any(),
                any()
            )
        }
        verify(exactly = 0) { deviceEntitlementRepository.saveActiveDeviceCache(any()) }
    }
}
