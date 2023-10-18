package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model

import com.google.gson.annotations.SerializedName

data class WeMallParametersModel(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("content_type")
    val contentType: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("thumb")
    val thumb: String = "",
    @SerializedName("setting")
    val setting: WeMallParametersSettingModel? = null
)

data class WeMallParametersSettingModel(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("component_name")
    val componentName: String = "",
    @SerializedName("limit")
    val limit: String = "",
    @SerializedName("seemore")
    val seeMore: String = "",
    @SerializedName("theme")
    val theme: String = ""
)
