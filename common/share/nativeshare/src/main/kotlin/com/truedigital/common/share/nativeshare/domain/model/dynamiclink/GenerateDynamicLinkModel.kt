package com.truedigital.common.share.nativeshare.domain.model.dynamiclink

import org.json.JSONObject

data class GenerateDynamicLinkModel(
    val title: String,
    val description: String,
    val imageUrl: String,
    val type: String,
    val slug: String,
    val info: JSONObject?,
    val shareUrl: String,
    val goToPlayStore: Boolean
)
