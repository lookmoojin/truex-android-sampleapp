package com.truedigital.features.tuned.data.authentication.model.response

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AccessTokenTest {

    @Test
    fun testGetDeviceId_matchFirstUniqueId_returnDeviceId() {
        val result = AccessToken(
            accessToken = "accessToken",
            tokenType = "tokenType",
            expiresIn = 1L,
            refreshToken = "refreshToken"
        )
        Assertions.assertEquals("accessToken", result.accessToken)
        Assertions.assertEquals("tokenType", result.tokenType)
        Assertions.assertEquals(1L, result.expiresIn)
        Assertions.assertEquals("refreshToken", result.refreshToken)
    }
}
