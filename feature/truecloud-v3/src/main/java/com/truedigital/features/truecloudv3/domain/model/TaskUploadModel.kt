package com.truedigital.features.truecloudv3.domain.model

import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType

data class TaskUploadModel(
    var id: Int,
    val path: String,
    var status: TaskStatusType,
    var name: String,
    var size: String,
    var coverImageSize: Int? = null,
    var type: FileMimeType,
    var objectId: String? = null,
    var actionType: TaskActionType = TaskActionType.UPLOAD,
    var progress: Long = 0,
    var updateAt: Long = 0L,
    var folderId: String = "",
    var progressMessage: String = ""
) {
    fun getUploadFilesModel(): TrueCloudFilesModel.Upload {
        return TrueCloudFilesModel.Upload(
            id = id,
            path = path,
            status = status,
            name = name,
            size = size,
            coverImageSize = coverImageSize,
            type = type,
            objectId = objectId,
            actionType = actionType,
            progress = progress,
            updateAt = updateAt,
            progressMessage = progressMessage
        )
    }

    fun getAutoBackupFileModel(): TrueCloudFilesModel.AutoBackup {
        return TrueCloudFilesModel.AutoBackup(
            id = id,
            path = path,
            status = status,
            name = name,
            size = size,
            coverImageSize = coverImageSize,
            type = type,
            objectId = objectId,
            actionType = actionType,
            progress = progress,
            updateAt = updateAt,
            progressMessage = progressMessage
        )
    }
}
