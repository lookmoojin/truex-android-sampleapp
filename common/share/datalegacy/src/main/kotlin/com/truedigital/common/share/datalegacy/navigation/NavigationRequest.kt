package com.truedigital.common.share.datalegacy.navigation

import com.truedigital.common.share.datalegacy.domain.discover.model.shelf.BaseShelfModel
import com.truedigital.common.share.datalegacy.domain.discover.model.shelf.ShelfModel

class NavigationRequest {

    /**
     * New Structure
     */
    var baseShelfModel: BaseShelfModel? = null
    var shelfModel: ShelfModel? = null
    var queryMap: HashMap<String, String>? = null
    // ////////////////////////

    var shelfSlug = ""
    var cmsId = ""
    var adsTagsUrl = ""
    var shelfIndex = ""
    var itemIndex = ""
    var shelfName = ""
    var shelfCode = ""
    var recommendationSchemaId = ""
    var isAutoPlay: Boolean = false

    var id = ""
    var apiUrl = ""
    var slug = ""
    var deepLink = ""

    // CampaignInfo
    var campaignName = ""

    // DealContentInfo
    var masterID = ""
    var category = ""

    // DramaScriptContentInfo
    var episodeId = ""

    // SongContentInfo
    var musicCode = ""

    // MatchContentInfo
    var channelCode = ""

    // SportClipInfo
    var streamName = ""

    // SeemoreInfo
    var index: Int = 0

    // MainSetting
    var callSync: Boolean = false
}
