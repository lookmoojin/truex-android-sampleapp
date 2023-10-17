package com.truedigital.features.music.domain.landing.model

import com.truedigital.features.music.constant.MusicShelfType
import com.truedigital.features.tuned.data.productlist.model.ProductListType

data class MusicForYouShelfModel(
    val index: Int = 0,
    var shelfIndex: Int = -1,
    var shelfId: String = "",
    var title: String = "",
    var titleFA: String = "",
    var productListType: ProductListType = ProductListType.UNSUPPORTED,
    val options: ItemOptionsModel? = null,
    var shelfType: MusicShelfType = MusicShelfType.HORIZONTAL,
    var seeMoreDeeplinkPair: Pair<MusicHeroBannerDeeplinkType, String>? = null,
    var itemList: List<MusicForYouItemModel> = emptyList()
)
