package com.truedigital.common.share.datalegacy.data.recommend.model.response

import com.google.gson.annotations.SerializedName

class RecommendedResponse {

    @SerializedName("items")
    var items: List<RecommendedItems> = mutableListOf()

    @SerializedName("message")
    var message: String? = ""

    @SerializedName("schemaId")
    var schemaId: String? = ""
}

class RecommendedItems {
    @SerializedName("actorsList")
    var actorsList: List<String>? = null

    @SerializedName("desc")
    var desc: String? = null

    @SerializedName("globalItemId")
    var globalItemId: String? = null

    @SerializedName("hitcountMonth")
    var countMonth: Int? = null

    @SerializedName("hitcountWeek")
    var countWeek: Int? = null

    @SerializedName("count_views")
    var countView: Int? = null

    @SerializedName("count_likes")
    var countLike: Int? = null

    @SerializedName("movie_type")
    var movieType: String? = null

    @SerializedName("other_type")
    var otherType: String? = null

    @SerializedName("genres")
    var geners: List<String>? = null

    @SerializedName("article_category")
    var articleCategory: List<String>? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("releaseYear")
    var releaseYear: String? = null

    @SerializedName("thumbnail")
    var thumbnail: String? = null

    @SerializedName("ep_items")
    val epList: List<String>? = mutableListOf()

    @SerializedName("tvod_flag")
    var tvodFlag: String? = null

    @SerializedName("thumb_list")
    var thumbList: ThumbList? = null

    @SerializedName("subscription_tiers")
    val subscriptionTiers: List<String>? = null

    @SerializedName("content_rights")
    val contentRight: String? = null

    @SerializedName("content_type")
    val contentType: String? = null

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
}

class ThumbList {
    @SerializedName("landscape_image")
    var landscapeImage: String? = null

    @SerializedName("portrait_image")
    var portraitImage: String? = null

    @SerializedName("poster")
    var poster: String? = null
}
