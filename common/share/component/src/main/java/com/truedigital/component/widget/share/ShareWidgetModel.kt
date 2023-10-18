package com.truedigital.component.widget.share

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShareWidgetModel(
    var slug: String = "",
    var id: String = "",
    var title: String = "",
    var detail: String = "",
    var thumbnail: String = "",
    var type: String = "",
    var apiUrl: String = "",
    var shareUrl: String = "",
    var deeplinkUrl: String = ""
) : Parcelable
