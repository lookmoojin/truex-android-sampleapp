package com.truedigital.features.tuned.common.extension

import com.truedigital.features.tuned.common.extensions.isDeviceUser
import com.truedigital.features.tuned.data.user.model.Login
import com.truedigital.features.tuned.data.user.model.UserAccountType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LoginExtensionTest {

    @Test
    fun testIsDeviceUser_matchType_returnTrue() {
        val mockList = listOf(
            Login(
                type = UserAccountType.DEVICE.type,
                value = ""
            )
        )
        val result = mockList.isDeviceUser
        Assertions.assertTrue(result)
    }

    @Test
    fun testIsDeviceUser_notMatchType_returnFalse() {
        val mockList = listOf(
            Login(
                type = UserAccountType.USERNAME.type,
                value = ""
            )
        )
        val result = mockList.isDeviceUser
        Assertions.assertFalse(result)
    }
}
