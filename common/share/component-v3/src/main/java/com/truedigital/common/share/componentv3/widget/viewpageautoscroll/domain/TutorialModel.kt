package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain

data class TutorialModel(
    var textButton: String = "",
    var colorButtonText: String = "",
    var colorButton: String = "",
    var isShowTutorial: Boolean = false,
    var bannerBaseItemModelList: List<BannerBaseItemModel>? = null
)
