package com.truedigital.common.share.datalegacy.data.other.model.response.tv.schedules

import com.google.gson.annotations.SerializedName

class LiveChatSchedulesResponse(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("platform_module")
    val platformModule: Int? = null,

    @SerializedName("report_dashboard")
    val reportDashboard: Int? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("pages")
    val pages: LiveChatSchedulesPages? = null,

    @SerializedName("data")
    val data: List<LiveChatSchedulesData>? = null
)

class LiveChatSchedulesPages(
    @SerializedName("cursor")
    val cursor: Any? = null,

    @SerializedName("limit")
    val limit: Any? = null,

    @SerializedName("total_items")
    val total_items: Any? = null,

    @SerializedName("total_pages")
    val total_pages: Any? = null
)

class LiveChatSchedulesData(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("livetv_id")
    val livetv_id: String? = null,

    @SerializedName("program_name")
    val program_name: String? = null,

    @SerializedName("start_date")
    var start_date: String,

    @SerializedName("end_date")
    var end_date: String
)
