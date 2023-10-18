package com.truedigital.common.share.data.coredata.deeplink.usecase

import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants.DeeplinkConstants.HOST_DYNAMIC_LINK
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants.DeeplinkConstants.HOST_ONELINK
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants.DeeplinkConstants.HOST_ONELINK_TTID
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants.DeeplinkConstants.HOST_TRUE_ID
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants.DeeplinkConstants.KEY_ONELINK
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class IsInternalDeeplinkUseCaseTest {

    private lateinit var isInternalDeeplinkUseCase: IsInternalDeeplinkUseCase

    @BeforeEach
    fun setUp() {
        isInternalDeeplinkUseCase = IsInternalDeeplinkUseCaseImpl()
    }

    @Test
    fun testIsInternalDeeplink_urlIsEmpty_returnFalse() {
        val result = isInternalDeeplinkUseCase.execute("")
        assertFalse(result)
    }

    @Test
    fun testIsInternalDeeplink_urlIsHostDynamicLink_returnTrue() {
        val result = isInternalDeeplinkUseCase.execute("link $HOST_DYNAMIC_LINK link")
        assertTrue(result)
    }

    @Test
    fun testIsInternalDeeplink_urlIsHostOneLink_returnTrue() {
        val result = isInternalDeeplinkUseCase.execute("link $HOST_ONELINK link")
        assertTrue(result)
    }

    @Test
    fun testIsInternalDeeplink_urlIsHostOneLinkTTID_returnTrue() {
        val result = isInternalDeeplinkUseCase.execute("link $HOST_ONELINK_TTID link")
        assertTrue(result)
    }

    @Test
    fun testIsInternalDeeplink_urlIsHostTrueId_returnTrue() {
        val result = isInternalDeeplinkUseCase.execute("link $HOST_TRUE_ID link")
        assertTrue(result)
    }

    @Test
    fun testIsInternalDeeplink_urlIsKeyOneLink_returnTrue() {
        val result = isInternalDeeplinkUseCase.execute("link $KEY_ONELINK link")
        assertTrue(result)
    }
}
