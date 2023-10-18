package com.truedigital.component.widget.livecommerce.domain.model

import com.google.gson.annotations.SerializedName

data class CommerceShelfResponseItems(
    @SerializedName("display_country")
    var displayCountry: String? = null,
    @SerializedName("display_lang")
    var displayLang: String? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("content_type")
    var contentType: String? = null,
    @SerializedName("original_id")
    var originalId: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("article_category")
    var articleCategory: Any? = null,
    @SerializedName("thumb")
    var thumb: String? = null,
    @SerializedName("tags")
    var tags: List<String>? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("count_views")
    var countViews: String? = null,
    @SerializedName("publish_date")
    var publishDate: String? = null,
    @SerializedName("create_date")
    var createDate: String? = null,
    @SerializedName("update_date")
    var updateDate: String? = null,
    @SerializedName("searchable")
    var searchable: String? = null,
    @SerializedName("create_by")
    var createBy: String? = null,
    @SerializedName("create_by_ssoid")
    var createBySsoId: String? = null,
    @SerializedName("update_by")
    var updateBy: String? = null,
    @SerializedName("update_by_ssoid")
    var updateBySsoId: String? = null,
    @SerializedName("source_url")
    var sourceUrl: String? = null,
    @SerializedName("count_likes")
    var countLikes: String? = null,
    @SerializedName("count_ratings")
    var countRatings: String? = null,
    @SerializedName("source_country")
    var sourceCountry: String? = null,
    @SerializedName("setting")
    var setting: CommerceShelfResponseSetting? = null
)

data class CommerceShelfResponseSetting(
    @SerializedName("navigate")
    var navigate: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("component_name")
    var componentName: String? = null,
    @SerializedName("ssoid_list")
    var ssoIdList: String? = null
)
