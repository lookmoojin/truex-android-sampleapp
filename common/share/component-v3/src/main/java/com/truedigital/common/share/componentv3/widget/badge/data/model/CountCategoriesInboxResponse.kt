package com.truedigital.common.share.componentv3.widget.badge.data.model

import com.google.gson.annotations.SerializedName

data class CountCategoriesInboxResponse(
    @SerializedName("code")
    var code: Int? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("data")
    var data: Data? = null
)

data class Data(
    @SerializedName("total_unseens")
    var totalUnseens: Int = 0,
    @SerializedName("details")
    var details: List<Detail>? = null
)

data class Detail(
    @SerializedName("category")
    var category: String? = null,
    @SerializedName("unseen")
    var unseen: Int = 0
)
