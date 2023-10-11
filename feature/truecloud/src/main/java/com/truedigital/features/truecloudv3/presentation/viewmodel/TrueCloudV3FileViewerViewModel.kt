package com.truedigital.features.truecloudv3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.usecase.GetObjectInfoUseCase
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class TrueCloudV3FileViewerViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val getObjectInfoUseCase: GetObjectInfoUseCase,
) : ScopedViewModel() {

    private val _onShowPreview = MutableLiveData<String>()
    val onShowPreview: LiveData<String> get() = _onShowPreview
    private lateinit var trueCloudFilesModel: TrueCloudFilesModel.File

    fun setObjectFile(fileModel: TrueCloudFilesModel.File) {
        trueCloudFilesModel = fileModel
        val key = fileModel.id.orEmpty()
        getObjectInfoUseCase.execute(key).flowOn(coroutineDispatcher.io())
            .onEach {
                _onShowPreview.value = it.fileUrl.orEmpty()
            }
            .launchSafeIn(this)
    }
}
