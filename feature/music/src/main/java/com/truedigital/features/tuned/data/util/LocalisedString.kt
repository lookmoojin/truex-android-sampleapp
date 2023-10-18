package com.truedigital.features.tuned.data.util

import android.content.res.Resources
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.truedigital.foundation.extension.getLocal
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalisedString(
    @SerializedName("Language", alternate = ["language"])
    var language: String = Resources.getSystem().getLocal().toString().lowercase(),

    @SerializedName("Value", alternate = ["value"])
    var value: String?
) : Parcelable
