package com.truedigital.features.truecloudv3.presentation.viewmodel

import androidx.exifinterface.media.ExifInterface
import com.truedigital.features.truecloudv3.R
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.provider.ExifProvider
import com.truedigital.features.truecloudv3.provider.FileProvider
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class FileInfoBottomSheetDialogViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val fileProvider: FileProvider,
    private val contextDataProviderWrapper: ContextDataProviderWrapper,
    private val exifProvider: ExifProvider
) : ScopedViewModel() {
    companion object {
        private const val MAX_SHOW_SIZE = 5
    }

    val onSetMaxHeightView = SingleLiveEvent<Unit>()
    val onSetUpData = SingleLiveEvent<List<Pair<String, String>>>()
    lateinit var trueCloudFilesModel: TrueCloudFilesModel.File
    fun onViewCreated(fileModel: TrueCloudFilesModel.File) {
        trueCloudFilesModel = fileModel
        onLoadInfo()
    }

    private fun onLoadInfo() {
        flow {
            emit(getFileInfoList(trueCloudFilesModel))
        }
            .mapNotNull { list -> list.filter { it.second.isNotEmpty() } }
            .onEach {
                if (it.size >= MAX_SHOW_SIZE) {
                    onSetMaxHeightView.value = Unit
                }
                onSetUpData.value = it
            }
            .flowOn(coroutineDispatcher.io())
            .launchSafeIn(this)
    }

    private fun getFileInfoList(fileModel: TrueCloudFilesModel.File): List<Pair<String, String>> {
        val infoList = mutableListOf<Pair<String, String>>()
        val key = fileModel.id.orEmpty()
        val name = fileModel.name.orEmpty()
        val size = fileModel.size.orEmpty()
        val titleName = contextDataProviderWrapper.get()
            .getString(R.string.true_cloudv3_dialog_file_info_title_name)
        val titleSize = contextDataProviderWrapper.get()
            .getString(R.string.true_cloudv3_dialog_file_info_title_size)
        infoList.add(Pair(titleName, name))
        val cacheDir =
            contextDataProviderWrapper.get()
                .getDataContext().cacheDir.absolutePath + "/true_cloud_cache_full_data_preview"
        val file = fileProvider.getFile(cacheDir)
        if (!file.exists()) {
            file.mkdir()
        }
        val fileData = fileProvider.getFile(file.path, "$key.jpg")
        if (fileData.exists()) {
            val exif = exifProvider.getExif(fileData.absolutePath)
            val datetime = exif.getAttribute(ExifInterface.TAG_DATETIME).orEmpty()
            val imageLength = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH).orEmpty()
            val imageWidth = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH).orEmpty()
            val model = exif.getAttribute(ExifInterface.TAG_MODEL).orEmpty()
            val focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH).orEmpty()
            val aperture = exif.getAttribute(ExifInterface.TAG_APERTURE_VALUE).orEmpty()
            val isoSpeed = exif.getAttribute(ExifInterface.TAG_ISO_SPEED).orEmpty()
            val flash = exif.getAttribute(ExifInterface.TAG_FLASH).orEmpty()
            val latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE).orEmpty()
            val longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE).orEmpty()
            val cameraOwnerName =
                exif.getAttribute(ExifInterface.TAG_CAMERA_OWNER_NAME).orEmpty()
            val imageDesc = exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION).orEmpty()
            val titleDateTime = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_file_info_title_datetime)
            val titleImageLength = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_file_info_title_image_length)
            val titleImageWidth = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_file_info_title_image_width)
            val titleDevice = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_file_info_title_device)
            val titleFocalLength = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_file_info_title_focal_length)
            val titleAperture = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_file_info_title_aperture)
            val titleISO = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_file_info_title_iso)
            val titleFlash = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_file_info_title_flash)
            val titleLatitude = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_file_info_title_latitude)
            val titleLongitude = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_file_info_title_longitude)
            val titleCameraOwnerName = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_file_info_title_camera_owner_name)
            val titleImageDesc = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_dialog_file_info_title_image_desc)
            infoList.add(Pair(titleDateTime, datetime))
            infoList.add(Pair(titleImageLength, imageLength))
            infoList.add(Pair(titleImageWidth, imageWidth))
            infoList.add(Pair(titleSize, size))
            infoList.add(Pair(titleDevice, model))
            infoList.add(Pair(titleFocalLength, focalLength))
            infoList.add(Pair(titleAperture, aperture))
            infoList.add(Pair(titleISO, isoSpeed))
            infoList.add(Pair(titleFlash, flash))
            infoList.add(Pair(titleLatitude, latitude))
            infoList.add(Pair(titleLongitude, longitude))
            infoList.add(Pair(titleCameraOwnerName, cameraOwnerName))
            infoList.add(Pair(titleImageDesc, imageDesc))
        } else {
            infoList.add(Pair(titleSize, size))
        }
        return infoList
    }
}
