package com.truedigital.common.share.datalegacy.data.repository.cmsfnsearch.model.response

import com.google.gson.annotations.SerializedName

class InfoData {
    @SerializedName("button_name_en")
    var buttonNameEn: String? = ""

    @SerializedName("button_name_th")
    var buttonNameTh: String? = ""

    @SerializedName("description_en")
    var descriptionEn: String? = ""

    @SerializedName("description_th")
    var descriptionTh: String? = ""

    @SerializedName("name_en")
    var nameEn: String? = ""

    @SerializedName("name_th")
    var nameTh: String? = ""

    @SerializedName("thumb")
    var thumb: String? = null

    @SerializedName("thumbnail")
    var thumbnail: String? = null

    @SerializedName("title_en")
    var titleEn: String? = null

    @SerializedName("title_th")
    var titleTh: String? = null
}
