package com.truedigital.features.truecloudv3.common

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.tdg.truecloud.R

enum class FileStorageType(
    val key: String,
    @ColorRes val typeColorRes: Int,
    @StringRes val textRes: Int,
    @DrawableRes val storageDrawableRes: Int
) {
    IMAGES_File_Storage_TYPE(
        "Images",
        R.color.true_cloudv3_progress_image_type,
        R.string.true_cloudv3_menu_images,
        R.drawable.ic_true_cloudv3_storage_image_type_color
    ),
    AUDIO_File_Storage_TYPE(
        "Audio",
        R.color.true_cloudv3_progress_audio_type,
        R.string.true_cloudv3_menu_audio,
        R.drawable.ic_true_cloudv3_storage_audio_type_color
    ),
    VIDEOS_File_Storage_TYPE(
        "Videos",
        R.color.true_cloudv3_progress_video_type,
        R.string.true_cloudv3_menu_videos,
        R.drawable.ic_true_cloudv3_storage_video_type_color
    ),
    CONTACTS_File_Storage_TYPE(
        "Contacts",
        R.color.true_cloudv3_progress_contact_type,
        R.string.true_cloudv3_menu_contacts,
        R.drawable.ic_true_cloudv3_storage_contact_type_color
    ),
    OTHERS_File_Storage_TYPE(
        "Others",
        R.color.true_cloudv3_progress_other_type,
        R.string.true_cloudv3_menu_others,
        R.drawable.ic_true_cloudv3_storage_other_type_color
    );

    companion object {
        fun getStorageType(type: String?): FileStorageType {
            return when (type) {
                FileStorageType.IMAGES_File_Storage_TYPE.key -> FileStorageType.IMAGES_File_Storage_TYPE
                FileStorageType.VIDEOS_File_Storage_TYPE.key -> FileStorageType.VIDEOS_File_Storage_TYPE
                FileStorageType.AUDIO_File_Storage_TYPE.key -> FileStorageType.AUDIO_File_Storage_TYPE
                FileStorageType.CONTACTS_File_Storage_TYPE.key -> FileStorageType.CONTACTS_File_Storage_TYPE
                else -> FileStorageType.OTHERS_File_Storage_TYPE
            }
        }
    }
}
