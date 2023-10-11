package com.truedigital.features.truecloudv3.domain.model

data class ContactDataModel(
    val id: String,
    val userId: String? = null,
    val objectType: String? = null,
    val parentObjectId: String? = null,
    val name: String? = null,
    val mimeType: String? = null,
    val coverImageKey: String? = null,
    val coverImageSize: String? = null,
    val category: String? = null,
    val size: String? = null,
    val checksum: String? = null,
    val isUploaded: Boolean = false,
    val deviceId: String? = null,
    val updatedAt: String = "",
    val createdAt: String? = null,
    val lastModified: String? = null
)
