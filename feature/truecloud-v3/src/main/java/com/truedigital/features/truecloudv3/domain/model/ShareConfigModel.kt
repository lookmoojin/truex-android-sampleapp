package com.truedigital.features.truecloudv3.domain.model

data class ShareConfigModel(
    val sharedFile: SharedFile
)

data class SharedFile(
    val id: String,
    val isPrivate: Boolean = false,
    val expireAt: String,
    val password: String,
    val updatedAt: String,
    val createdAt: String,
)
