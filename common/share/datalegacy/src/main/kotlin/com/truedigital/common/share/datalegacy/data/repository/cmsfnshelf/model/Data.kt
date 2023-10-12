package com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model

import com.google.gson.annotations.SerializedName
import com.truedigital.common.share.datalegacy.data.repository.cmsfnsearch.model.response.ThumbList

class Data {

    @SerializedName("id")
    var id: String? = null

    @SerializedName("detail")
    var detail: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("create_date")
    var createDate: String? = null

    @SerializedName("shelf_items")
    var shelfList: List<Shelf>? = null

    @SerializedName("publish_date")
    var publishDate: String? = null

    @SerializedName("update_date")
    var updateDate: String? = null

    @SerializedName("setting")
    var setting: Setting? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("thumb")
    var thumb: String? = null

    @SerializedName("source_url")
    var sourceUrl: String? = null

    @SerializedName("start_date")
    var startDate: String? = null

    @SerializedName("end_date")
    var endDate: String? = null

    @SerializedName("schemaId")
    var schemaId: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("view_type")
    var viewType: String? = null

    @SerializedName("content_type")
    var contentType: String? = null

    @SerializedName("count_views")
    var countViews: Int? = null

    @SerializedName("movie_type")
    var movieType: String? = null

    @SerializedName("other_type")
    var otherType: String? = null

    @SerializedName("thumb_list")
    var thumbList: ThumbList? = null
}
