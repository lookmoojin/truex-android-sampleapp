package com.truedigital.common.share.datalegacy.domain.entitlement.usecase

import com.truedigital.common.share.datalegacy.data.repository.entitlement.model.entity.ActiveDeviceEntitlement
import com.truedigital.core.data.repository.DeviceRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ContainDeviceIdInDeviceListUseCaseTest {

    private lateinit var deviceRepository: DeviceRepository
    private lateinit var containDeviceIdInDeviceListUseCase: ContainDeviceIdInDeviceListUseCase

    @BeforeEach
    fun setUp() {
        deviceRepository = mockk(relaxed = true)
        containDeviceIdInDeviceListUseCase =
            ContainDeviceIdInDeviceListUseCaseImpl(deviceRepository)
    }

    @Test
    fun `test execute with active device entitlement list contains own device id`() {
        // given
        val ownDeviceId = "123"
        val activeDeviceEntitlementList = listOf(
            ActiveDeviceEntitlement().apply {
                os = "1"
                deviceId = "device1"
                deviceName = "1"
            },
            ActiveDeviceEntitlement().apply {
                os = "2"
                deviceId = "device2"
                deviceName = "2"
            },
            ActiveDeviceEntitlement().apply {
                deviceId = ownDeviceId
                deviceName = "device3"
                os = "3"
            }
        )
        every { deviceRepository.getAndroidId() } returns ownDeviceId

        // when
        val result =
            containDeviceIdInDeviceListUseCase.execute(activeDeviceEntitlementList).blockingGet()

        // then
        assertEquals(true, result)
    }

    @Test
    fun `test execute with active device entitlement list does not contain own device id`() {
        // given
        val ownDeviceId = "123"
        val activeDeviceEntitlementList = listOf(
            ActiveDeviceEntitlement().apply {
                os = "1"
                deviceId = "device1"
                deviceName = "1"
            },
            ActiveDeviceEntitlement().apply {
                os = "2"
                deviceId = "device2"
                deviceName = "2"
            },
            ActiveDeviceEntitlement().apply {
                os = "3"
                deviceId = "device3"
                deviceName = "3"
            }
        )
        every { deviceRepository.getAndroidId() } returns ownDeviceId

        // when
        val result =
            containDeviceIdInDeviceListUseCase.execute(activeDeviceEntitlementList).blockingGet()

        // then
        assertEquals(false, result)
    }
}
