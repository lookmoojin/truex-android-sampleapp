package com.truedigital.common.share.datalegacy.domain.profile.model

data class SocialConfigureTabModel(
    var id: Int = 0,
    var title: String = "",
    var titleShortByLocalized: String = "",
    var titleInTrendByLocalized: String = "",
    var order: Int = 0,
    var enable: Boolean = false,
    var hidden: Boolean = false
)
