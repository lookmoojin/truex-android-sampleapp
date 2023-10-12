package com.truedigital.features.truecloudv3.domain.model

data class TrueCloudV3Model(
    var id: String? = null,
    var parentObjectId: String? = null,
    var objectType: String? = null,
    var name: String? = null,
    var size: String? = null,
    var mimeType: String = "",
    var category: String? = null,
    var coverImageKey: String? = null,
    var coverImageSize: String? = null,
    var updatedAt: String? = null,
    var createdAt: String? = null,
    var isPrivate: Boolean? = null,
    var deleteIn: String? = null
) {
    fun getFilesModel(): TrueCloudFilesModel.File {
        return TrueCloudFilesModel.File(
            id = id,
            parentObjectId = parentObjectId,
            objectType = objectType,
            name = name,
            size = size,
            mimeType = mimeType,
            category = category,
            coverImageKey = coverImageKey,
            coverImageSize = coverImageSize,
            updatedAt = updatedAt,
            createdAt = createdAt,
            isPrivate = isPrivate,
            deleteIn = deleteIn
        )
    }

    fun getFolderModel(): TrueCloudFilesModel.Folder {
        return TrueCloudFilesModel.Folder(
            id = id,
            parentObjectId = parentObjectId,
            objectType = objectType,
            name = name,
            size = size,
            mimeType = mimeType,
            category = category,
            coverImageKey = coverImageKey,
            coverImageSize = coverImageSize,
            updatedAt = updatedAt,
            createdAt = createdAt,
            deleteIn = deleteIn
        )
    }
}
