package com.truedigital.common.share.componentv3.widget.communitytab

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.data.CommonAppbarViewType
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetAmityConfigUseCase
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetCommunityTabConfigUseCase
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.model.CommunityTabDataModel
import com.truedigital.common.share.componentv3.widget.feedmenutab.presentation.CommunityTabEnum
import com.truedigital.common.share.componentv3.widget.feedmenutab.presentation.CommunityTabViewModel
import com.truedigital.community.domain.usecase.CommunityShortCutUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class CommunityTabViewModelTest {

    private lateinit var viewModel: CommunityTabViewModel
    private var communityShortCutUseCase: CommunityShortCutUseCase = mock()
    private var getAmityConfigUseCase: GetAmityConfigUseCase = mock()
    private var getCommunityTabConfigUseCase: GetCommunityTabConfigUseCase = mock()

    private val dataList = mutableListOf(
        CommonAppbarViewType.SHOW_ICON_ROW
    )

    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    @BeforeEach
    fun setUp() {
        viewModel = CommunityTabViewModel(
            communityShortCutUseCase = communityShortCutUseCase,
            getAmityConfigUseCase = getAmityConfigUseCase,
            getCommunityTabConfigUseCase = getCommunityTabConfigUseCase
        )
    }

    @Test
    fun test_get_amity_service_enable_and_community_tab_data_not_null() = runTest {
        whenever(getAmityConfigUseCase.execute()).thenReturn(
            true
        )
        whenever(communityShortCutUseCase.execute()).thenReturn(flowOf(true))
        whenever(getCommunityTabConfigUseCase.execute()).thenReturn(
            flow {
                emit(
                    CommunityTabDataModel().apply {
                        this.communityTitle = "communityTitle"
                    }
                )
            }
        )
        viewModel.setTabTypeDecodeDeepLink(CommunityTabEnum.POPULAR)
        viewModel.checkCommunityTabEnable()
        verify(getAmityConfigUseCase, times(1)).execute()
        verify(getCommunityTabConfigUseCase, times(1)).execute()

        assert(viewModel.onShowCommunityTab.value?.first?.communityTitle == "communityTitle")
        assert(viewModel.onShowCommunityTab.value?.second == CommunityTabEnum.POPULAR)
    }

    @Test
    fun test_get_amity_service_enable_and_community_tab_data_has_null() = runTest {
        whenever(getAmityConfigUseCase.execute()).thenReturn(
            true
        )
        whenever(communityShortCutUseCase.execute()).thenReturn(flowOf(true))
        whenever(getCommunityTabConfigUseCase.execute()).thenReturn(
            flow {
                emit(null)
            }
        )
        viewModel.checkCommunityTabEnable()
        verify(getAmityConfigUseCase, times(1)).execute()
        verify(getCommunityTabConfigUseCase, times(1)).execute()

        dataList.add(
            CommonAppbarViewType.SHOW_COMMUNITY_SHORTCUT
        )

        assert(viewModel.onShowCommunityTab.value?.first?.tabList == dataList)
        assert(viewModel.onShowCommunityTab.value?.second == null)
    }

    @Test
    fun test_get_amity_service_not_enable() = runTest {
        whenever(getAmityConfigUseCase.execute()).thenReturn(
            false
        )

        viewModel.checkCommunityTabEnable()
        verify(getAmityConfigUseCase, times(1)).execute()
        assert(viewModel.onHideCommunityTab.value == Unit)
    }

    @Test
    fun test_set_select_tab() = runTest {
        viewModel.onTabSelectChange.value = CommunityTabEnum.FOR_YOU
        viewModel.setSelectedTab(CommunityTabEnum.POPULAR)
        assert(viewModel.onTabSelectChange.value == CommunityTabEnum.POPULAR)
    }

    @Test
    fun testOnTrackingScreenWithOutDeeplink() {
        viewModel.setTrackingScreen(CommunityTabEnum.POPULAR, "communityTitle")
        assert(viewModel.onSendTrackingScreen.value?.first == "communityTitle")
        assert(viewModel.onSendTrackingScreen.value?.second == null)
    }

    @Test
    fun testOnTrackingScreen() {
        viewModel.setTrackingScreen(CommunityTabEnum.FOR_YOU, "communityTitle")
        assert(viewModel.onSendTrackingScreen.value?.first == null)
        assert(viewModel.onSendTrackingScreen.value?.second == R.string.today_txt_for_you)
    }
}
