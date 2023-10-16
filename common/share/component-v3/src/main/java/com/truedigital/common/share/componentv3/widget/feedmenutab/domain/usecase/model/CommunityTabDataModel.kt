package com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.model

import com.truedigital.common.share.componentv3.data.CommonAppbarViewType

data class CommunityTabDataModel(
    var communityTitle: String = "",
    var forYouTitle: String = "",
    var tabList: List<CommonAppbarViewType> = emptyList()
)
