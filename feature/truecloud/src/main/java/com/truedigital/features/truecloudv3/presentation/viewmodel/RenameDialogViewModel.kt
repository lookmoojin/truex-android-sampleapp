package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.truedigital.core.base.ScopedViewModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject

class RenameDialogViewModel @Inject constructor() : ScopedViewModel() {

    val onSetUpView = SingleLiveEvent<TrueCloudFilesModel.File>()
    val onSetFileName = SingleLiveEvent<TrueCloudFilesModel.File>()
    var trueCloudFilesModel: TrueCloudFilesModel.File? = null
    fun onViewCreated(fileModel: TrueCloudFilesModel.File) {
        trueCloudFilesModel = fileModel
        onSetUpView.value = fileModel
    }
    fun onClickRename(filename: String) {
        filename.isNotEmpty().let {
            trueCloudFilesModel?.name = filename
            onSetFileName.value = trueCloudFilesModel
        }
    }
}
