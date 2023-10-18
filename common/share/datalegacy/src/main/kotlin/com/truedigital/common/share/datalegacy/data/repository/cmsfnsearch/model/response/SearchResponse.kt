package com.truedigital.common.share.datalegacy.data.repository.cmsfnsearch.model.response

import com.google.gson.annotations.SerializedName

class SearchResponse {

    @SerializedName("code")
    var code: Int? = null

    @SerializedName("lang")
    var lang: String? = null

    @SerializedName("data")
    var data: List<SearchData>? = null

    @SerializedName("nextPage")
    var nextPage: String? = null

    @SerializedName("total")
    var total: String? = null

    @SerializedName("pages")
    var pages: SearchPages? = null
}
