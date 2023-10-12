package com.truedigital.core.data

import com.google.android.gms.ads.AdSize

open class ShelfItemModel(
    var id: String = "",
    var title: String = "",
    var thumb: String = "",
    var detail: String = "",
    var contentType: String = ""
)

open class ContentShelfItem(
    var articleCategory: String = ""
) : ShelfItemModel()

open class ArticleContentShelfItem(
    var nameArticleCategoryDetail: String = "",
    var slugArticleCategoryDetail: String = "",
    var landscapeImage: String = "",
    var publishDate: String = ""
) : ContentShelfItem()

data class MovieShelfItemModel(
    var badgeType: String = "",
    var contentRights: String = "",
    var enableNewDesign: Boolean = false,
    var exclusiveBadgeNotExpired: Boolean = false,
    var exclusiveBadgeType: String = "",
    var originalId: String = "",
    var subscriptionTiers: List<String>? = null,
    var movieType: String = "",
    var genres: String = "",
    var posterImage: String = "",
    var squareImage: String = "",
    var landscapeImage: String = "",
    var titleLogo: String = "",
    var caption: String = "",
    var captionDetail: String = "",
    var vodOtherType: String = "",
    var recommendSchemaId: String = "",
    var sourceType: String = ""
) : ContentShelfItem()

data class TvShelfItemModel(
    var apiUrl: String = "",
    var channelName: String = "",
    var contentId: String = "",
    var epgThumb: String = "",
    var epgTitle: String = "",
    var recommend: String = "",
    var slug: String = "",
    var totalCcu: Int = 0,
    var indexOfCcu: String = "",
    var channelCode: String = ""

) : ContentShelfItem()

data class ComponentShelfModel(
    var adSize: List<AdSize> = listOf(),
    var adSizeTablet: List<AdSize> = listOf(),
    var componentName: String = "",
    var adsId: String = "",
    var dataItemJson: String = "",
    var description: String = "",
    var shelfId: String = "",
    var slug: String = "",
    var limit: String = ""
) : ShelfItemModel()

data class Header(
    var shelfCode: String = "",
    var data: CommonHeaderFooterDataModel? = null,
    var navigate: String = "",
) : ShelfItemModel()

data class PersonalizeShelf(
    var recommendSchemaId: String = "",
    var sourceType: String = ""
) : ShelfItemModel()
