package com.truedigital.common.share.datalegacy.data.other.model.response

/**
 * Created by phongsathon on 24/9/2018 AD.
 */

class GroupProfile {
    var group_id: String? = ""
    var profile: List<RolesProfile>? = null
    var role: String? = ""
}

class RolesProfile {
    var color: String? = ""
    var crown_no: Int? = null
    var custom: String? = ""
    var default_bg: String? = ""
    var id: String? = ""
    var display_name_color: String? = ""
    var font_awesome_color: String? = ""
    var badge_description_en: String? = null
    var badge_description_th: String? = null
    var badge_description_local: String? = null
    var badge_title_en: String? = null
    var badge_title_th: String? = null
    var badge_title_local: String? = null
    var badge_icon: String? = null
}
