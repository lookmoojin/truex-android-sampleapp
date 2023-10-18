package com.truedigital.core.data

import com.google.gson.annotations.SerializedName

data class CommonShelfModelResponse(
    var viewType: String = "",
    var header: CommonHeaderFooterModel? = null,
    var footer: CommonHeaderFooterModel? = null,
    var theme: String = "",
    var shelfCode: String = "",
    var shelfId: String = "",
    var shelfName: String = "",
    var items: MutableList<CommonShelfItemModelResponse>? = null,
    var vType: String = "",
    var data: CommonHeaderFooterDataModel? = null,
    var navigate: String = "",
    @SerializedName("check_segment")
    var checkSegment: String = "",
    @SerializedName("placement_id")
    var placementId: String = "",
    @SerializedName("type_call_api")
    var typeCallApi: String = "",
    @SerializedName("gradient_color_start")
    var gradientStart: String = "",
    @SerializedName("gradient_color_end")
    var gradientEnd: String = "",
    @SerializedName("shelf_custom_type")
    var shelfCustomType: String = ""
)

data class CommonHeaderFooterModel(
    var viewType: String = "",
    var data: CommonHeaderFooterDataModel? = null,
    var navigate: String = "",
    var vType: String = "",
    var theme: String = "",
    var shelfCode: String = ""
)

data class CommonShelfItemModelResponse(
    var viewType: String = "",
    var header: CommonHeaderFooterModel? = null,
    var data: CommonShelfItemDataModel? = null,
    var footer: CommonHeaderFooterModel? = null,
    var navigate: String = ""
)

data class CommonShelfItemDataModel(
    var id: String = "",
    var title: String = "",
    @SerializedName("content_type")
    var contentType: String = "",
    var thumb: String = "",
    var detail: String = "",
    @SerializedName("synopsis")
    var synopsis: String = "",

    // Article
    var lang: String = "",
    @SerializedName("article_category")
    var articleCategory: List<String>? = null,
    @SerializedName("article_category_detail")
    var articleCategoryDetail: MutableList<CommonDataArticleCategoryDetail>? = null,
    var tags: List<String>? = null,
    var status: String = "",
    @SerializedName("publish_date")
    var publishDate: String = "",

    // Movie
    @SerializedName("original_id")
    var originalId: String = "",
    @SerializedName("subscription_tiers")
    var subscriptionTiers: List<String>? = null,
    @SerializedName("exclusive_badge_end")
    var exclusiveBadgeEnd: String = "",
    @SerializedName("exclusive_badge_start")
    var exclusiveBadgeStart: String = "",
    @SerializedName("exclusive_badge_type")
    var exclusiveBadgeType: String = "",
    @SerializedName("content_rights")
    var contentRights: String = "",
    @SerializedName("newepi_badge_start")
    var newepiBadgeStart: String = "",
    @SerializedName("newepi_badge_end")
    var newepiBadgeEnd: String = "",
    @SerializedName("newepi_badge_type")
    var newepiBadgeType: String = "",
    @SerializedName("movie_type")
    var movieType: String = "",
    var genres: List<String>? = null,
    @SerializedName("thumb_list")
    var thumbList: ThumbList? = null,
    @SerializedName("other_type")
    var otherType: String = "",
    @SerializedName("title_logo")
    var titleLogo: String = "",
    // TV
    @SerializedName("api_url")
    var apiUrl: String = "",
    @SerializedName("channel_name")
    var channelName: String = "",
    @SerializedName("content_id")
    var contentId: String = "",
    @SerializedName("expired_date")
    var expiredDate: String = "",
    @SerializedName("epg_title")
    var epgTitle: String = "",
    @SerializedName("epg_thumb")
    var epgThumb: String = "",
    var order: String = "",
    var recommend: String = "",
    var slug: String = "",
    @SerializedName("start_date")
    var startDate: String = "",
    @SerializedName("stream_info")
    var streamInfo: CommonDataStreamInfoModel? = null,
    @SerializedName("total_ccu")
    var totalCcu: Int = 0,
    @SerializedName("channel_code")
    var channelCode: String = "",
    // Deals
    @SerializedName("redeem_point")
    var redeemPoint: String = "",
    var price: String = "",
    @SerializedName("howto")
    var howTo: String = "",
    @SerializedName("expire_date")
    var expireDate: String = "",
    @SerializedName("quota_over_existed")
    var quotaOverExisted: Int = 0,

    // Component
    var setting: CommonDataSettingModel? = null
)

data class ThumbList(
    @SerializedName("square_image")
    var squareImage: String = "",
    @SerializedName("poster")
    var poster: String = "",
    @SerializedName("landscape_image")
    var landscapeImage: String = "",
    @SerializedName("image")
    var image: String = "",
    @SerializedName("trueid_landscape")
    var trueIdLandscape: String = ""
)

data class CommonDataSettingModel(
    @SerializedName("component_name")
    var componentName: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("random")
    var random: String = "",
    @SerializedName("ads_id")
    var adsId: String = "",
    @SerializedName("shelf_id")
    var shelfId: String = "",
    @SerializedName("shelf_code")
    var shelfCode: String = "",
    var experiment: String = "",
    var key: String = "",
    var value: String = "",

    @SerializedName("placement_id")
    var placementId: String = "",
    @SerializedName("placement_id_feed")
    var placementIdFeed: String = "",

    var size: String = "",
    @SerializedName("size_tablet")
    var sizeTablet: String = "",
    var slug: String = "",
    var category: String = "",
    var limit: String? = null,
    var seemore: String = "",
    @SerializedName("theme")
    var theme: String = "",

    @SerializedName("dynamic_link")
    var dynamicLink: String = "",
    @SerializedName("trueid_plus_logo")
    var trueIdPlusLogo: String = "",

    @SerializedName("header_icon")
    var headerIcon: String = "",
    @SerializedName("header_navigate")
    var headerNavigate: String = "",
    @SerializedName("header_title")
    var headerTitle: String = "",
    @SerializedName("seemore_icon")
    var seeMoreIcon: String? = null,
    @SerializedName("seemore_navigate")
    var seeMoreNavigate: String? = null,
    @SerializedName("seemore_title")
    var seeMoreTitle: String? = null,

    @SerializedName("caption")
    var caption: String = "",
    @SerializedName("caption_th")
    var captionTh: String = "",
    @SerializedName("caption_en")
    var captionEn: String = "",
    @SerializedName("caption_id")
    var captionId: String = "",
    @SerializedName("caption_fil")
    var captionFil: String = "",
    @SerializedName("caption_my")
    var captionMy: String = "",
    @SerializedName("caption_km")
    var captionKm: String = "",
    @SerializedName("caption_vi")
    var captionVi: String = "",

    @SerializedName("caption_detail")
    var captionDetail: String = "",
    @SerializedName("caption_detail_th")
    var captionDetailTh: String = "",
    @SerializedName("caption_detail_en")
    var captionDetailEn: String = "",
    @SerializedName("caption_detail_id")
    var captionDetailId: String = "",
    @SerializedName("caption_detail_fil")
    var captionDetailFil: String = "",
    @SerializedName("caption_detail_my")
    var captionDetailMy: String = "",
    @SerializedName("caption_detail_km")
    var captionDetailKm: String = "",
    @SerializedName("caption_detail_vi")
    var captionDetailVi: String = "",

    @SerializedName("community_id")
    var communityId: String = "",

    @SerializedName("navigate")
    var navigate: String = "",

    @SerializedName("title")
    var title: String = "",

    @SerializedName("type")
    var type: String = "",

    @SerializedName("subtitle")
    var titleLine2: String = "",

    @SerializedName("button_text")
    var textButton: String = "",

    @SerializedName("bg_image")
    var backgroundImage: String = "",

    @SerializedName("background_color")
    var backgroundColor: String = "",

    @SerializedName("create_room_title")
    var createRoomTitle: String = "",

    @SerializedName("create_room_subtitle")
    var createRoomSubtitle: String = "",

    @SerializedName("create_room_button_text")
    var createRoomButtonText: String = "",

    @SerializedName("create_room_bg_image")
    var createRoomBgImage: String? = null,

    @SerializedName("create_room_navigate")
    var createRoomNavigate: String = "",

    @SerializedName("title_image")
    var titleImage: String = "",

    @SerializedName("product_code")
    var productCode: String = "",
    @SerializedName("league_code")
    var leagueCode: String = "",
    @SerializedName("sort_by_team_ids")
    var sortByTeamIds: String = "",

    // Check User Segment
    @SerializedName("check_login")
    var checkLogin: String = "",
    @SerializedName("truemoveh")
    var truemoveh: String = "",
    @SerializedName("truescore")
    var trueScore: String = "",
    @SerializedName("truecard_type")
    var trueCardType: String = "",
    @SerializedName("min_version")
    var minVersion: String = "",
    @SerializedName("max_version")
    var maxVersion: String = "",
    @SerializedName("platform")
    var platform: String = "",
    @SerializedName("sub_include_package_code")
    var includePackageCode: String = "",
    @SerializedName("sub_exclude_package_code")
    var excludePackageCode: String = "",
    @SerializedName("sub_include_group_code")
    var includeGroupCode: String = "",
    @SerializedName("sub_exclude_group_code")
    var excludeGroupCode: String = "",
    @SerializedName("schedule_limit")
    var scheduleLimit: String = "",
    @SerializedName("shelf_league_id")
    var shelfLeagueId: String = "",
    @SerializedName("min_truepoint")
    val minTruePoint: String = "",
    @SerializedName("sub_include_product_group_code")
    val includeProductGroupCode: String = "",
    @SerializedName("sub_exclude_product_group_code")
    val excludeProductGroupCode: String = ""
)

data class CommonDataArticleCategoryDetail(
    @SerializedName("slug")
    var slug: String = "",
    @SerializedName("name")
    var name: String = ""
)

data class CommonDataStreamInfoModel(
    @SerializedName("allow_chrome_cast")
    var allowChromeCast: String = "",
    @SerializedName("back_out")
    var backOut: String = "",
    @SerializedName("backout_end_date")
    var backOutEndDate: String = "",
    @SerializedName("backout_message")
    var backOutMessage: String = "",
    @SerializedName("backout_start_date")
    var backOutStartDate: String = "",
    var drm: String = "",
    @SerializedName("geo_block")
    var geoBlock: String = "",
    @SerializedName("is_premium")
    var isPremium: String = "",
    @SerializedName("subscriptionoff_requirelogin")
    var subscriptionOffRequireLogin: String = ""
)

data class CommonHeaderFooterDataModel(
    var background: HeaderBackground? = null,
    var description: String = "",
    @SerializedName("footer_badge")
    var footerBadge: String = "",
    @SerializedName("footer_hight")
    var footerHeight: String = "",
    @SerializedName("header_badge")
    var headerBadge: String = "",
    @SerializedName("header_hight")
    var headerHeight: String = "",
    var title: String = "",
    var thumb: String = ""
)

data class HeaderBackground(
    var mobile: String = "",
    var tablet: String = "",
    @SerializedName("color_start")
    var colorStart: String = "",
    @SerializedName("color_end")
    var colorEnd: String = ""
)
