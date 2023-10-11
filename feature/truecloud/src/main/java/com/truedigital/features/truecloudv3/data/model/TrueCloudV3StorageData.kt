package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel

data class TrueCloudV3StorageData(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("user_id")
    var userId: String? = null,
    @SerializedName("parent_object_id")
    var parentObjectId: String? = null,
    @SerializedName("object_type")
    var objectType: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("size")
    var size: String? = null,
    @SerializedName("mime_type")
    var mimeType: String? = null,
    @SerializedName("category")
    var category: String? = null,
    @SerializedName("cover_image_key")
    var coverImageKey: String? = null,
    @SerializedName("cover_image_size")
    var coverImageSize: String? = null,
    @SerializedName("updated_at")
    var updatedAt: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("shared_object")
    var sharedObject: SharedObject? = null,
    @SerializedName("is_uploaded")
    var isUploaded: Boolean = false,
    @SerializedName("is_deleted")
    var isDeleted: Boolean = false,
    @SerializedName("delete_in")
    var deleteDate: String? = null
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
            isPrivate = sharedObject?.isPrivate
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
            createdAt = createdAt
        )
    }
}

data class SharedObject(
    @SerializedName("is_private")
    var isPrivate: Boolean = false,
)
