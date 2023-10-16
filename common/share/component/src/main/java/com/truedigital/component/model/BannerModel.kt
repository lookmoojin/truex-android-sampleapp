package com.truedigital.component.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class BannerModel(
    var id: String = "",
    var thumb: String = "",
    var dynamicLink: String = ""
) : Parcelable
