package com.truedigital.features.music.data.landing.model.response.tagname

import com.google.gson.annotations.SerializedName
import com.truedigital.features.music.data.trending.model.response.playlist.Translation

class TagNameResponse {
    @SerializedName("DisplayName")
    var displayName: List<Translation>? = null
}
