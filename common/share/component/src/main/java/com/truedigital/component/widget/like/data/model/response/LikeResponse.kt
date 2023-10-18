package com.truedigital.component.widget.like.data.model.response

import com.google.gson.annotations.SerializedName

class LikeResponse {
    companion object {
        const val CODE_SUCCESS = 10001
    }

    @SerializedName("code")
    var code: Int? = null

    @SerializedName("data")
    var data: LikeData? = null
}

class LikeData {
    @SerializedName("reaction")
    var reaction: String? = null

    @SerializedName("liked")
    var liked: Boolean? = null
}

fun LikeResponse.isLiked(): Boolean {
    return this.code == LikeResponse.CODE_SUCCESS &&
        this.data?.liked == true
}
