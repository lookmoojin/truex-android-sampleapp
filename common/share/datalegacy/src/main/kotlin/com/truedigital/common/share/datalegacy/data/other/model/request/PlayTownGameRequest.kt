package com.truedigital.common.share.datalegacy.data.other.model.request

import com.google.gson.annotations.SerializedName

class PlayTownGameRequest {

    @SerializedName("clientid")
    var clientId: String = ""

    @SerializedName("clientsecret")
    var clientSecret: String = ""
}
