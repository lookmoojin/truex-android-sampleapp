package com.truedigital.common.share.componentv3.widget.badge.model

data class CountInboxModel(
    val totalUnseens: Int = 0,
    val categories: List<CategoriesInboxModel>? = null
)
