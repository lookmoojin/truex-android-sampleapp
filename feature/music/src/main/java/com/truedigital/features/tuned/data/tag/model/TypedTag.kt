package com.truedigital.features.tuned.data.tag.model

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.util.LocalisedString

data class TypedTag(
    @SerializedName("TagName") var name: String,
    @SerializedName("DisplayNames") var displayName: List<LocalisedString>
)
