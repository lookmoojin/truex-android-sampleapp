package com.truedigital.common.share.datalegacy.data.similar

import com.google.gson.annotations.SerializedName

data class SimilarResponse(
    @SerializedName("items")
    var items: List<SimilarItem>? = mutableListOf(),

    @SerializedName("message")
    var message: String? = "",

    @SerializedName("schemaId")
    var schemaId: String? = ""
)

data class SimilarItem(
    @SerializedName("actors")
    var actors: List<String>? = listOf(),

    @SerializedName("article_category")
    var articleCategory: List<String>? = listOf(),

    @SerializedName("audio")
    var audio: List<String>? = listOf(),

    @SerializedName("content_rights")
    var contentRights: String? = "",

    @SerializedName("content_type")
    var contentType: String? = "",

    @SerializedName("count_likes")
    var countLikes: Int? = 0,

    @SerializedName("count_views")
    var countViews: Int? = 0,

    @SerializedName("desc")
    var desc: String? = "",

    @SerializedName("display_qualities")
    var displayQualities: Any? = Any(),

    @SerializedName("drm")
    var drm: String? = "",

    @SerializedName("duration")
    var duration: String? = "",

    @SerializedName("ep_items")
    var epItems: List<String>? = listOf(),

    @SerializedName("exclusive_badge_end")
    var exclusiveBadgeEnd: String? = "",

    @SerializedName("exclusive_badge_start")
    var exclusiveBadgeStart: String? = "",

    @SerializedName("exclusive_badge_type")
    var exclusiveBadgeType: String? = "",

    @SerializedName("genres")
    var genres: List<String>? = listOf(),

    @SerializedName("globalItemId")
    var globalItemId: String? = "",

    @SerializedName("hitcountMonth")
    var hitcountMonth: Int? = 0,

    @SerializedName("hitcountWeek")
    var hitcountWeek: Int? = 0,

    @SerializedName("hitcountYear")
    var hitcountYear: Int? = 0,

    @SerializedName("movie_type")
    var movieType: String? = "",

    @SerializedName("name")
    var name: String? = "",

    @SerializedName("newepi_badge_end")
    var newepiBadgeEnd: String? = "",

    @SerializedName("newepi_badge_start")
    var newepiBadgeStart: String? = "",

    @SerializedName("newepi_badge_type")
    var newepiBadgeType: String? = "",

    @SerializedName("other_type")
    var otherType: String? = "",

    @SerializedName("package_alacarte")
    var packageAlacarte: Any? = Any(),

    @SerializedName("partner_related")
    var partnerRelated: List<String>? = listOf(),

    @SerializedName("rate")
    var rate: String? = "",

    @SerializedName("releaseYear")
    var releaseYear: String? = "",

    @SerializedName("sub_genres")
    var subGenres: List<String>? = listOf(),

    @SerializedName("subscription_tiers")
    var subscriptionTiers: List<String>? = listOf(),

    @SerializedName("subtitle")
    var subtitle: Any? = Any(),

    @SerializedName("synopsis")
    var synopsis: String? = "",

    @SerializedName("thumb_list")
    var thumbList: ThumbList? = ThumbList(),

    @SerializedName("thumbnail")
    var thumbnail: String? = "",

    @SerializedName("trailer")
    var trailer: String? = "",

    @SerializedName("tvod_flag")
    var tvodFlag: String? = ""
)

data class ThumbList(
    @SerializedName("landscape_image")
    var landscapeImage: String? = "",

    @SerializedName("ott_landscape")
    var ottLandscape: String? = "",

    @SerializedName("ott_portrait")
    var ottPortrait: String? = "",

    @SerializedName("portrait_image")
    var portraitImage: String? = "",

    @SerializedName("poster")
    var poster: String? = "",

    @SerializedName("square_image")
    var squareImage: String? = "",

    @SerializedName("trueid_landscape")
    var trueidLandscape: String? = ""
)
