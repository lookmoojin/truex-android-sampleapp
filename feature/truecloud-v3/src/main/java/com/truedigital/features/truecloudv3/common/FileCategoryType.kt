package com.truedigital.features.truecloudv3.common

import androidx.annotation.DrawableRes
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import com.truedigital.features.truecloudv3.R

enum class FileCategoryType(
    val type: String,
    val title: String,
    val resTitle: Int,
    val mediaType: TrueCloudV3MediaType,
    val fileMimeType: FileMimeType,
    @DrawableRes val res: Int,
    @DrawableRes val resGrid: Int = 0
) {
    IMAGE(
        "IMAGE",
        "Images",
        R.string.true_cloudv3_menu_images,
        TrueCloudV3MediaType.TypeImage,
        FileMimeType.IMAGE,
        R.drawable.placeholder_true_cloudv3_images,
        R.drawable.placeholder_true_cloudv3_images
    ),
    VIDEO(
        "VIDEO",
        "Videos",
        R.string.true_cloudv3_menu_videos,
        TrueCloudV3MediaType.TypeVideo,
        FileMimeType.VIDEO,
        R.drawable.ic_place_holder_true_cloudv3_video_small,
        R.drawable.ic_place_holder_true_cloudv3_video
    ),
    AUDIO(
        "AUDIO",
        "Audios",
        R.string.true_cloudv3_menu_audio,
        TrueCloudV3MediaType.TypeAudio,
        FileMimeType.AUDIO,
        R.drawable.ic_place_holder_true_cloudv3_audio_small,
        R.drawable.ic_place_holder_true_cloudv3_audio
    ),
    CONTACT(
        "text/vcard",
        "Contacts",
        R.string.true_cloudv3_menu_contacts,
        TrueCloudV3MediaType.TypeContact,
        FileMimeType.CONTACT,
        R.drawable.placeholder_true_cloudv3_contact
    ),
    OTHER(
        "OTHER",
        "Others",
        R.string.true_cloudv3_menu_others,
        TrueCloudV3MediaType.TypeAllFile,
        FileMimeType.OTHER,
        R.drawable.ic_place_holder_true_cloudv3_file_small,
        R.drawable.ic_place_holder_true_cloudv3_file
    ),
    RECENT(
        IMAGE.type + "," + VIDEO.type + "," + AUDIO.type + "," + OTHER.type,
        "RECENT",
        R.string.true_cloudv3_menu_recent_upload,
        TrueCloudV3MediaType.TypeOther,
        FileMimeType.OTHER,
        R.drawable.placeholder_true_cloudv3_images,
        R.drawable.placeholder_true_cloudv3_images
    ),
    UNSUPPORTED_FORMAT(
        "UNSUPPORTED_FORMAT",
        "",
        R.string.true_cloudv3_menu_all_files,
        TrueCloudV3MediaType.TypeAllFile,
        FileMimeType.UNSUPPORTED_FORMAT,
        R.drawable.ic_place_holder_true_cloudv3_file_small,
        R.drawable.ic_place_holder_true_cloudv3_file
    )
}

object FileCategoryTypeManager {
    fun getCategoryType(type: String): FileCategoryType {
        return when (type.toUpperCase(Locale.current)) {
            FileCategoryType.IMAGE.type -> FileCategoryType.IMAGE
            FileCategoryType.VIDEO.type -> FileCategoryType.VIDEO
            FileCategoryType.AUDIO.type -> FileCategoryType.AUDIO
            FileCategoryType.CONTACT.type.toUpperCase(Locale.current) -> FileCategoryType.CONTACT
            FileCategoryType.OTHER.type -> FileCategoryType.OTHER
            FileCategoryType.RECENT.type -> FileCategoryType.RECENT
            else -> FileCategoryType.UNSUPPORTED_FORMAT
        }
    }
}
