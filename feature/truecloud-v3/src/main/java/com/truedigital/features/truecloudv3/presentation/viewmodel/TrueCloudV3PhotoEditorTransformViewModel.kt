package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import com.canhub.cropper.CropImageView
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.truecloudv3.provider.FileProvider
import com.truedigital.foundation.extension.LiveEvent
import java.util.UUID
import javax.inject.Inject

class TrueCloudV3PhotoEditorTransformViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val fileProvider: FileProvider,
    private val contextDataProviderWrapper: ContextDataProviderWrapper,
) : ScopedViewModel() {

    val onGenerateUri: LiveData<Uri> get() = _onGenerateUri
    val onShowError: LiveData<String> get() = _onShowError
    val onCropComplete: LiveData<Uri> get() = _onCropComplete
    val onFlipImage: LiveData<Unit> get() = _onFlipImage
    val onRotateImage: LiveData<Unit> get() = _onRotateImage
    val onSetRotation: LiveData<Int> get() = _onSetRotation
    val onCropReset: LiveData<Unit> get() = _onCropReset
    val onSetCrop: LiveData<Pair<Int, Int>?> get() = _onSetCrop
    private val _onGenerateUri = LiveEvent<Uri>()
    private val _onShowError = LiveEvent<String>()
    private val _onCropComplete = LiveEvent<Uri>()
    private val _onFlipImage = LiveEvent<Unit>()
    private val _onRotateImage = LiveEvent<Unit>()
    private val _onSetRotation = LiveEvent<Int>()
    private val _onCropReset = LiveEvent<Unit>()
    private val _onSetCrop = LiveEvent<Pair<Int, Int>?>()

    fun onConfirmClick() {
        launchSafe(coroutineDispatcher.io()) {
            val cacheDir =
                contextDataProviderWrapper.get()
                    .getDataContext().cacheDir.absolutePath + "/true_cloud_cache_photo_editor"

            val file = fileProvider.getFile(cacheDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val fileData = fileProvider.getFile(file.path, "${UUID.randomUUID()}.jpg")
            _onGenerateUri.postValue(Uri.fromFile(fileData))
        }
    }

    fun onCropImageComplete(result: CropImageView.CropResult) {
        if (result.error != null) {
            val errorMessage = result.error?.message ?: ""
            _onShowError.value = errorMessage
        } else {
            result.uriContent?.let { uri ->
                _onCropComplete.value = uri
            }
        }
    }

    fun onFlipImage() {
        _onFlipImage.value = Unit
    }

    fun onRotateImage() {
        _onRotateImage.value = Unit
    }

    fun onSetRotation(rotation: Int) {
        _onSetRotation.value = rotation
    }

    fun onCropReset() {
        _onCropReset.value = Unit
    }

    fun onSetCrop(ratio: Pair<Int, Int>?) {
        _onSetCrop.value = ratio
    }
}
