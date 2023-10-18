package com.truedigital.common.share.datalegacy.data.repository.cmsfnsearch.model.response

import com.google.gson.annotations.SerializedName
import com.truedigital.common.share.datalegacy.data.other.model.response.tv.ChannelInfo
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.ChannelItem

class SearchData {

    @SerializedName("allow_app")
    var allowAppList: List<String>? = null

    @SerializedName("channel_info")
    var channelInfo: ChannelInfo? = null

    @SerializedName("digital_no")
    var digitalNo: String? = null

    @SerializedName("lang")
    var lang: String? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("drm")
    var drm: String? = null

    @SerializedName("thumb")
    var thumb: String? = null

    @SerializedName("original_id")
    var originalId: String? = null

    @SerializedName("content_type")
    var contentType: String? = null

    @SerializedName("movie_type")
    var movieType: String? = null

    @SerializedName("count_likes")
    var countLikes: Int? = null

    @SerializedName("count_views")
    var countViews: Int? = null

    @SerializedName("business_models")
    var businessModels: String? = null

    @SerializedName("subscription_tiers")
    var subscriptionTiers: List<String>? = null

    @SerializedName("actor")
    var actor: List<String>? = null

    @SerializedName("allow_chrome_cast")
    var allowChromeCast: String? = null

    @SerializedName("singer_name")
    var singerName: String? = null

    @SerializedName("setting")
    var setting: SearchSetting? = null

    @SerializedName("thumb_list")
    var thumbList: ThumbList? = null

    @SerializedName("publish_date")
    var publishDate: String? = null

    @SerializedName("package_code")
    var packageCode: String? = null

    @SerializedName("league_code")
    var leagueCode: String? = null

    @SerializedName("tv_show_code")
    var tvShowCode: String? = null

    @SerializedName("article_category")
    var articleCategory: List<String>? = null

    @SerializedName("genres")
    var genres: List<String>? = null

    @SerializedName("tvod_flag")
    var tvodFlag: String? = ""

    @SerializedName("tags")
    var tagList: List<String>? = null

    @SerializedName("ep_items")
    var episodeList: List<String>? = null

    @SerializedName("is_trailer")
    var isTrailer: String? = null

    @SerializedName("channel_code")
    var channelCode: String? = ""

    @SerializedName("channel_items")
    var channelItems: List<ChannelItem>? = null

    @SerializedName("channel_name")
    var channelName: String? = ""

    @SerializedName("embed")
    var embed: String? = ""

    @SerializedName("duration")
    var duration: String? = ""

    @SerializedName("nextPage")
    var nextPage: String? = null

    @SerializedName("total")
    var total: String? = null

    @SerializedName("info")
    var info: InfoData? = null

    @SerializedName("ep_master")
    var epMaster: String? = null

    @SerializedName("expire_date")
    var expireDate: String? = null

    @SerializedName("article_category_detail")
    var articleCategoryDetail: List<String>? = null

    @SerializedName("redeem_point")
    var redeemPoint: String? = null

    @SerializedName("campaign_code")
    var campaignCode: String? = null

    @SerializedName("campaign_type")
    var campaignType: String? = null

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

    @SerializedName("other_type")
    var otherType: String? = null

    @SerializedName("preview_end")
    var previewEnd: String? = null

    @SerializedName("sub_ep_included")
    var subEpIncluded: String? = null

    @SerializedName("slug")
    var slug: String? = null
}

class ThumbList {
    @SerializedName("landscape_image")
    var landscapeImage: String? = null

    @SerializedName("poster")
    var poster: String? = null

    @SerializedName("poster_background_template_url")
    var posterBackgroundTemplateUrl: String? = null

    @SerializedName("trueid_landscape")
    var trueidLandscape: String? = null

    @SerializedName("square_image")
    var squareImage: String? = null

    @SerializedName("banner")
    var banner: String? = null

    @SerializedName("highlight16x9")
    var highlight16x9: String? = null

    @SerializedName("thumbnail")
    var thumbnail: String? = null

    @SerializedName("image_coupon")
    var imageCoupon: String? = null

    @SerializedName("icon")
    var icon: String? = null

    @SerializedName("icon_button")
    var iconButton: String? = null

    @SerializedName("thumb_app")
    val thumbApp: String? = null

    @SerializedName("sponsorship_clip_player")
    var sponsorshipClip: String? = null

    @SerializedName("banner_large_en")
    var bannerLargeEn: String? = null

    @SerializedName("banner_large_th")
    var bannerLargeTh: String? = null

    @SerializedName("banner_small_en")
    var bannerSmallEn: String? = null

    @SerializedName("banner_small_th")
    var bannerSmallTh: String? = null

    @SerializedName("background_image")
    var backgroundImage: String? = null

    @SerializedName("image_1")
    val imageOne: String? = null

    @SerializedName("image_2")
    val imageTwo: String? = null

    @SerializedName("logo")
    val logo: String? = null

    @SerializedName("logo_s")
    var logoS: String? = null

    @SerializedName("thumb_en")
    var thumbEn: String? = null

    @SerializedName("thumb_th")
    var thumbTh: String? = null

    @SerializedName("thumb_live_en")
    var thumbLiveEn: String? = null

    @SerializedName("thumb_live_th")
    var thumbLiveTh: String? = null

    @SerializedName("header_backgroud")
    var headerBackground: String? = null

    @SerializedName("header_icon")
    var headerIcon: String? = null
}
