package com.truedigital.common.share.datalegacy.data.repository.cmsfnsearch.model.response

import com.google.gson.annotations.SerializedName

class SearchSetting {

    @SerializedName("shelf_code")
    var shelfCode: String? = null

    @SerializedName("title_en")
    var titleEn: String? = null

    @SerializedName("title_th")
    var titleTh: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("day")
    var day: String? = null

    @SerializedName("end")
    var end: String? = null

    @SerializedName("start")
    var start: String? = null

    @SerializedName("ads_tags_url")
    var adsTagUrl: String? = null

    @SerializedName("mobile_size")
    var mobileSize: String? = null

    @SerializedName("tablet_size")
    var tabletSize: String? = null
}
