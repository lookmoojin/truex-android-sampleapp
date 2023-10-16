package com.truedigital.common.share.nativeshare.domain.model.dynamiclink

import androidx.annotation.StringRes

internal data class CreateDynamicLinkModel(
    val longUrl: String,
    val url: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    @StringRes val errorMegDisplay: Int
)
