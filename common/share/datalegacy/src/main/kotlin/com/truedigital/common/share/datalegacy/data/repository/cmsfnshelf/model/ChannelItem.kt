package com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model

import com.google.gson.annotations.SerializedName

data class ChannelItem(
    @SerializedName("content_type")
    var contentType: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("slug")
    var slug: String = "",
    @SerializedName("title")
    var title: String = ""
)
