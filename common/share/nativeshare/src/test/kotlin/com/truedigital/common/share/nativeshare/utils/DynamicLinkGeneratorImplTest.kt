package com.truedigital.common.share.nativeshare.utils

import com.truedigital.common.share.nativeshare.R
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DynamicLinkGeneratorImplTest {
    private val contextDataProvider: ContextDataProvider = mockk(relaxed = true)
    private lateinit var dynamicLinkGeneratorImp: DynamicLinkGeneratorImpl

    @BeforeEach
    fun setUp() {
        dynamicLinkGeneratorImp = DynamicLinkGeneratorImpl(
            contextDataProvider
        )
    }

    @Test
    fun `generateDynamicLink is goto google play is true`() {
        mockkObject(ConnectivityStateHolder)

        every {
            ConnectivityStateHolder.isConnected
        } returns (false)
        val callback = mockk<ShortenUrlCallback> {
            every { onFailure(any()) } just runs
        }

        every {
            contextDataProvider.getString(R.string.nativeshare_no_internet_connection)
        } returns ("ไม่สามารถเชื่อมต่ออินเตอร์เน็ตได้ กรุณาลองอีกครั้ง")
        dynamicLinkGeneratorImp.generateDynamicLink(
            title = "",
            description = "",
            imageUrl = "",
            type = "series",
            slug = "",
            info = JSONObject(),
            shareUrl = "movie",
            goToPlayStore = true,
            contentId = "",
            callback = callback
        )
    }

    @Test
    fun `generateDynamicLink is goto google play is false`() {
        mockkObject(ConnectivityStateHolder)

        every {
            ConnectivityStateHolder.isConnected
        } returns (false)
        val callback = mockk<ShortenUrlCallback> {
            every { onFailure(any()) } just runs
        }

        every {
            contextDataProvider.getString(R.string.nativeshare_no_internet_connection)
        } returns ("ไม่สามารถเชื่อมต่ออินเตอร์เน็ตได้ กรุณาลองอีกครั้ง")
        dynamicLinkGeneratorImp.generateDynamicLink(
            title = "",
            description = "",
            imageUrl = "",
            type = "playlist",
            slug = "",
            info = JSONObject(),
            shareUrl = "music",
            goToPlayStore = false,
            contentId = "",
            callback = callback
        )
    }

    @Test
    fun `generateDynamicLink not contentId is goto google play is true`() {
        mockkObject(ConnectivityStateHolder)

        every {
            ConnectivityStateHolder.isConnected
        } returns (false)
        val callback = mockk<ShortenUrlCallback> {
            every { onFailure(any()) } just runs
        }

        every {
            contextDataProvider.getString(R.string.nativeshare_no_internet_connection)
        } returns ("ไม่สามารถเชื่อมต่ออินเตอร์เน็ตได้ กรุณาลองอีกครั้ง")

        dynamicLinkGeneratorImp.generateDynamicLink(
            title = "",
            description = "",
            imageUrl = "",
            type = "movie",
            slug = "",
            info = JSONObject(),
            shareUrl = "movie",
            goToPlayStore = true,
            callback = callback
        )
    }

    @Test
    fun `generateDynamicLink not contentId is goto google play is false`() {
        mockkObject(ConnectivityStateHolder)
        every {
            ConnectivityStateHolder.isConnected
        } returns (false)
        val callback = mockk<ShortenUrlCallback> {
            every { onFailure(any()) } just runs
        }

        every {
            contextDataProvider.getString(R.string.nativeshare_no_internet_connection)
        } returns ("ไม่สามารถเชื่อมต่ออินเตอร์เน็ตได้ กรุณาลองอีกครั้ง")

        dynamicLinkGeneratorImp.generateDynamicLink(
            title = "",
            description = "",
            imageUrl = "",
            type = "",
            slug = "",
            info = JSONObject(),
            shareUrl = "",
            goToPlayStore = false,
            callback = callback
        )
    }

    @Test
    fun `generateDynamicLink input data type and shareUrl`() {
        mockkObject(ConnectivityStateHolder)

        every {
            ConnectivityStateHolder.isConnected
        } returns (false)
        val callback = mockk<ShortenUrlCallback> {
            every { onFailure(any()) } just runs
        }

        every {
            contextDataProvider.getString(R.string.nativeshare_no_internet_connection)
        } returns ("ไม่สามารถเชื่อมต่ออินเตอร์เน็ตได้ กรุณาลองอีกครั้ง")

        dynamicLinkGeneratorImp.generateDynamicLink(
            title = "",
            description = "",
            imageUrl = "",
            type = "",
            slug = "",
            info = JSONObject(),
            shareUrl = "?",
            goToPlayStore = false,
            callback = callback
        )

        dynamicLinkGeneratorImp.generateDynamicLink(
            title = "",
            description = "",
            imageUrl = "",
            type = "",
            slug = "",
            info = null,
            shareUrl = "?",
            goToPlayStore = false,
            contentId = "id",
            callback = callback
        )

        dynamicLinkGeneratorImp.generateDynamicLink(
            title = "",
            description = "",
            imageUrl = "",
            type = "",
            slug = "",
            info = null,
            shareUrl = "?",
            goToPlayStore = false,
            callback = callback
        )
    }

    @Test
    fun `generateDynamicLinkToSingle is goto google play is true`() {
        mockkObject(ConnectivityStateHolder)
        every {
            ConnectivityStateHolder.isConnected
        } returns (true)
        val testObserver = dynamicLinkGeneratorImp.generateDynamicLinkToSingle(
            title = "",
            description = "",
            imageUrl = "",
            type = "series",
            slug = "",
            info = JSONObject(),
            shareUrl = "movie",
            goToPlayStore = true
        ).test()

        testObserver.assertError { true }
    }

    @Test
    fun `generateDynamicLinkToSingle is goto google play is false`() {
        mockkObject(ConnectivityStateHolder)
        every {
            ConnectivityStateHolder.isConnected
        } returns (false)
        val testObserver = dynamicLinkGeneratorImp.generateDynamicLinkToSingle(
            title = "",
            description = "",
            imageUrl = "",
            type = "series",
            slug = "",
            info = JSONObject(),
            shareUrl = "movie",
            goToPlayStore = false
        ).test()

        testObserver.assertError { true }
    }
}
