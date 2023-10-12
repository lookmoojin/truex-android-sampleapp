package com.truedigital.features.truecloudv3.common

import androidx.annotation.DrawableRes
import com.tdg.truecloud.R

enum class FileMimeType(
    val type: String,
    val mimeType: String,
    @DrawableRes val res: Int = 0,
    @DrawableRes val resGrid: Int = 0
) {
    IMAGE(
        "image",
        "image/*",
        R.drawable.placeholder_true_cloudv3_images,
        R.drawable.placeholder_true_cloudv3_images
    ),
    VIDEO(
        "video",
        "video/*",
        R.drawable.placeholder_true_cloudv3_videos,
        R.drawable.placeholder_true_cloudv3_videos
    ),
    AUDIO(
        "audio",
        "audio/*",
        R.drawable.placeholder_true_cloudv3_audio,
        R.drawable.placeholder_true_cloudv3_audio
    ),
    CONTACT(
        "text/vcard",
        "text/vcard"
    ),
    OTHER(
        "other",
        "*/*",
        R.drawable.placeholder_true_cloudv3_files,
        R.drawable.placeholder_true_cloudv3_files
    ),
    UNSUPPORTED_FORMAT(
        "UNSUPPORTED_FORMAT",
        "*/*",
        R.drawable.placeholder_true_cloudv3_files,
        R.drawable.placeholder_true_cloudv3_files
    )
}

object FileMimeTypeManager {
    fun getMimeType(mimeType: String): FileMimeType {
        return when {
            mimeType == FileMimeType.CONTACT.type -> FileMimeType.CONTACT
            mimeType.startsWith("image") -> FileMimeType.IMAGE
            mimeType.startsWith("video") -> FileMimeType.VIDEO
            mimeType.startsWith("audio") -> FileMimeType.AUDIO
            else -> FileMimeType.OTHER
        }
    }
}
