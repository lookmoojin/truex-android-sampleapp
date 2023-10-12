package com.truedigital.core.domain.usecase.model

import com.google.gson.annotations.SerializedName

data class DataRocketModel(
    @SerializedName("control_list")
    val controlList: ArrayList<String>? = arrayListOf(),
    @SerializedName("blackbox")
    val blackBox: ArrayList<String>? = arrayListOf()
)
