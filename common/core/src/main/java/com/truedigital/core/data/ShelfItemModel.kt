package com.truedigital.core.data

open class ShelfItemModel(
    var id: String = "",
    var title: String = "",
    var thumb: String = "",
    var detail: String = "",
    var contentType: String = ""
)
data class Header(
    var shelfCode: String = "",
    var data: CommonHeaderFooterDataModel? = null,
    var navigate: String = "",
) : ShelfItemModel()
