package com.truedigital.common.share.nativeshare.domain.usecase.dynamiclink

import com.truedigital.common.share.nativeshare.R
import com.truedigital.common.share.nativeshare.domain.model.DynamicLinkForTestModel
import com.truedigital.common.share.nativeshare.domain.model.dynamiclink.GenerateDynamicLinkModel
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder
import com.truedigital.share.mock.utils.JsonHelper
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DynamicLinkGeneratorUseCaseTest {

    private val contextDataProvider: ContextDataProvider = mockk(relaxed = true)
    private val dynamicLinkGeneratorCallback: DynamicLinkGeneratorCallback = mockk()

    private lateinit var dynamicLinkGeneratorUseCase: DynamicLinkGeneratorUseCase

    @BeforeEach
    fun setUp() {
        dynamicLinkGeneratorUseCase = DynamicLinkGeneratorUseCaseImpl(
            contextDataProvider
        )
    }

    @Nested
    inner class GenerateDynamicLink {
        @Test
        fun `GenerateDynamicLink is goto google play is true`() {
            val dynamicLink = JsonHelper.fromJson<DynamicLinkForTestModel>("DynamicLink.json")

            mockkObject(ConnectivityStateHolder)

            every {
                ConnectivityStateHolder.isConnected
            } returns (false)

            val onFail = mockk<DynamicLinkGeneratorCallback> {
                every { onFailure(any()) } just runs
            }

            every {
                contextDataProvider.getString(R.string.nativeshare_no_internet_connection)
            } returns ("ไม่สามารถเชื่อมต่ออินเตอร์เน็ตได้ กรุณาลองอีกครั้ง")

            dynamicLinkGeneratorUseCase.generateDynamicLink(
                dynamicLink.dynamicLinkModel[0],
                contentId = "",
                callback = onFail
            )
        }

        @Test
        fun `GenerateDynamicLink is goto google play is false`() {
            val dynamicLink = JsonHelper.fromJson<DynamicLinkForTestModel>("DynamicLink.json")

            mockkObject(ConnectivityStateHolder)

            every {
                ConnectivityStateHolder.isConnected
            } returns (false)

            val onFail = mockk<DynamicLinkGeneratorCallback> {
                every { onFailure(any()) } just runs
            }

            every {
                contextDataProvider.getString(R.string.nativeshare_no_internet_connection)
            } returns ("ไม่สามารถเชื่อมต่ออินเตอร์เน็ตได้ กรุณาลองอีกครั้ง")

            dynamicLinkGeneratorUseCase.generateDynamicLink(
                dynamicLink.dynamicLinkModel[1],
                contentId = "",
                callback = onFail
            )
        }

        @Test
        fun `GenerateDynamicLink test input data type and shareUrl`() {
            mockkObject(ConnectivityStateHolder)

            every {
                ConnectivityStateHolder.isConnected
            } returns (false)
            val callback = mockk<DynamicLinkGeneratorCallback> {
                every { onFailure(any()) } just runs
            }

            every {
                contextDataProvider.getString(R.string.nativeshare_no_internet_connection)
            } returns ("ไม่สามารถเชื่อมต่ออินเตอร์เน็ตได้ กรุณาลองอีกครั้ง")
            dynamicLinkGeneratorUseCase.generateDynamicLink(
                dynamicLinkModel = getGenerateDynamicLinkModel("", "?", JSONObject()),
                callback = callback
            )
            dynamicLinkGeneratorUseCase.generateDynamicLink(
                dynamicLinkModel = getGenerateDynamicLinkModel("", "", JSONObject()),
                callback = callback
            )
            dynamicLinkGeneratorUseCase.generateDynamicLink(
                dynamicLinkModel = getGenerateDynamicLinkModel("series", "moview", JSONObject()),
                callback = callback
            )
            dynamicLinkGeneratorUseCase.generateDynamicLink(
                dynamicLinkModel = getGenerateDynamicLinkModel("album", "music", JSONObject()),
                callback = callback
            )
            dynamicLinkGeneratorUseCase.generateDynamicLink(
                dynamicLinkModel = getGenerateDynamicLinkModel("song", "music", JSONObject()),
                callback = callback
            )
            dynamicLinkGeneratorUseCase.generateDynamicLink(
                dynamicLinkModel = getGenerateDynamicLinkModel("playlist", "music", JSONObject()),
                callback = callback
            )
            dynamicLinkGeneratorUseCase.generateDynamicLink(
                dynamicLinkModel = getGenerateDynamicLinkModel("", "?", null),
                callback = callback
            )
            dynamicLinkGeneratorUseCase.generateDynamicLink(
                dynamicLinkModel = getGenerateDynamicLinkModel("", "?", null),
                contentId = "1",
                callback = callback
            )
        }
    }

    fun getGenerateDynamicLinkModel(type: String, shareUrl: String, info: JSONObject?): GenerateDynamicLinkModel {
        return GenerateDynamicLinkModel(
            title = "",
            description = "",
            imageUrl = "",
            type = type,
            slug = "",
            info = info,
            shareUrl = shareUrl,
            goToPlayStore = false
        )
    }

    @Nested
    inner class GenerateDynamicLinkWithOutContentId {
        @Test
        fun `GenerateDynamicLink is goto google play is true`() {
            val dynamicLink = JsonHelper.fromJson<DynamicLinkForTestModel>("DynamicLink.json")

            mockkObject(ConnectivityStateHolder)

            every {
                ConnectivityStateHolder.isConnected
            } returns (false)

            val onFail = mockk<DynamicLinkGeneratorCallback> {
                every { onFailure(any()) } just runs
            }

            every {
                contextDataProvider.getString(R.string.nativeshare_no_internet_connection)
            } returns ("ไม่สามารถเชื่อมต่ออินเตอร์เน็ตได้ กรุณาลองอีกครั้ง")

            dynamicLinkGeneratorUseCase.generateDynamicLink(dynamicLink.dynamicLinkModel[0], onFail)
        }

        @Test
        fun `GenerateDynamicLink is goto google play is false`() {
            val dynamicLink = JsonHelper.fromJson<DynamicLinkForTestModel>("DynamicLink.json")

            mockkObject(ConnectivityStateHolder)

            every {
                ConnectivityStateHolder.isConnected
            } returns (false)

            val onFail = mockk<DynamicLinkGeneratorCallback> {
                every { onFailure(any()) } just runs
            }

            every {
                contextDataProvider.getString(R.string.nativeshare_no_internet_connection)
            } returns ("ไม่สามารถเชื่อมต่ออินเตอร์เน็ตได้ กรุณาลองอีกครั้ง")

            dynamicLinkGeneratorUseCase.generateDynamicLink(dynamicLink.dynamicLinkModel[1], onFail)
        }
    }

    @Test
    fun generateDynamicLinkToSingle() {
        val dynamicLink = JsonHelper.fromJson<DynamicLinkForTestModel>("DynamicLink.json")

        mockkObject(ConnectivityStateHolder)

        every {
            ConnectivityStateHolder.isConnected
        } returns (false)

        every {
            contextDataProvider.getString(R.string.nativeshare_no_internet_connection)
        } returns ("ไม่สามารถเชื่อมต่ออินเตอร์เน็ตได้ กรุณาลองอีกครั้ง")

        dynamicLinkGeneratorUseCase.generateDynamicLinkToSingle(dynamicLink.dynamicLinkModel[1]).test().let {
            it.assertNoValues()
            it.assertError { true }
        }
    }
}
