package com.truedigital.features.truecloudv3.domain.model

data class HeaderSelectionModel(
    val key: String,
    val size: Int
) : Contact {

    override fun areContentsTheSame(newItem: ContactBaseModel): Boolean {
        return newItem is HeaderSelectionModel
    }

    override fun areItemsTheSame(newItem: ContactBaseModel): Boolean {
        return newItem is HeaderSelectionModel && key == newItem.key
    }
}
