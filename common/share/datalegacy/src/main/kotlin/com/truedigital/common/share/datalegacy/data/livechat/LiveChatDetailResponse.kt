package com.truedigital.common.share.datalegacy.data.livechat

import com.google.gson.annotations.SerializedName

class LiveChatDetailResponse {
    @SerializedName("code")
    var code: Int? = null

    @SerializedName("data")
    var data: LiveChatDetailData? = null
}
