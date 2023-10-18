package com.truedigital.common.share.datalegacy.domain.discover.model.shelf

import android.os.Parcelable
import com.truedigital.common.share.datalegacy.domain.discover.model.info.BaseInfoModel
import kotlinx.parcelize.Parcelize

@Parcelize
open class ShelfModel(
    var analyticLabel: String = "",
    var index: Int = -1,
    var info: BaseInfoModel? = null,
    var shelfSlug: String = "",
    open var title: String = "",
    var thumbnail: String = "",
    var type: String = "",
    var deepLinkUrl: String = ""
) : Parcelable
