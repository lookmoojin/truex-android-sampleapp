package com.truedigital.features.music.data.search.model.response

import com.google.gson.annotations.SerializedName
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.trending.model.response.playlist.Translation

data class Source(
    @SerializedName("artists")
    val artists: List<Artist>? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("images")
    val images: List<Image>? = null,
    @SerializedName("meta")
    val meta: List<Meta>? = null,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("translations")
    var translationsList: List<Translation> = emptyList()
) {

    val nameTranslations: String
        get() = this.translationsList.find {
            it.language == com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH
        }?.value?.ifEmpty { this.name } ?: this.name
}
