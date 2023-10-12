package com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model

import com.google.gson.annotations.SerializedName

class Pages {

    @SerializedName("page")
    var page: Int? = null

    @SerializedName("limit")
    var limit: Int? = null

    @SerializedName("total_items")
    var totalItems: Int? = null

    @SerializedName("total_pages")
    var totalPages: Int? = null
}
