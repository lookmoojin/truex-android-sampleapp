package com.truedigital.common.share.datalegacy.domain.discover.model.shelf

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class BaseShelfModel(
    var shelfSlug: String = "",
    var shelfCode: String = "",
    var title: String = "",
    var baseShelfIcon: String = "",
    var analyticLabel: String = "",
    var index: Int = -1,
    var indexWithoutAds: Int = -1,
    var searchMenuOrder: String = "",
    var searchMenuTitle: String = "",
    var seeMoreDeepLink: String = "",
    var topNavAnalyticLabel: String = "",
    var topNavTitle: String = "",
    var topNavOrder: String = "",
    var baseShelfId: String = "",
    var contentListId: String = "",
    var icon: String = "",
    var headerLogo: String = "",
    var trueidPlusOnboardingShelfId: String = "",
    var trueidPlusSubscribePartnerCode: String = "",
    var trueidPlusSubscribeProductCode: String = "",
) : Parcelable
