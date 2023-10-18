package com.truedigital.component.widget.like.data.model.request

import com.google.gson.annotations.SerializedName

class LikeDataRequest {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("access_token")
    var accessToken: String? = null
    @SerializedName("emocode")
    var emocode: String? = null
    @SerializedName("reaction")
    var reaction: String? = null
}
