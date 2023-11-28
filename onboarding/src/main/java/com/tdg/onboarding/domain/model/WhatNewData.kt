package com.tdg.onboarding.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WhatNewData(
    val imageMobile: String = "",
    val imageTablet: String = "",
    val type: WhatNewType = WhatNewType.NONE,
    val url: String = ""
) : Parcelable

enum class WhatNewType(val value: String) {
    NONE(""),
    WEBVIEW("webview")
}
