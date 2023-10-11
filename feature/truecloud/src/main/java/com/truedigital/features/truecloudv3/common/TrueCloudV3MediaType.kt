package com.truedigital.features.truecloudv3.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class TrueCloudV3MediaType(
    val mediaType: String,
    val mimeType: Array<String>
) : Parcelable {
    @Parcelize
    object TypeImage : TrueCloudV3MediaType(
        "picture",
        arrayOf("image/*")
    )

    @Parcelize
    object TypeVideo :
        TrueCloudV3MediaType(
            "video",
            arrayOf("video/*")
        )

    @Parcelize
    object TypeAudio :
        TrueCloudV3MediaType(
            "audio",
            arrayOf("audio/*")
        )

    @Parcelize
    object TypeFile : TrueCloudV3MediaType(
        "file",
        arrayOf(
            "application/*",
            "font/*",
            "message/*",
            "model/*",
            "multipart/*",
            "text/*"
        )
    )

    @Parcelize
    object TypeAllFile :
        TrueCloudV3MediaType("all_file", arrayOf("*/*"))

    @Parcelize
    object TypeContact :
        TrueCloudV3MediaType("contact", arrayOf())

    @Parcelize
    object TypeOther :
        TrueCloudV3MediaType("null", arrayOf())
}
