package com.truedigital.common.share.datalegacy.domain.model.trueidwebview

import com.truedigital.common.share.datalegacy.domain.discover.model.shelf.ShelfModel
import kotlinx.parcelize.Parcelize

@Parcelize
class TrueIdWebViewShelfModel(
    var isShowCloseButton: Boolean = false,
    var isShowWarningDialog: Boolean = false,
    var isShowAllDialog: Boolean = false,
    var functionKeys: String = "",
    var bannerName: String = "",
    var bannerCategory: String = "",
    var bannerCampaign: String = ""
) : ShelfModel()
