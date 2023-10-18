package com.truedigital.features.tuned.common.extension

import com.truedigital.features.tuned.common.extensions.getDeviceId
import com.truedigital.features.tuned.data.user.model.AssociatedDevice
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class DeviceExtensionTest {

    @Test
    fun testGetDeviceId_matchFirstUniqueId_returnDeviceId() {
        val mockDeviceId = 1
        val mockUniqueId = "uniqueId"
        val mockList = listOf(
            AssociatedDevice(
                deviceId = mockDeviceId,
                uniqueId = mockUniqueId,
                displayName = "displayName",
                lastSeen = "lastSeen"
            ),
            AssociatedDevice(
                deviceId = 2,
                uniqueId = "id",
                displayName = "displayName",
                lastSeen = "lastSeen"
            )
        )
        val result = mockList.getDeviceId(mockUniqueId)
        assertEquals(mockDeviceId, result)
    }

    @Test
    fun testGetDeviceId_notMatchUniqueId_returnNull() {
        val mockDeviceId = 1
        val mockUniqueId = "uniqueId"
        val mockList = listOf(
            AssociatedDevice(
                deviceId = mockDeviceId,
                uniqueId = mockUniqueId,
                displayName = "displayName",
                lastSeen = "lastSeen"
            )
        )
        val result = mockList.getDeviceId("id")
        assertNull(result)
    }
}
