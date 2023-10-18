package com.truedigital.common.share.componentv3.widget.searchanimation.model

import com.google.gson.annotations.SerializedName

class SearchAnimationData {

    @SerializedName("ads_url")
    var adsUrl: String = ""

    @SerializedName("deeplink")
    var deeplink: String = ""

    val searchAnimationTime: SearchAnimationTime = SearchAnimationTime()
}

class SearchAnimationTime {

    @SerializedName("start_date")
    var startDate: String = ""

    @SerializedName("end_date")
    var endDate: String = ""
}
