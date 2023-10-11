package com.truedigital.features.truecloudv3.extension

import android.content.Context
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.component.extension.formatEnTh
import com.truedigital.core.constant.DateFormatConstant
import com.truedigital.core.extensions.toDateByDefaultTimeZone
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.FileMimeTypeManager
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import java.io.File

fun TrueCloudV3StorageData?.convertToTrueCloudV3Model(context: ContextDataProviderWrapper? = null): TrueCloudV3Model {
    val trueCloudV3StorageData = this
    val createdAt = trueCloudV3StorageData?.createdAt?.toDateByDefaultTimeZone(
        DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss
    )?.formatEnTh(
        DateFormatConstant.dd_MMM_yy_HH_mm,
        DateFormatConstant.dd_MMM_yy_HH_mm
    )
    val deleteDate = trueCloudV3StorageData?.deleteDate?.toDateByDefaultTimeZone(
        DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss
    )?.formatEnTh(
        DateFormatConstant.dd_MMM_yy_HH_mm,
        DateFormatConstant.dd_MMM_yy_HH_mm
    )
    var deleteIn: String? = null
    if (context != null) {
        deleteIn = String.format(context.get().getString(R.string.true_cloudv3_delete_date), deleteDate)
    }
    return TrueCloudV3Model(
        id = trueCloudV3StorageData?.id,
        parentObjectId = trueCloudV3StorageData?.parentObjectId,
        objectType = trueCloudV3StorageData?.objectType,
        name = trueCloudV3StorageData?.name,
        size = trueCloudV3StorageData?.size,
        mimeType = trueCloudV3StorageData?.mimeType ?: "",
        category = trueCloudV3StorageData?.category,
        coverImageKey = trueCloudV3StorageData?.coverImageKey,
        coverImageSize = trueCloudV3StorageData?.coverImageSize,
        updatedAt = trueCloudV3StorageData?.updatedAt,
        createdAt = createdAt,
        isPrivate = trueCloudV3StorageData?.sharedObject?.isPrivate,
        deleteIn = deleteIn
    )
}

fun List<TrueCloudV3StorageData>?.convertToListTrueCloudV3Model(context: ContextDataProviderWrapper? = null):
    List<TrueCloudV3Model> {
    return this?.map { it.convertToTrueCloudV3Model(context) } ?: emptyList()
}

fun TrueCloudV3Model?.convertToFilesModel(context: Context): TrueCloudFilesModel.File {
    val trueCloudV3Model = this
    val fileMimeType = FileMimeTypeManager.getMimeType(trueCloudV3Model?.mimeType ?: "")
    val key = trueCloudV3Model?.id.orEmpty()
    val cacheDir =
        context.cacheDir.absolutePath + "/true_cloud_cache_full_data_preview"
    val fileData = File(cacheDir, "$key.jpg")
    return TrueCloudFilesModel.File(
        id = trueCloudV3Model?.id,
        parentObjectId = trueCloudV3Model?.parentObjectId,
        objectType = trueCloudV3Model?.objectType,
        name = trueCloudV3Model?.name,
        size = trueCloudV3Model?.size?.toLong()?.formatBinarySize(context),
        mimeType = trueCloudV3Model?.mimeType,
        category = trueCloudV3Model?.category,
        coverImageKey = trueCloudV3Model?.coverImageKey,
        coverImageSize = trueCloudV3Model?.coverImageSize,
        updatedAt = trueCloudV3Model?.updatedAt,
        createdAt = trueCloudV3Model?.createdAt,
        isPrivate = trueCloudV3Model?.isPrivate,
        fileMimeType = fileMimeType,
        fileData = fileData,
        deleteIn = trueCloudV3Model?.deleteIn
    )
}

fun TrueCloudV3Model?.convertToFolderModel(context: Context): TrueCloudFilesModel.Folder {
    val trueCloudV3Model = this
    return TrueCloudFilesModel.Folder(
        id = trueCloudV3Model?.id,
        parentObjectId = trueCloudV3Model?.parentObjectId,
        objectType = trueCloudV3Model?.objectType,
        name = trueCloudV3Model?.name,
        size = trueCloudV3Model?.size?.toLong()?.formatBinarySize(context),
        mimeType = trueCloudV3Model?.mimeType,
        category = trueCloudV3Model?.category,
        coverImageKey = trueCloudV3Model?.coverImageKey,
        coverImageSize = trueCloudV3Model?.coverImageSize,
        updatedAt = trueCloudV3Model?.updatedAt,
        createdAt = trueCloudV3Model?.createdAt,
        deleteIn = trueCloudV3Model?.deleteIn
    )
}

fun TrueCloudFilesModel.Folder?.convertFolderToFileModel(context: Context): TrueCloudFilesModel.File {
    val trueCloudFolderModel = this
    val fileMimeType = FileMimeTypeManager.getMimeType(trueCloudFolderModel?.mimeType ?: "")
    return TrueCloudFilesModel.File(
        id = trueCloudFolderModel?.id,
        parentObjectId = trueCloudFolderModel?.parentObjectId,
        objectType = trueCloudFolderModel?.objectType,
        name = trueCloudFolderModel?.name,
        size = trueCloudFolderModel?.size?.toLong()?.formatBinarySize(context),
        mimeType = trueCloudFolderModel?.mimeType,
        category = trueCloudFolderModel?.category,
        coverImageKey = trueCloudFolderModel?.coverImageKey,
        coverImageSize = trueCloudFolderModel?.coverImageSize,
        updatedAt = trueCloudFolderModel?.updatedAt,
        createdAt = trueCloudFolderModel?.createdAt,
        fileMimeType = fileMimeType,
        deleteIn = trueCloudFolderModel?.deleteIn
    )
}

fun TrueCloudFilesModel.File?.convertFileToFolder(context: Context): TrueCloudFilesModel.Folder {
    val trueCloudV3Model = this
    return TrueCloudFilesModel.Folder(
        id = trueCloudV3Model?.id,
        parentObjectId = trueCloudV3Model?.parentObjectId,
        objectType = trueCloudV3Model?.objectType,
        name = trueCloudV3Model?.name,
        size = trueCloudV3Model?.size?.toLong()?.formatBinarySize(context),
        mimeType = trueCloudV3Model?.mimeType,
        category = trueCloudV3Model?.category,
        coverImageKey = trueCloudV3Model?.coverImageKey,
        coverImageSize = trueCloudV3Model?.coverImageSize,
        updatedAt = trueCloudV3Model?.updatedAt,
        createdAt = trueCloudV3Model?.createdAt,
        deleteIn = trueCloudV3Model?.deleteIn
    )
}
fun TrueCloudFilesModel.AutoBackup.convertBackupToUpload(): TrueCloudFilesModel.Upload {
    val trueCloudV3Model = this
    return TrueCloudFilesModel.Upload(
        id = trueCloudV3Model.id,
        path = trueCloudV3Model.path,
        status = trueCloudV3Model.status,
        name = trueCloudV3Model.name,
        size = trueCloudV3Model.size,
        coverImageSize = trueCloudV3Model.coverImageSize,
        type = trueCloudV3Model.type,
        objectId = trueCloudV3Model.objectId,
        actionType = TaskActionType.AUTO_BACKUP,
        progress = trueCloudV3Model.progress,
        updateAt = trueCloudV3Model.updateAt,
        progressMessage = trueCloudV3Model.progressMessage
    )
}
