package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain

import androidx.annotation.DrawableRes

open class BannerBaseItemModel {
    var id: String = ""
    var thum: String = ""
    var title: String = ""
    var titleEn: String = ""
    var detail: String = ""
    var squareImage: String = ""
    var landscapeImage: String = ""
    var isNewDesign: Boolean = false
    var isTextVisible = true
    var isTutorial = false
    var isGradientViewVisible = true
    var isSecondGradientViewVisible = false
    var shelfId: String = ""
    var shelfTitle: String = ""
    var shelfTitleEn: String = ""
    var contentDescription: String = ""
    var imageContentDescription: String = ""
    var movieBannerModel: MovieBannerModel? = null
    var sportCountdownModel: SportCountdownModel = SportCountdownModel()

    @DrawableRes
    var thumResource: Int? = null

    inner class MovieBannerModel {
        var exclusiveBadgeNotExpired = false
        var caption = ""
        var captionDetail = ""
        var contentRights = ""
        var subscriptionTiers: List<String>? = null
        var contentType = ""
        var badgeType = ""
        var titleLogo = ""
    }

    inner class SportCountdownModel {
        var awayAka: String = ""
        var awayLogo: String = ""
        var channelImage: String = ""
        var contentType: String = ""
        var dayInt: Int = 0
        var dayString: String = ""
        var defaultBackground: Int = 0
        var defaultLiveBadge: Int = 0
        var homeAka: String = ""
        var homeLogo: String = ""
        var imagePlayerAway: String = ""
        var imagePlayerHome: String = ""
        var liveBadge: String = ""
        var matchDate: String = ""
        var previewBadge: String = ""
        var truePointImage: String = ""
        var voteImage: String = ""
    }
}
