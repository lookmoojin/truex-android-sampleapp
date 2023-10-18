package com.truedigital.common.share.data.coredata.data.model.response

import com.google.gson.annotations.SerializedName

class ContentDetailResponse {
    @SerializedName("code")
    var code: Int? = 0

    @SerializedName("data")
    var data: ContentDetailData? = null
}
