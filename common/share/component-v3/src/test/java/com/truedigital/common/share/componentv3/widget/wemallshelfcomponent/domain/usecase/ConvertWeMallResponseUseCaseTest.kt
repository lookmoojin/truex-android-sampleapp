package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.usecase

import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseData
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseItems
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallParametersModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallParametersSettingModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallShelfItemModel
import com.truedigital.core.extensions.collectSafe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ConvertWeMallResponseUseCaseTest {
    private lateinit var convertWeMallResponseUseCase: ConvertWeMallResponseUseCase

    @BeforeEach
    fun setup() {
        convertWeMallResponseUseCase = ConvertWeMallResponseUseCaseImpl()
    }

    @Test
    fun `When executed success should return data`() = runTest {
        val mockedResponse = WeMallShelfResponseModel(
            code = "code",
            data = listOf(
                WeMallShelfResponseData(
                    categoryName = "truepackages",
                    categoryUrl = "categoryUrl",
                    limit = "10",
                    items = listOf(
                        WeMallShelfResponseItems(
                            id = "id",
                            title = "title",
                            itemUrl = "itemUrl",
                            thumbnail = "thumbnail",
                            prices = listOf("11")
                        )
                    )
                )
            )
        )

        val mockedParametersModel = WeMallParametersModel(
            id = "oLkAxYXaD41q",
            contentType = "misc",
            title = "True Packages",
            thumb = "",
            setting = WeMallParametersSettingModel(
                category = "truepackages",
                componentName = "wemall",
                limit = "10",
                seeMore = "https://home.trueid.net/webview-dynamic-token?website=https://account.wemall.com/third-party/trueid/login?ref=https://www.wemall.com",
                theme = "white"
            )
        )

        convertWeMallResponseUseCase.execute(
            content = mockedResponse,
            parametersModel = mockedParametersModel
        ).collectSafe { result ->
            assertNotNull(result)
            assertTrue {
                result is WeMallShelfItemModel
            }
        }
    }

    @Test
    fun `When executed success should return data with empty data`() = runTest {
        val mockedResponse = WeMallShelfResponseModel(
            code = "code",
            data = listOf(
                WeMallShelfResponseData(
                    categoryName = null,
                    categoryUrl = null,
                    limit = "10",
                    items = listOf(
                        WeMallShelfResponseItems(
                            id = null,
                            title = null,
                            itemUrl = null,
                            thumbnail = null,
                            prices = listOf("11")
                        )
                    )
                )
            )
        )

        val mockedParametersModel = WeMallParametersModel(
            id = "",
            contentType = "misc",
            title = "",
            thumb = "",
            setting = WeMallParametersSettingModel(
                category = "",
                componentName = "wemall",
                limit = "10",
                seeMore = "https://home.trueid.net/webview-dynamic-token?website=https://account.wemall.com/third-party/trueid/login?ref=https://www.wemall.com",
                theme = "white"
            )
        )

        convertWeMallResponseUseCase.execute(
            content = mockedResponse,
            parametersModel = mockedParametersModel
        ).collectSafe { result ->
            assertNotNull(result)
            assertEquals("", result.itemUrl)
            assertEquals("", result.categoryUrl)
            assertEquals("", result.thumb)
            assertEquals("", result.cmsId)
            assertEquals("", result.title)
            assertEquals("", result.categoryName)
            assertEquals("", result.shelfName)
            assertEquals("", result.shelfCode)
        }
    }

    @Test
    fun `When executed success should return data without WeMallParametersModel`() =
        runTest {
            val mockedResponse = WeMallShelfResponseModel(
                code = "code",
                data = null
            )
            val mockedParametersModel = WeMallParametersModel(
                id = "oLkAxYXaD41q",
                contentType = "misc",
                title = "True Packages",
                thumb = "",
                setting = WeMallParametersSettingModel(
                    category = "truepackages",
                    componentName = "wemall",
                    limit = "10",
                    seeMore = "https://home.trueid.net/webview-dynamic-token?website=https://account.wemall.com/third-party/trueid/login?ref=https://www.wemall.com",
                    theme = "white"
                )
            )

            convertWeMallResponseUseCase.execute(
                content = mockedResponse,
                parametersModel = mockedParametersModel
            ).collectSafe { result ->
                assertNull(result)
            }
        }
}
