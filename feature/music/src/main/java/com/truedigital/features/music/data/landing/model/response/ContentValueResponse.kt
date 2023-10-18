package com.truedigital.features.music.data.landing.model.response

import com.google.gson.annotations.SerializedName
import com.truedigital.features.music.data.trending.model.response.playlist.Translation

data class ContentValueResponse(
    @SerializedName("Items")
    val items: List<ContentValueItem>? = null,
    @SerializedName("Title")
    val title: List<Translation>? = null
) {
    data class ContentValueItem(
        @SerializedName("Options")
        val options: ContentValueItemOptions? = null,
        @SerializedName("Text")
        val text: List<Translation>? = null,
        @SerializedName("Type")
        val type: String? = null
    ) {
        data class ContentValueItemOptions(
            @SerializedName("DisplayTitle")
            val displayTitle: Boolean? = null,
            @SerializedName("DisplayType")
            val displayType: String? = null,
            @SerializedName("DisplaySubType")
            val displaySubType: String? = null,
            @SerializedName("Format")
            val format: String? = null,
            @SerializedName("ListLimit")
            val listLimit: String? = null,
            @SerializedName("PlaylistId")
            val playlistId: String? = null,
            @SerializedName("Tag")
            val tag: String? = null,
            @SerializedName("TargetTime")
            val targetTime: Boolean? = null
        )
    }
}
