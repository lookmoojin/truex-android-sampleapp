package com.truedigital.component.widget.livecommerce.data.model

import com.google.gson.annotations.SerializedName

data class CommerceActiveLiveStreamResponse(
    @SerializedName("count")
    var count: Int? = null,
    @SerializedName("data")
    var data: List<CommerceActiveLiveStreamData?> = listOf(),
    @SerializedName("max_page")
    var maxPage: Int? = null
)

data class CommerceActiveLiveStreamData(
    @SerializedName("posts")
    var posts: ArrayList<CommerceActiveLiveStreamPosts?> = arrayListOf(),
    @SerializedName("videoStreamings")
    var commerceVideoStreamings: ArrayList<CommerceActiveLiveStreamResponseVideoStreamings?> = arrayListOf(),
    @SerializedName("postId")
    var postId: String? = null,
    @SerializedName("users")
    var users: ArrayList<CommerceActiveLiveStreamResponseUsers?> = arrayListOf(),
    @SerializedName("streamId")
    var streamId: String? = null
)

data class CommerceActiveLiveStreamPosts(
    @SerializedName("postedUserId")
    var postedUserId: String? = null,
    @SerializedName("feedId")
    var feedId: String? = null,
    @SerializedName("targetId")
    var targetId: String? = null,
    @SerializedName("postId")
    var postId: String? = null
)

data class CommerceActiveLiveStreamResponseComponents(
    @SerializedName("streamName")
    var streamName: String? = null,
    @SerializedName("appName")
    var appName: String? = null,
    @SerializedName("origin")
    var origin: String? = null,
    @SerializedName("query")
    var query: String? = null
)

data class CommerceActiveLiveStreamResponseVideoStreamings(

    @SerializedName("streamId")
    var streamId: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("thumbnailFileId")
    var thumbnailFileId: String? = null,
    @SerializedName("isLive")
    var isLive: Boolean? = false,
    @SerializedName("status")
    var status: String? = null
)

data class CommerceActiveLiveStreamResponseUsers(
    @SerializedName("displayName")
    var displayName: String? = null
)
