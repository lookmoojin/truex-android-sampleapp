package com.truedigital.features.truecloudv3.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SortType(val type: String) : Parcelable {
    SORT_DATE_ASC("DATE_ASC"),
    SORT_DATE_DESC("DATE_DESC"),
    SORT_NAME_ASC("NAME_ASC"),
    SORT_NAME_DESC("NAME_DESC"),
    SORT_SIZE_ASC("SIZE_ASC"),
    SORT_SIZE_DESC("SIZE_DESC")
}
