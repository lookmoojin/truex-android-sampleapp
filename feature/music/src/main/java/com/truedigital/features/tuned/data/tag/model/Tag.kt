package com.truedigital.features.tuned.data.tag.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.util.LocalisedString

data class Tag(
    @SerializedName("TagId") override var id: Int,
    @SerializedName("Name") var name: String,
    @SerializedName("Image") var image: String?,
    @SerializedName("Images") var images: List<LocalisedString>?,
    @SerializedName(
        "DisplayName",
        alternate = ["DisplayNames"]
    ) var displayName: List<LocalisedString>
) : Product {
    companion object {
        fun fromString(str: String): Tag =
            Gson().fromJson<Tag>(str, Tag::class.java)
    }

    override fun toString(): String =
        Gson().toJson(this)
}
