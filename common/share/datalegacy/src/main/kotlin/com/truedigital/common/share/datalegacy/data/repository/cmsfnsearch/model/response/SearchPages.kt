package com.truedigital.common.share.datalegacy.data.repository.cmsfnsearch.model.response

import com.google.gson.annotations.SerializedName

class SearchPages {
    @SerializedName("cursor")
    var cursor: String? = null

    @SerializedName("total_items")
    var totalItems: Int? = null

    @SerializedName("total_pages")
    var totalPages: Int? = null
}
