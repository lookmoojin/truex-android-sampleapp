package com.truedigital.common.share.datalegacy.domain.entitlement.usecase

import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.add.DeviceEntitlementDetails
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.get.GetDeviceEntitlementData
import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.response.get.GetDeviceEntitlementResponse
import com.truedigital.common.share.datalegacy.data.repository.entitlement.repository.DeviceEntitlementRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.data.repository.DeviceRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.reactivex.Single
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Calendar

internal class GetActiveDeviceEntitlementUseCaseTest {

    private lateinit var useCase: GetActiveDeviceEntitlementUseCaseImpl

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var deviceRepository: DeviceRepository

    @MockK
    private lateinit var deviceEntitlementRepository: DeviceEntitlementRepository

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        useCase = GetActiveDeviceEntitlementUseCaseImpl(
            userRepository,
            deviceRepository,
            deviceEntitlementRepository
        )
    }

    @Test
    fun `execute should return DeviceEntitlement when ssoId is not empty`() {
        // given
        val ssoId = "123"
        val currentTime = Calendar.getInstance().timeInMillis
        val deviceEntitlementResponse = GetDeviceEntitlementResponse().apply {
            data = GetDeviceEntitlementData().apply {
                maxEntitlement = 5
                active = listOf(
                    DeviceEntitlementDetails().apply {
                        os = "android"
                        deviceId = "1234"
                        nickName = "My Device"
                        isActive = true
                        registeredAt = Calendar.getInstance().timeInMillis
                    }
                )
            }
        }
        every { userRepository.getSsoId() } returns ssoId
        every { deviceRepository.getAndroidId() } returns "1234"
        every { deviceEntitlementRepository.getDeviceEntitlementList(ssoId) } returns Single.just(
            deviceEntitlementResponse
        )

        every {
            deviceEntitlementRepository.saveActiveDeviceCache(any())
        } just runs

        // when
        val result = useCase.execute(currentTime).test()

        // then
        result.assertNoErrors()
        result.assertValueCount(1)
        val deviceEntitlement = result.values()[0]
        assertEquals(deviceEntitlement.limitDevice, 5)
        assertEquals(deviceEntitlement.activeDeviceEntitlementList.size, 1)
        assertEquals(deviceEntitlement.activeDeviceEntitlementList[0].os, "android")
        assertEquals(deviceEntitlement.activeDeviceEntitlementList[0].deviceId, "1234")
        assertEquals(deviceEntitlement.activeDeviceEntitlementList[0].deviceName, "My Device")
        assertTrue(deviceEntitlement.activeDeviceEntitlementList[0].isOwnDevice)
        assertEquals(
            deviceEntitlement.activeDeviceEntitlementList[0].numberDateAfterRegisterDate,
            ((currentTime - deviceEntitlement.activeDeviceEntitlementList[0].registerDate) / 1000 / 60 / 60 / 24)
        )
        assertTrue(deviceEntitlement.activeDeviceEntitlementList[0].isShowRemoveDevice)
    }

    @Test
    fun `execute should return error when ssoId is empty`() {
        // given
        every { userRepository.getSsoId() } returns ""

        // when
        val result = useCase.execute(Calendar.getInstance().timeInMillis).test()

        // then
        result.assertError { it.message == "SsoId is required" }
    }
}
