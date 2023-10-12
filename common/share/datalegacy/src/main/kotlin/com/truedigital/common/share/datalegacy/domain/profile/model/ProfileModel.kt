package com.truedigital.common.share.datalegacy.domain.profile.model

import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.CoverData

data class ProfileModel(
    var ssoId: String = "",
    var avatarUrl: String = "",
    var displayName: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var userName: String = "",
    var language: String = "",
    var notifyOrder: String = "",
    var languageNotify: String = "",
    var bio: String = "",
    var cover: CoverData? = null,
    var profileId: String = "",
    var iconBorder: String = "",
    var socialConfigureTab: List<SocialConfigureTabModel>? = null,
    var socialSettings: SocialSettingsModel? = null,
    var segment: Map<String, String>? = null
)
