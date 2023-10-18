package com.truedigital.core.domain.usecase.model

import com.google.gson.annotations.SerializedName

data class DataPinnedDomainsModel(
    @SerializedName("control_list")
    val controlList: List<String>? = arrayListOf(),
    @SerializedName("blackbox")
    val blackBox: List<String>? = arrayListOf(),
    @SerializedName("urls")
    val urls: List<String>? = arrayListOf()
)
