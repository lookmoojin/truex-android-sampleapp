package com.truedigital.common.share.datalegacy.data.repository.profile.model.request

import com.google.gson.annotations.SerializedName

class UpdateSettingProfileRequest {
    @SerializedName("lang_subtitle")
    var langSubtitle: String? = null

    @SerializedName("lang_audio")
    var langAudio: String? = null

    // For update tv audio across multi platform
    @SerializedName("lang_locale")
    var langLocale: String? = null

    @SerializedName("voice_commentary")
    var voiceCommentary: String? = null
}
