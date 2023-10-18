package com.truedigital.features.tuned.data.user.model

import com.google.gson.annotations.SerializedName
import java.util.Objects

data class ContentLanguage(
    @SerializedName("Code") var code: String,
    @SerializedName("LocalDisplayName") var localDisplayName: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ContentLanguage?
        return this.code.equals(that?.code, true)
    }

    override fun hashCode() = Objects.hash(code.uppercase(), localDisplayName)

    override fun toString() = String.format(
        "ContentLanguage(code=%s, localDisplayName=%s",
        code.uppercase(), localDisplayName
    )
}
