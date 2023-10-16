package com.truedigital.common.share.data.coredata.data.model.response

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

class ContentDetailData {
    @SerializedName("article_category")
    var articleCategoryList: List<String>? = null

    @SerializedName("article_category_detail")
    var articleCategoryDetailList: List<ArticleCategoryDetail>? = null

    @SerializedName("chapter_items")
    var chapterItemsList: List<String>? = null

    @SerializedName("content_type")
    var contentType: String? = null

    @SerializedName("count_likes")
    val countLikes: Int = 0

    @SerializedName("count_views")
    val countViews: Int = 0

    @SerializedName("create_by")
    var createBy: String? = null

    @SerializedName("create_by_ssoid")
    var createBySsoId: String? = null

    @SerializedName("detail")
    var detail: String? = null

    @SerializedName("ep_items")
    var epItemsList: List<ContentDetailData>? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("lang")
    val language: String? = null

    @SerializedName("original_id")
    var originalId: String? = null

    @SerializedName("partner_info")
    var partnerInfo: PartnerInfo? = null

    @SerializedName("partner_related")
    var partnerRelatedList: List<PartnerRelated>? = null

    @SerializedName("publish_date")
    var publishDate: String? = null

    @SerializedName("related")
    var relatedList: List<Related>? = null

    @SerializedName("setting")
    var setting: JsonElement? = null

    @SerializedName("share_url")
    val shareUrl: String? = null

    @SerializedName("subscription_tiers")
    var subscriptionTiers: List<String>? = null

    @SerializedName("synopsis")
    var synopsis: String? = null

    @SerializedName("tags")
    var tagsList: List<String>? = null

    @SerializedName("thumb")
    var thumb: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("trailer")
    var trailer: String? = null

    @SerializedName("true_vision")
    var trueVision: String? = null

    class PartnerInfo {
        @SerializedName("id")
        var partnerId: String? = null

        @SerializedName("name_en")
        var nameEn: String? = null

        @SerializedName("name_th")
        var nameTh: String? = null

        @SerializedName("thumb")
        val thumb: String? = null
    }

    class PartnerRelated {
        @SerializedName("setting")
        var settingPartner: JsonElement? = null

        @SerializedName("id")
        var id: String? = null

        @SerializedName("title")
        var title: String? = null

        @SerializedName("thumb")
        var thumb: String? = null

        @SerializedName("content_type")
        var contentType: String? = null
    }

    class Related {

        @SerializedName("id")
        var id: String? = null

        @SerializedName("title")
        var title: String? = null

        @SerializedName("thumb")
        var thumb: String? = null

        @SerializedName("source_url")
        var sourceUrl: String? = null
    }

    class ArticleCategoryDetail {

        @SerializedName("name")
        var name: String? = null
        @SerializedName("slug")
        var slug: String? = null
        @SerializedName("sub_category")
        var subCategory: List<ArticleCategoryDetail>? = null
    }
}
