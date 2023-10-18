package com.truedigital.common.share.componentv3.widget.feedmenutab.presentation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.data.CommonAppbarViewType
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetAmityConfigUseCase
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetCommunityTabConfigUseCase
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.model.CommunityTabDataModel
import com.truedigital.community.domain.usecase.CommunityShortCutUseCase
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.launchSafe
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommunityTabViewModel @Inject constructor(
    private val communityShortCutUseCase: CommunityShortCutUseCase,
    private val getAmityConfigUseCase: GetAmityConfigUseCase,
    private val getCommunityTabConfigUseCase: GetCommunityTabConfigUseCase
) : ScopedViewModel() {

    val onTabSelectChange = MutableLiveData<CommunityTabEnum>()
    val onHideCommunityTab = MutableLiveData<Unit>()
    val onShowCommunityTab = SingleLiveEvent<Pair<CommunityTabDataModel, CommunityTabEnum?>>()
    val onSendTrackingScreen = MutableLiveData<Pair<String?, Int?>>()

    private var tabTypeDecodeDeepLink: CommunityTabEnum? = null
    private var tabTitlePopularFeed: String? = null
    private var featureConfigListOf = mutableListOf(CommonAppbarViewType.SHOW_ICON_ROW)

    fun checkCommunityTabEnable() {
        launchSafe {
            val isEnable = getAmityConfigUseCase.execute()
            if (isEnable) {
                getCommunityTabData()
            } else {
                onHideCommunityTab.value = Unit
            }
        }
    }

    fun focusCommunityTab(tabType: CommunityTabEnum) {
        setTabTypeFromDeeplink(tabType)
        getPopularTitle()?.let { popularTitle ->
            setSelectedTab(tabType)
            setTrackingScreen(
                tabType,
                popularTitle
            )
        }
    }

    fun setSelectedTab(tabSelected: CommunityTabEnum) {
        if (tabSelected != onTabSelectChange.value) {
            onTabSelectChange.value = tabSelected
        }
    }

    fun setTrackingScreen(tabType: CommunityTabEnum?, communityTitle: String) {
        onSendTrackingScreen.value = getTabTitle(tabType, communityTitle)
    }

    @VisibleForTesting
    fun setTabTypeDecodeDeepLink(tabType: CommunityTabEnum) {
        this.tabTypeDecodeDeepLink = tabType
    }

    private suspend fun getCommunityTabData() {
        communityShortCutUseCase.execute()
            .map {
                if (it) {
                    featureConfigListOf
                        .add(CommonAppbarViewType.SHOW_COMMUNITY_SHORTCUT)
                }
            }.collectSafe {
                getCommunityTabConfigUseCase.execute()
                    .catch {
                        onShowCommunityTab.value = Pair(
                            CommunityTabDataModel().apply {
                                tabList = featureConfigListOf
                            },
                            null
                        )
                    }
                    .collectSafe { communityTabData ->
                        if (communityTabData != null) {
                            featureConfigListOf
                                .add(CommonAppbarViewType.SHOW_COMMUNITY_TAB)
                            communityTabData.tabList = featureConfigListOf
                            val tabType = getTabType()
                            tabTitlePopularFeed = communityTabData.communityTitle
                            onShowCommunityTab.value = Pair(communityTabData, tabType)
                        } else {
                            onShowCommunityTab.value = Pair(
                                CommunityTabDataModel().apply {
                                    this.tabList = featureConfigListOf
                                },
                                null
                            )
                        }
                    }
            }
    }

    private fun getPopularTitle() = tabTitlePopularFeed

    private fun setTabTypeFromDeeplink(tabTypeDeepLink: CommunityTabEnum) {
        tabTypeDecodeDeepLink = tabTypeDeepLink
    }

    private fun getTabTitle(
        tabType: CommunityTabEnum?,
        communityTitle: String
    ): Pair<String?, Int?> =
        if (tabType == CommunityTabEnum.FOR_YOU) {
            Pair(null, R.string.today_txt_for_you)
        } else {
            Pair(communityTitle, null)
        }

    private fun getTabType() = if (tabTypeDecodeDeepLink != null) {
        tabTypeDecodeDeepLink
    } else {
        onTabSelectChange.value
    }
}
