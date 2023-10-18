package com.truedigital.common.share.data.coredata.data.model

import com.google.gson.annotations.SerializedName

data class RequestQuerySimilar(
    @SerializedName("query")
    var query: String? = null
)
