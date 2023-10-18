package com.truedigital.features.truecloudv3.domain.usecase

import android.net.Uri
import android.provider.MediaStore
import com.truedigital.core.extensions.convertToDate
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.presentation.viewmodel.AutoBackupViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

interface ScanFileUseCase {
    fun execute(
        contentList: ArrayList<String>,
        lastTimeStamp: Long
    ): Flow<MutableList<Uri>>
}

class ScanFileUseCaseImpl @Inject constructor(
    private val contextDataProvider: ContextDataProvider
) : ScanFileUseCase {

    companion object {
        private const val MEDIA_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE
        private const val IMAGE_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
        private const val VIDEO_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
        private const val AUDIO_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO
        private const val OTHER_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE_NONE
        private const val SORT_ORDER = "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
    }

    override fun execute(
        contentList: ArrayList<String>,
        lastTimeStamp: Long
    ): Flow<MutableList<Uri>> = flow {
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DATE_MODIFIED
        )
        val selectionList = arrayListOf<String>()
        var imageContent = ""
        var videoContent = ""
        var audioContent = ""
        var otherContent = ""
        contentList.forEach {
            when {
                it.contains(AutoBackupViewModel.imageBackup) -> {
                    imageContent = "$MEDIA_TYPE == $IMAGE_TYPE"
                    selectionList.add(imageContent)
                }

                it.contains(AutoBackupViewModel.videoBackup) -> {
                    videoContent = "$MEDIA_TYPE == $VIDEO_TYPE"
                    selectionList.add(videoContent)
                }

                it.contains(AutoBackupViewModel.audioBackup) -> {
                    audioContent = "$MEDIA_TYPE == $AUDIO_TYPE"
                    selectionList.add(audioContent)
                }

                it.contains(AutoBackupViewModel.otherBackup) -> {
                    otherContent = "$MEDIA_TYPE == $OTHER_TYPE"
                    selectionList.add(otherContent)
                }
            }
        }
        val cursor = contextDataProvider.getDataContext().contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            if (selectionList.size > 1) getMultiSelection(selectionList) else selectionList[0],
            null,
            SORT_ORDER
        )
        val newFiles = mutableListOf<Uri>()
        cursor?.use {
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            while (it.moveToNext()) {
                val filePath = it.getString(dataColumn)
                val fileModifiedDate = File(filePath).lastModified().convertToDate()
                // Check if this file is new (e.g., created/modified after your last backup).
                if (fileModifiedDate.after(lastTimeStamp.convertToDate())) {
                    newFiles.add(Uri.fromFile(File(filePath)))
                }
            }
        }
        emit(newFiles)
    }

    private fun getMultiSelection(selectionList: ArrayList<String>): String {
        val multiSelection = StringBuilder()
        if (selectionList.size > 1) {
            multiSelection.append(selectionList[0])
            for (index in 1 until selectionList.size) {
                multiSelection.append(" ")
                multiSelection.append("OR")
                multiSelection.append(" ")
                multiSelection.append(selectionList[index])
            }
        }
        return multiSelection.toString()
    }
}
