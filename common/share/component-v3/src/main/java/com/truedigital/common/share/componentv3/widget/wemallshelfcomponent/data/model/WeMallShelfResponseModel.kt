package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model

import com.google.gson.annotations.SerializedName

data class WeMallShelfResponseModel(
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("data")
    val data: List<WeMallShelfResponseData>? = null
)

data class WeMallShelfResponseData(
    @SerializedName("category_name")
    val categoryName: String? = null,
    @SerializedName("category_url")
    val categoryUrl: String? = null,
    @SerializedName("limit")
    val limit: String? = null,
    @SerializedName("items")
    val items: List<WeMallShelfResponseItems>? = null
)

data class WeMallShelfResponseItems(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("item_url")
    val itemUrl: String? = null,
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("prices")
    val prices: List<String>? = null
)
