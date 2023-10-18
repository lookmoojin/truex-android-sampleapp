package com.truedigital.common.share.datalegacy.data.followteam.model

import com.google.gson.annotations.SerializedName

data class FollowTeamRequest(
    @SerializedName("appid")
    val appId: String = "",

    @SerializedName("action_type")
    val actionType: String = "",

    @SerializedName("content_type")
    val contentType: String = "",

    @SerializedName("refid")
    val refId: String = "",

    @SerializedName("title")
    val title: String = "",

    @SerializedName("payload")
    val payload: String = ""
)
