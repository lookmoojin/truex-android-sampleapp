package com.truedigital.common.share.datalegacy.data.topcontent.model.response

import com.google.gson.annotations.SerializedName
import com.truedigital.common.share.datalegacy.data.repository.cmsfnsearch.model.response.SearchData

class TopContentResponse {
    @SerializedName("code")
    var code: Int? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("total")
    var total: Int? = null

    @SerializedName("limit")
    var limit: Int? = null

    @SerializedName("data")
    var topSearchDataList: List<SearchData>? = null

    @SerializedName("nextPage")
    var nextPage: String? = null
}
