package com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model

import com.google.gson.annotations.SerializedName
import com.truedigital.common.share.datalegacy.data.other.model.response.tv.ChannelInfo
import com.truedigital.common.share.datalegacy.data.repository.cmsfnsearch.model.response.ThumbList

class Shelf {
    @SerializedName("expire_date")
    var expireDate: String? = null

    @SerializedName("id")
    var id: String? = null

    var cursor: String = ""

    @SerializedName("duration")
    var duration: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("title_logo")
    var titleLogo: String? = null

    @SerializedName("publish_date")
    var publishDate: String? = null

    @SerializedName("partner_info")
    var partnerInfo: PartnerInfo? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("setting")
    var setting: Setting? = null

    @SerializedName("thumb")
    var thumb: String? = null

    @SerializedName("info")
    var info: Info? = null

    @SerializedName("count_views")
    var countView: Int = 0

    @SerializedName("count_likes")
    var countLike: Int = 0

    @SerializedName("ep_items")
    var episodeList: List<String>? = null

    @SerializedName("content_type")
    var contentType: String? = null

    @SerializedName("other_type")
    var otherType: String? = null

    @SerializedName("article_category")
    var articleCategory: List<String>? = null

    @SerializedName("genres")
    var genres: List<String>? = null

    @SerializedName("subscription_tiers")
    var subscriptionTiers: List<String>? = null

    @SerializedName("thumb_list")
    var thumbList: ThumbList? = null

    @SerializedName("movie_type")
    var movieType: String? = null

    @SerializedName("tvod_flag")
    var tvodFlag: String? = null

    @SerializedName("synopsis")
    var synopsis: String? = null

    @SerializedName("detail")
    var detail: String? = null

    @SerializedName("source_type")
    var sourceType: String? = null

    @SerializedName("source_url")
    var sourceUrl: String? = null

    @SerializedName("start_date")
    var startDate: String? = null

    @SerializedName("end_date")
    var endDate: String? = null

    @SerializedName("article_category_detail")
    var articleCategoryDetailList: List<Any>? = listOf()

    @SerializedName("content_rights")
    var contentRights: String? = null

    @SerializedName("newepi_badge_start")
    var newepiBadgeStart: String? = null

    @SerializedName("newepi_badge_end")
    var newepiBadgeEnd: String? = null

    @SerializedName("newepi_badge_type")
    var newepiBadgeType: String? = null

    @SerializedName("exclusive_badge_start")
    var exclusiveBadgeStart: String? = null

    @SerializedName("exclusive_badge_end")
    var exclusiveBadgeEnd: String? = null

    @SerializedName("exclusive_badge_type")
    var exclusiveBadgeType: String? = null

    @SerializedName("ep_master")
    var epMaster: String? = null

    @SerializedName("allow_app")
    var allowAppList: List<String>? = null

    @SerializedName("channel_code")
    var channelCode: String? = null

    @SerializedName("true_vision")
    var trueVision: String? = null

    @SerializedName("sub_ep_included")
    var subEpIncluded: String? = null

    @SerializedName("channel_info")
    var channelInfo: ChannelInfo? = null

    /**
     * TV
     */
    @SerializedName("subscriptionoff_requirelogin")
    var subscriptionOffRequireLogin: String? = null

    @SerializedName("subscription_package")
    var subscriptionPackage: List<String>? = null

    @SerializedName("is_premium")
    var isPremium: String? = null

    @SerializedName("drm")
    var drm: String? = null

    @SerializedName("digital_no")
    var digitalNo: String? = null

    @SerializedName("lang_dual")
    var langDual: String? = null

    @SerializedName("ads_webapp")
    var adsWebAppUrl: AdsWebAppUrl? = null

    @SerializedName("catch_up")
    var catchUp: Int? = null

    @SerializedName("allow_catchup")
    var allowCatchUpList: List<String>? = null

    @SerializedName("epg_flag")
    var epgFlag: Int? = null

    @SerializedName("slug")
    var slugChannel: String? = null

    @SerializedName("allow_chrome_cast")
    var allowChromeCast: String? = null

    @SerializedName("content_provider")
    var contentProvider: String? = null

    /**
     * Privilege
     */
    @SerializedName("redeem_point")
    var redeemPoint: String? = null

    @SerializedName("campaign_type")
    var campaignType: String? = null

    @SerializedName("card_type")
    var cardType: List<String?>? = null

    @SerializedName("schemaId")
    var schemaId: String? = null

    var isFollow: Boolean = false
    var isClickable: Boolean = false

    @SerializedName("preview_end")
    val previewEnd: String? = null

    @SerializedName("location")
    var location: String = ""

    @SerializedName("master_id")
    var masterId: String = ""

    @SerializedName("merchant_type")
    var merchantType: String = ""

    @SerializedName("relate_team")
    var relateTeam: List<String>? = null

    /**
     * Banner Countdown Today
     */
    @SerializedName("channel_items")
    var channelItems: List<ChannelItem>? = null

    @SerializedName("match_date")
    var matchDate: String = ""

    @SerializedName("match_status")
    var matchStatus: String = ""

    @SerializedName("league_code")
    var leagueCode: String = ""
}

class Info {
    @SerializedName("initials_name")
    var initialsName: String? = null

    @SerializedName("live_badge_en")
    var liveBadgeEn: String? = null

    @SerializedName("live_badge_th")
    var liveBadgeTh: String? = null

    @SerializedName("name_thai")
    var nameThai: String? = null

    @SerializedName("name_eng")
    var nameEng: String? = null

    @SerializedName("point_image_en")
    var pointImageEn: String? = null

    @SerializedName("point_image_th")
    var pointImageTh: String? = null

    @SerializedName("preview_image_en")
    var previewImageEn: String? = null

    @SerializedName("preview_image_th")
    var previewImageTh: String? = null

    @SerializedName("preview_url")
    var previewUrl: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("vote_image_en")
    var voteImageEn: String? = null

    @SerializedName("vote_image_th")
    var voteImageTh: String? = null

    @SerializedName("vote_url")
    var voteUrl: String? = null

    @SerializedName("channel_image")
    var channelImage: String? = null
}

class PartnerInfo {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("name_en")
    var nameEn: String? = null
}

class AdsWebAppUrl {
    @SerializedName("tidapp_listepg_0")
    var adsEpgUrl: String? = null
}
