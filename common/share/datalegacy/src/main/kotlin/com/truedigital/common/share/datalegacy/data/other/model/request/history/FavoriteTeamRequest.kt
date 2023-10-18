package com.truedigital.common.share.datalegacy.data.other.model.request.history

import com.google.gson.annotations.SerializedName

open class FavoriteTeamRequest {

    @SerializedName("appid")
    var appId: String = ""

    @SerializedName("refid")
    var refId: String = ""

    @SerializedName("action_type")
    var actionType: String = ""

    @SerializedName("content_type")
    var contentType: String = ""
}

open class GetFavoriteTeamRequest : FavoriteTeamRequest() {
    @SerializedName("limit")
    var limit: Int = 50

    @SerializedName("title")
    var title: String = ""
}

open class DeleteFavoriteTeamRequest : FavoriteTeamRequest() {

    @SerializedName("title") // primary key
    var title: String = ""
}

class AddFavoriteTeamRequest : DeleteFavoriteTeamRequest() {

    @SerializedName("payload")
    var payload: String = ""
}
