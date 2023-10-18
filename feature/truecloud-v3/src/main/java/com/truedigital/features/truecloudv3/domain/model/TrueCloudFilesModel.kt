package com.truedigital.features.truecloudv3.domain.model

import android.os.Parcelable
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import kotlinx.parcelize.Parcelize

sealed class TrueCloudFilesModel {
    data class Header(
        val title: String,
        val headerType: HeaderType
    ) : TrueCloudFilesModel()

    data class Upload(
        val id: Int,
        val path: String,
        var status: TaskStatusType,
        var name: String,
        var size: String,
        var coverImageSize: Int?,
        var type: FileMimeType,
        var objectId: String? = null,
        val actionType: TaskActionType = TaskActionType.UPLOAD,
        var progress: Long = 0,
        var updateAt: Long? = 0,
        var progressMessage: String = ""
    ) : TrueCloudFilesModel()

    data class Folder(
        var id: String? = null,
        var parentObjectId: String? = null,
        var objectType: String? = null,
        var name: String? = null,
        var size: String? = null,
        var mimeType: String? = null,
        var category: String? = null,
        var coverImageKey: String? = null,
        var coverImageSize: String? = null,
        var updatedAt: String? = null,
        var createdAt: String? = null,
        var isDeleted: Boolean = false,
        var deleteIn: String? = null
    ) : TrueCloudFilesModel()

    @Parcelize
    data class File(
        var id: String? = null,
        var parentObjectId: String? = null,
        var objectType: String? = null,
        var name: String? = null,
        var size: String? = null,
        var mimeType: String? = null,
        var category: String? = null,
        var coverImageKey: String? = null,
        var coverImageSize: String? = null,
        var updatedAt: String? = null,
        var createdAt: String? = null,
        var selected: Boolean? = false,
        var imageCoverAlready: Boolean = false,
        var isPrivate: Boolean? = null,
        var fileMimeType: FileMimeType = FileMimeType.UNSUPPORTED_FORMAT,
        var fileData: java.io.File? = null,
        var isDeleted: Boolean = false,
        var deleteIn: String? = null
    ) : TrueCloudFilesModel(), Parcelable

    data class AutoBackup(
        val id: Int,
        val path: String,
        var status: TaskStatusType,
        var name: String,
        var size: String,
        var coverImageSize: Int?,
        var type: FileMimeType,
        var objectId: String? = null,
        val actionType: TaskActionType = TaskActionType.AUTO_BACKUP,
        var progress: Long = 0,
        var updateAt: Long? = 0,
        var progressMessage: String = ""
    ) : TrueCloudFilesModel()
}

enum class HeaderType {
    UPLOAD,
    AUTO_BACKUP,
    FILE,
    OTHER
}
