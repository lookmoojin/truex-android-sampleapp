package com.truedigital.common.share.data.extension

import com.truedigital.common.share.data.coredata.deeplink.extension.isOneLink
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

interface OneLinkExtensionTestCase {
    fun `Given link is onelink When isOneLink Then true`()
    fun `Given link is onelink ttid When isOneLink Then true`()
    fun `Given link is not onelink When isOnelink Then false`()
}

class OneLinkExtensionTest : OneLinkExtensionTestCase {
    @Test
    override fun `Given link is onelink When isOneLink Then true`() {
        assertTrue("https://onelink.me/KupK/test2GBpage".isOneLink())
    }

    @Test
    override fun `Given link is onelink ttid When isOneLink Then true`() {
        assertTrue("https://ttid.co/KupK/test2GBpage".isOneLink())
    }

    @Test
    override fun `Given link is not onelink When isOnelink Then false`() {
        assertFalse("https://www.trueid.net/KupK/test2GBpage".isOneLink())
    }
}
