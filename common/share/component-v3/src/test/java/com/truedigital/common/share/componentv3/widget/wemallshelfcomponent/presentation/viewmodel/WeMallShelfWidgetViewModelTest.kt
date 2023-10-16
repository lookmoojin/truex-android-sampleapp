package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.presentation.viewmodel

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfRequestModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseData
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseItems
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallParametersModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallParametersSettingModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallShelfItemModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.usecase.ConvertWeMallResponseUseCase
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.usecase.GetWeMallShelfContentUseCase
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.presentation.WeMallShelfWidgetViewModel
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.repository.DeviceRepository
import com.truedigital.foundation.extension.test.LiveDataTestObserver
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@Suppress("EXPERIMENTAL_API_USAGE")
@ExtendWith(InstantTaskExecutorExtension::class)
class WeMallShelfWidgetViewModelTest {

    @RegisterExtension
    @JvmField
    var testCoroutine = TestCoroutinesExtension()

    private lateinit var viewModel: WeMallShelfWidgetViewModel
    private val analyticManager: AnalyticManager = mock()
    private val convertWeMallResponseUseCase: ConvertWeMallResponseUseCase = mock()
    private val getWeMallShelfContentUseCase: GetWeMallShelfContentUseCase = mock()
    private val deviceRepository: DeviceRepository = mock()
    private val userRepository: UserRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)

    @BeforeEach
    fun setup() {
        viewModel = WeMallShelfWidgetViewModel(
            convertWeMallResponseUseCase,
            getWeMallShelfContentUseCase,
            deviceRepository,
            localizationRepository,
            userRepository,
            coroutineDispatcher,
            analyticManager
        )
    }

    private fun getStringComponent(): String =
        "{\"api_url\":\"\",\"channel_name\":\"\",\"content_id\":\"\",\"content_rights\":\"\",\"content_type\":\"misc\",\"detail\":\"\",\"epg_thumb\":\"\",\"epg_title\":\"\",\"exclusive_badge_end\":\"\",\"exclusive_badge_start\":\"\",\"exclusive_badge_type\":\"\",\"expire_date\":\"\",\"expired_date\":\"\",\"howto\":\"\",\"id\":\"oLkAxYXaD41q\",\"lang\":\"\",\"movie_type\":\"\",\"newepi_badge_end\":\"\",\"newepi_badge_start\":\"\",\"newepi_badge_type\":\"\",\"order\":\"\",\"original_id\":\"\",\"price\":\"\",\"publish_date\":\"\",\"quota_over_existed\":0,\"recommend\":\"\",\"redeem_point\":\"\",\"setting\":{\"ads_id\":\"\",\"category\":\"truepackages\",\"component_name\":\"wemall\",\"experiment\":\"\",\"key\":\"\",\"limit\":\"10\",\"random\":\"\",\"seemore\":\"https://home.trueid.net/webview-dynamic-token?website\\u003dhttps://account.wemall.com/third-party/trueid/login?ref\\u003dhttps://www.wemall.com\",\"theme\":\"white\",\"value\":\"\"},\"slug\":\"\",\"start_date\":\"\",\"status\":\"\",\"thumb\":\"\",\"thumb_list\":{\"poster\":\"\",\"square_image\":\"\"},\"title\":\"True Packages\",\"total_ccu\":\"\"}"

    @Test
    fun `getWeMallShelfLayout_happyCase`() = runTest {

        val mockedResponse = WeMallShelfResponseModel()
        whenever(deviceRepository.getAndroidId()).thenReturn("androidId")
        whenever(localizationRepository.getAppLanguageCodeForEnTh()).thenReturn("en")
        whenever(userRepository.getSsoId()).thenReturn("ssoId")
        whenever(userRepository.getAccessToken()).thenReturn("accessToken")
        whenever(
            getWeMallShelfContentUseCase.execute(
                "accessToken",
                WeMallShelfRequestModel(
                    "androidId",
                    ssoID = "ssoId",
                    lang = "en",
                    categoryName = "truepackages",
                    limit = "10"
                )
            )
        ).thenReturn(
            flowOf(mockedResponse)
        )

        val testObserver = LiveDataTestObserver.test(viewModel.responseItem)

        viewModel.getWeMallShelfLayout(getStringComponent())
        testObserver.assertValue(
            WeMallShelfResponseModel()
        )
    }

    @Test
    fun `getWeMallShelfLayout_nullCase`() = runTest {
        whenever(deviceRepository.getAndroidId()).thenReturn("androidId")
        whenever(localizationRepository.getAppLanguageCodeForEnTh()).thenReturn("en")
        whenever(userRepository.getSsoId()).thenReturn("ssoId")
        whenever(userRepository.getAccessToken()).thenReturn("accessToken")
        whenever(
            getWeMallShelfContentUseCase.execute(
                "accessToken",
                WeMallShelfRequestModel(
                    "androidId",
                    ssoID = "ssoId",
                    lang = "en",
                    categoryName = "truepackages",
                    limit = "10"
                )
            )
        ).thenReturn(
            flowOf(null)
        )

        val testObserver = LiveDataTestObserver.test(viewModel.responseItem)
        viewModel.getWeMallShelfLayout(getStringComponent())
        testObserver.assertNoValue()
    }

    @Test
    fun `transformData_happyCase`() = runTest {
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
        whenever(deviceRepository.getAndroidId()).thenReturn("androidId")
        whenever(localizationRepository.getAppLanguageCodeForEnTh()).thenReturn("en")
        whenever(userRepository.getSsoId()).thenReturn("ssoId")
        whenever(userRepository.getAccessToken()).thenReturn("accessToken")
        whenever(
            convertWeMallResponseUseCase.execute(
                content = mockedResponse,
                parametersModel = WeMallParametersModel()
            )
        ).thenReturn(
            flowOf(WeMallShelfItemModel())
        )
        whenever(
            getWeMallShelfContentUseCase.execute(
                "accessToken",
                WeMallShelfRequestModel(
                    "androidId",
                    ssoID = "ssoId",
                    lang = "en",
                    categoryName = "truepackages",
                    limit = "10"
                )
            )
        ).thenReturn(
            flowOf(mockedResponse)
        )
        val testWeMallShelfLayoutObserver = LiveDataTestObserver.test(viewModel.weMallShelfLayout)
        viewModel.getWeMallShelfLayout(getStringComponent())
        viewModel.transformData(mockedResponse)

        testWeMallShelfLayoutObserver.assertValue(
            WeMallParametersModel(
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
        )
    }
}
