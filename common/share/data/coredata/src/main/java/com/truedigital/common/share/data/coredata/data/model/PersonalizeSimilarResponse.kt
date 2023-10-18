package com.truedigital.common.share.data.coredata.data.model

import com.google.gson.annotations.SerializedName

data class PersonalizeSimilarResponse(
    @SerializedName("data")
    var responseData: Data? = null
) {
    data class Data(
        @SerializedName("short")
        var short: List<SimilarResponse>? = null,
        @SerializedName("schemaId")
        var schemaId: String? = null
    )

    data class SimilarResponse(
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("title")
        var title: String? = null,
        @SerializedName("detail")
        var detail: String? = null,
        @SerializedName("tags")
        var tags: List<String>? = null,
        @SerializedName("status")
        var status: String? = null,
        @SerializedName("content_type")
        var contentType: String? = null,
        @SerializedName("publish_date")
        val publishDate: String? = null,
        @SerializedName("expire_date")
        var expireDate: String? = null,
        @SerializedName("navigate")
        var navigate: String? = null,
        @SerializedName("count_likes")
        val countLikes: Int? = null,
        @SerializedName("count_views")
        var countViews: Int? = null,
        @SerializedName("thumb_list")
        val thumbList: ThumbList? = null,
        @SerializedName("thumb")
        var thumb: String? = null,
        @SerializedName("create_by")
        val createBy: String? = null,
        @SerializedName("create_by_ssoid")
        val createBySsoid: String? = null,
        @SerializedName("article_category")
        val articleCategory: List<String>? = null,
        @SerializedName("genres")
        val genres: List<String>? = null,
        @SerializedName("setting")
        val setting: Setting? = null,
        @SerializedName("partner_related")
        val partnerRelated: List<PartnerRelated>? = null,
        @SerializedName("relate_content")
        val relateContent: List<PartnerRelated>? = null
    )

    data class PartnerRelated(
        @SerializedName("id")
        val id: String? = null,
        @SerializedName("title")
        val title: String? = null,
        @SerializedName("detail")
        val detail: String? = null,
        @SerializedName("content_type")
        val contentType: String? = null,
        @SerializedName("thumb")
        val thumb: String? = null,
        @SerializedName("thumb_list")
        val thumbList: ThumbList? = null,
        @SerializedName("navigate")
        val navigate: String? = null,
        @SerializedName("publish_date")
        val publishDate: String? = null,
        @SerializedName("expire_date")
        val expireDate: String? = null,
        @SerializedName("setting")
        val setting: Setting? = null
    )

    data class Setting(
        @SerializedName("auto_chat_display")
        val autoChatDisplay: String? = null,

        @SerializedName("dcr_crossid2")
        val dcrCrossid2: String? = null,

        @SerializedName("dcr_enable")
        val dcrEnable: String? = null,

        @SerializedName("dcr_live")
        val dcrLive: String? = null,

        @SerializedName("dcr_name")
        val dcrName: String? = null,

        val h1: String? = null,

        @SerializedName("h1_en")
        val h1En: String? = null,

        @SerializedName("h1_th")
        val h1Th: String? = null,

        @SerializedName("meta_description")
        val metaDescription: String? = null,

        @SerializedName("meta_title")
        val metaTitle: String? = null,

        @SerializedName("ott_genre")
        val ottGenre: String? = null,

        @SerializedName("ott_pwtm_iu")
        val ottPwtmIu: String? = null,

        @SerializedName("badge_color")
        val badgeColor: String? = null,

        @SerializedName("badge_text")
        val badgeText: String? = null,

        @SerializedName("cms_id")
        val cmsID: String? = null,

        @SerializedName("content_id")
        val contentID: String? = null,

        @SerializedName("dynamic_link")
        val dynamicLink: String? = null,

        @SerializedName("master_id")
        val masterID: String? = null,

        val navigate: String? = null,
        val slug: String? = null,

        @SerializedName("tag_en")
        val tagEn: String? = null,

        @SerializedName("tag_th")
        val tagTh: String? = null,

        @SerializedName("text_color")
        val textColor: String? = null,

        val trailer: String? = null,

        @SerializedName("youtube_link")
        val youtubeLink: String? = null,

        val partner: String? = null
    )

    data class ThumbList(
        @SerializedName("landscape_image")
        val landscapeImage: String? = null,

        @SerializedName("ott_landscape")
        val ottLandscape: String? = null,

        @SerializedName("ott_portrait")
        val ottPortrait: String? = null,

        @SerializedName("portrait_image")
        val portraitImage: String? = null,

        @SerializedName("poster")
        val poster: String? = null,

        @SerializedName("square_image")
        val squareImage: String? = null,

        @SerializedName("trueid_landscape")
        val trueidLandscape: String? = null
    )
}
