package com.truedigital.common.share.datalegacy.data.repository.profile.model.response

import com.google.gson.annotations.SerializedName
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting

class ProfileResponse {
    @SerializedName("code")
    var code: Int? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("platform_module")
    var platformModule: Int? = null

    @SerializedName("report_dashboard")
    var reportDashboard: Int? = null

    @SerializedName("data")
    var data: ProfileData? = null

    @SerializedName("paging")
    var paging: Paging? = null
}

class Paging {
    @SerializedName("previous")
    var previous: String? = null
}

class ProfileData {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("ssoid")
    var ssoid: String? = null

    @SerializedName("appid")
    var appid: String? = null

    @SerializedName("settings")
    var settings: Setting? = null

    @SerializedName("content_id")
    var contentId: String? = null

    @SerializedName("content_lang")
    var contentLang: String? = null

    @SerializedName("action_type")
    var actionType: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("content_type")
    var contentType: String? = null

    @SerializedName("created_at")
    var createdAt: Any? = null

    @SerializedName("created_ip")
    var createdIp: String? = null

    @SerializedName("payload")
    var payload: String? = null

    @SerializedName("metadata")
    var metadata: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("pretty_date")
    var prettyDate: String? = null

    @SerializedName("thumb")
    var thumb: String? = null

    @SerializedName("avatar")
    var avatar: String? = null

    @SerializedName("display_name")
    var displayName: String? = null

    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("username")
    var userName: String? = null

    @SerializedName("bio")
    var bio: String? = null

    @SerializedName("cover")
    var cover: CoverData? = null

    @SerializedName("iconborder")
    var iconBorder: String? = null

    @SerializedName("social_configure_tab")
    var socialConfigureTab: List<SocialConfigureTab>? = null

    @SerializedName("social_settings")
    var socialSettings: SocialSettings? = null

    @SerializedName("activities")
    var activities: List<Activity>? = null

    @SerializedName("segment")
    var segment: SegmentData? = null
}

class Activity {
    @SerializedName("refid")
    var refId: String? = null
}

class CoverData {
    @SerializedName("small")
    var small: String? = null

    @SerializedName("medium")
    var medium: String? = null

    @SerializedName("large")
    var large: String? = null
}

class SocialConfigureTab {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("order")
    var order: Int? = null

    @SerializedName("enable")
    var enable: Boolean? = null
}

class SocialSettings {
    @SerializedName("truecard_display")
    var truecardDisplay: Boolean? = null

    @SerializedName("badge_display")
    var badgeDisplay: Boolean? = null

    @SerializedName("chat_display")
    var chatDisplay: Boolean? = null
}

class SegmentData {
    @SerializedName("trueid_persona")
    var segmentPersonaHashMap: Map<String, String>? = null
}
