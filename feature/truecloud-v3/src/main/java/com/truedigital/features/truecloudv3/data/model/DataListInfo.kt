package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class DataListInfo(
    @SerializedName("number_of_files")
    var numberOfFiles: Int = 0
)
