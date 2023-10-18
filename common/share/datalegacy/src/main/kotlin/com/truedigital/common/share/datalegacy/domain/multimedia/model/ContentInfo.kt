package com.truedigital.common.share.datalegacy.domain.multimedia.model

import com.truedigital.common.share.datalegacy.domain.discover.model.shelf.ShelfModel
import kotlinx.parcelize.Parcelize

@Parcelize
open class ContentInfo(
    var id: String = "",
    var apiUrl: String = "",
    var slug: String = "",
    var slugTvChannel: String = "",
    var subscriptionTiers: List<String> = arrayListOf(),
    var subscriptionPackage: List<String> = arrayListOf(),
    var channelCode: String = "",
    var contentType: String = "",
    open var startDate: String = "",

    // token for load more
    var nextPage: String = "",
    var relateTeam: List<String> = arrayListOf(),
    var deeplink: String = "",
    var itemIndex: String = "",
    var shelfIndex: String = "",
    var shelfName: String = "",
    var shelfCode: String = "",
    var detail: String = "",
    var countViews: Int = 0,
    var countLikes: Int = 0,
    var isTrueVisions: Boolean = false,
    var subscriptionOffRequireLogin: String = "",
    var allowChromeCast: String = "",
    var viewType: Int? = null,
    var access: String = "",
    var detailEn: String = "",
    var titleEn: String = "",
    var titleTh: String = "",
    var icon: String = "",
    var schemaId: String = "",
    var contentTypeTag: String = "",
    var isAutoPlay: Boolean = false,
    var recommendationSchemaId: String = "",
    var publishDate: String = "",
    var leagueCode: String = "",
    var articleCategoryList: List<String> = listOf(),
    var packageCollectionId: String = "",
    open var endDate: String = "",

    // catch up args
    open var channelCmsId: String = "",
    var channelTitle: String = "",
    var channelTitleId: String = "",
    var channelEpisodeId: String = "",
    var epgCmsId: String = "",
    var isDualLanguage: Boolean = false
) : ShelfModel()
