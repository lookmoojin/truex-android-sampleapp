package com.truedigital.common.share.datalegacy.data.repository.profile.model.response

import com.google.gson.annotations.SerializedName

class ProfileSettingsResponse {
    @SerializedName("code")
    var code: Int? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ProfileSettingsData? = null
}

class ProfileSettingsData {
    @SerializedName("id")
    var id: String? = null
}
